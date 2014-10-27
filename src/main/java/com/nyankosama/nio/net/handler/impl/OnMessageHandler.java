package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;
import com.nyankosama.nio.net.utils.BindFunction;
import com.nyankosama.nio.net.utils.ByteBufferThreadLocal;
import com.nyankosama.nio.net.utils.NoCopyByteArrayOutputStream;
import com.nyankosama.nio.net.utils.SynchronizedPrintln;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class OnMessageHandler implements SelectorHandler{

    private NetCallback callback;

    private InnerWorkThread workThreads[];

    private int curWorkIndex = 0;

    private static final int WORK_QUEUE_CAPACITY = 100;
    private static final int WORK_THREAD_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    public OnMessageHandler() {
        this(null);
    }

    public OnMessageHandler(NetCallback callback) {
        this.callback = callback;
        workThreads = new InnerWorkThread[WORK_THREAD_SIZE];
        for (int i = 0; i < WORK_THREAD_SIZE; i++) {
            workThreads[i] = new InnerWorkThread();
            workThreads[i].start();
        }
    }

    @Override
    public void process(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBufferThreadLocal.getInstance().get();
            NoCopyByteArrayOutputStream outputStream = new NoCopyByteArrayOutputStream(TcpBuffer.FIXED_BUFFER_SIZE);
//            SynchronizedPrintln.println("read");
            int read = channel.read(buffer);
//            SynchronizedPrintln.println("read down");
            if (read == -1) {
                //handle close
//                SynchronizedPrintln.println("on close");
                channel.close();
                key.cancel();
                return;
            }
//            SynchronizedPrintln.println("on message");
            do {
                outputStream.write(buffer.array());
                buffer.clear();
            }
            while ((read = channel.read(buffer)) != 0);
            if (callback != null) {
                TcpConnection tcpConnection = new TcpConnection(channel, key);
                //NOTE TcpBuffer和ByteArrayOutputStream共享一个buf
                TcpBuffer tcpBuffer = new TcpBuffer(outputStream.getBuf(), outputStream.size());
                BindFunction messageFunc = BindFunction.bind(callback, "onMessage", tcpConnection, tcpBuffer);
                workThreads[curWorkIndex = roundRobinIndex(curWorkIndex, WORK_THREAD_SIZE)].putWork(messageFunc);
//                SynchronizedPrintln.println("put work! threadIndex=" + curWorkIndex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            try {
                workQueue.put(function);
            } catch (InterruptedException e) {
                //NOTE 忽略中断
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true){
                try {
                    BindFunction function = workQueue.take();
                    function.call();
//                    SynchronizedPrintln.println("work down thread=" + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    //NOTE 忽略中断
                    e.printStackTrace();
                }
            }
        }
    }
}
