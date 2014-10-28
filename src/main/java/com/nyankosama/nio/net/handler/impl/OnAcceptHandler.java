package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;

import java.io.IOException;
import java.nio.channels.*;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class OnAcceptHandler extends AbstractSelectorHandler{
    private SelectorHandler onMessageHandler;
    private NetCallback onAcceptCallback;

    public OnAcceptHandler(Selector selector) {
        this(null, null);
    }

    public OnAcceptHandler(SelectorHandler onMessageHandler) {
        this(onMessageHandler, null);
    }

    public OnAcceptHandler(SelectorHandler onMessageHandler, NetCallback onAcceptCallback) {
        this.onMessageHandler = onMessageHandler;
        this.onAcceptCallback = onAcceptCallback;
    }

    @Override
    public void process(SelectionKey key) throws IOException {
        if (onMessageHandler == null) return;
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        onMessageHandler.registerTask(socketChannel);
        if (onAcceptCallback != null) {
            TcpConnection tcpConnection = new TcpConnection(socketChannel, key);
            tcpConnection.reset(socketChannel, key);
            onAcceptCallback.onAccept(tcpConnection);
        }
    }
}
