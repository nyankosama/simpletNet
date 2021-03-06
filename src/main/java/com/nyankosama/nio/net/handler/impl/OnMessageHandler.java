package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;
import com.nyankosama.nio.net.utils.ByteBufferThreadLocal;
import com.nyankosama.nio.net.utils.NoCopyByteArrayOutputStream;

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
    private static final int WORK_THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;
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
            int read = channel.read(buffer);
            if (read == -1) {
                channel.close();
                key.cancel();
                return;
            }
            do {
                outputStream.write(buffer.array());
                buffer.clear();
            }
            while ((read = channel.read(buffer)) != 0);
            if (callback != null) {
                TcpConnection tcpConnection = new TcpConnection(channel, key);
                //NOTE TcpBuffer和ByteArrayOutputStream共享一个buf
                TcpBuffer tcpBuffer = new TcpBuffer(outputStream.getBuf(), outputStream.size());
                workThreads[curWorkIndex = roundRobinIndex(curWorkIndex, WORK_THREAD_SIZE)]
                        .putWork(new InnerWork(tcpBuffer, tcpConnection, callback));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int roundRobinIndex(int curIndex, int maxSize) {
        if (curIndex == maxSize - 1) return 0;
        return curIndex + 1;
    }

    private static class InnerWork {
        private TcpBuffer tcpBuffer;
        private TcpConnection tcpConnection;
        private NetCallback callback;

        private InnerWork(TcpBuffer tcpBuffer, TcpConnection tcpConnection, NetCallback callback) {
            this.tcpBuffer = tcpBuffer;
            this.tcpConnection = tcpConnection;
            this.callback = callback;
        }

        public void call() {
            callback.onMessage(tcpConnection, tcpBuffer);
        }
    }

    private static class InnerWorkThread extends Thread {
        private BlockingQueue<InnerWork> workQueue;

        public InnerWorkThread() {
            this.workQueue = new ArrayBlockingQueue<>(WORK_QUEUE_CAPACITY);
        }

        public void putWork(InnerWork work) {
            try {
                workQueue.put(work);
            } catch (InterruptedException e) {
                //NOTE 忽略中断
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true){
                try {
                    InnerWork work = workQueue.take();
                    work.call();
                } catch (InterruptedException e) {
                    //NOTE 忽略中断
                    e.printStackTrace();
                }
            }
        }
    }
}
