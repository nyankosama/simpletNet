package com.nyankosama.nio.net;

import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;
import com.nyankosama.nio.net.handler.impl.OnAcceptHandler;
import com.nyankosama.nio.net.handler.impl.OnConnectHandler;
import com.nyankosama.nio.net.handler.impl.OnMessageHandler;
import com.nyankosama.nio.net.utils.CommonUtils;
import com.nyankosama.nio.net.utils.SynchronizedPrintln;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class TcpServer {

    private int port;

    private Selector acceptSelector;

    private volatile boolean isStop = false;

    private NetCallback callback;

    public TcpServer(int port) {
        this.port = port;
    }

    public void setCallback(NetCallback callback) {
        this.callback = callback;
    }

    private void initServer() {

        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress("0.0.0.0", port));
            serverSocketChannel.configureBlocking(false);
            acceptSelector = Selector.open();

            SelectorHandler onAcceptHandler = null;
            SelectorHandler onConnectHandler = null;
            SelectorHandler onMessageHandler = null;
            if (callback != null) {
                if (CommonUtils.decideCallbackOverride(callback, NetCallback.ON_MESSAGE)) {
                    onMessageHandler = new OnMessageHandler(callback);
                }
                if (CommonUtils.decideCallbackOverride(callback, NetCallback.ON_ACCET)) {
                    onAcceptHandler = new OnAcceptHandler(acceptSelector, onMessageHandler, callback);
                } else {
                    onAcceptHandler = new OnAcceptHandler(acceptSelector, onMessageHandler);
                }
            } else {
                onAcceptHandler = new OnAcceptHandler(acceptSelector);
            }

            if (onConnectHandler != null)
                serverSocketChannel.register(acceptSelector, SelectionKey.OP_CONNECT, onConnectHandler);
            serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT, onAcceptHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        //NOTE 单线程accept，多工作线程处理handler，通过round robin的方式决定处理工作线程
        SynchronizedPrintln.println("start server");
        initServer();
        try {
            while (!isStop) {
                //NOTE 这里只处理Accept任务
                int ready = acceptSelector.select();
                if (ready == 0) continue;
                Set<SelectionKey> keys = acceptSelector.selectedKeys();
                for (SelectionKey key : keys) {
                    SelectorHandler handler = (SelectorHandler) key.attachment();
                    if (handler != null) handler.process(key);
                }
                //FIXME 没有使用iterator.remove可能会存在问题
                keys.clear();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void stopServer() {
        SynchronizedPrintln.println("stop server");
        this.isStop = true;
    }

}
