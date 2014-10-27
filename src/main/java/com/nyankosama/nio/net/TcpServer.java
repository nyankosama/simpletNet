package com.nyankosama.nio.net;

import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;
import com.nyankosama.nio.net.handler.impl.OnAcceptHandler;
import com.nyankosama.nio.net.handler.impl.OnConnectHandler;
import com.nyankosama.nio.net.handler.impl.OnMessageHandler;
import com.nyankosama.nio.net.utils.BindFunction;
import com.nyankosama.nio.net.utils.CommonUtils;
import com.nyankosama.nio.net.utils.SynchronizedPrintln;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class TcpServer {

    private int port;

    private Selector selector;

    private volatile boolean isStop = false;

    private NetCallback callback;

    private InnerWorkThread workThreads[];

    private static final int WORK_QUEUE_CAPACITY = 100;
    private static final int WORK_THREAD_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    public TcpServer(int port) {
        this.port = port;
    }

    public void setCallback(NetCallback callback) {
        this.callback = callback;
    }

    private void initServer() {
        workThreads = new InnerWorkThread[WORK_THREAD_SIZE];
        for (int i = 0; i < WORK_THREAD_SIZE; i++) {
            workThreads[i] = new InnerWorkThread();
            workThreads[i].start();
        }

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress("0.0.0.0", port));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();

            int selectionKeyOp = SelectionKey.OP_ACCEPT;
            SelectorHandler onAcceptHandler = null;
            SelectorHandler onConnectHandler = null;
            SelectorHandler onMessageHandler = null;
            if (callback != null) {
                if (CommonUtils.decideCallbackOverride(callback, NetCallback.ON_CONNECT)) {
                    selectionKeyOp |= SelectionKey.OP_CONNECT;
                    onConnectHandler = new OnConnectHandler(callback);
                }
                if (CommonUtils.decideCallbackOverride(callback, NetCallback.ON_MESSAGE)) {
                    onMessageHandler = new OnMessageHandler(callback);
                }
                if (CommonUtils.decideCallbackOverride(callback, NetCallback.ON_ACCET)) {
                    onAcceptHandler = new OnAcceptHandler(selector, onMessageHandler, callback);
                } else {
                    onAcceptHandler = new OnAcceptHandler(selector, onMessageHandler);
                }
            } else {
                onAcceptHandler = new OnAcceptHandler(selector);
            }

            if (onConnectHandler != null)
                serverSocketChannel.register(selector, SelectionKey.OP_CONNECT, onConnectHandler);
            serverSocketChannel.register(selector, selectionKeyOp, onAcceptHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        //NOTE 单线程accept，多工作线程处理handler，通过round robin的方式决定处理工作线程
        SynchronizedPrintln.println("start server");
        initServer();
        int curIndex = workThreads.length - 1;
        int maxSize = workThreads.length;
        try {
            while (!isStop) {
                int ready = selector.select();
                if (ready == 0) continue;
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    SelectorHandler handler = (SelectorHandler) key.attachment();
                    if (handler != null){
                        BindFunction function = BindFunction.bind(handler, "process", key);
                        workThreads[curIndex = roundRobinIndex(curIndex, maxSize)].putWork(function);
                    }
                    iterator.remove();
                }
//                for (SelectionKey key : keys) {
//                    SelectorHandler handler = (SelectorHandler) key.attachment();
//                    if (handler != null){
//                        BindFunction function = BindFunction.bind(handler, "process", key);
//                        workThreads[curIndex = roundRobinIndex(curIndex, maxSize)].putWork(function);
//                    }
//                }
//                //FIXME 没有使用iterator.remove可能会存在问题
//                keys.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void stopServer() {
        SynchronizedPrintln.println("stop server");
        this.isStop = true;
    }

    private int roundRobinIndex(int curIndex, int maxSize) {
        if (curIndex == maxSize - 1) return 0;
        return curIndex + 1;
    }

    private static class InnerWorkThread extends Thread {
        private BlockingQueue<BindFunction> workQueue;

        public InnerWorkThread() {
            this.workQueue = new ArrayBlockingQueue<>(WORK_QUEUE_CAPACITY);
        }

        public void putWork(BindFunction function) {
            SynchronizedPrintln.println("put work!. thread=" + Thread.currentThread().getName());
            try {
                workQueue.put(function);
            } catch (InterruptedException e) {
                //NOTE 忽略中断
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                BindFunction function = workQueue.take();
                SynchronizedPrintln.println("take! thread=" + Thread.currentThread().getName());
                function.call();
            } catch (InterruptedException e) {
                //NOTE 忽略中断
                e.printStackTrace();
            }
        }
    }
}
