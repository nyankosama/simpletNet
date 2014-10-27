package com.nyankosama.nio.net;

import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;
import com.nyankosama.nio.net.handler.impl.OnAcceptHandler;
import com.nyankosama.nio.net.handler.impl.OnConnectHandler;
import com.nyankosama.nio.net.handler.impl.OnMessageHandler;
import com.nyankosama.nio.net.utils.CommonUtils;

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

    private Selector selector;

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
        System.out.println("start server");
        initServer();
        try {
            while (!isStop) {
                int ready = selector.select();
                if (ready == 0) continue;
//                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//                while (iterator.hasNext()) {
//                    SelectionKey key = iterator.next();
//                    SelectorHandler handler = (SelectorHandler) key.attachment();
//                    if (handler != null) handler.process(key);
//                    iterator.remove();
//                }
                Set<SelectionKey> keys = selector.selectedKeys();
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
        System.out.println("stop server");
        this.isStop = true;
    }
}
