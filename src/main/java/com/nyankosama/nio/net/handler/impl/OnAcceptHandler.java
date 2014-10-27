package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class OnAcceptHandler implements SelectorHandler{
    private SelectorHandler onMessageHandler;
    private NetCallback onAcceptCallback;
    private Selector selector;

    public OnAcceptHandler(Selector selector) {
        this(selector, null, null);
    }

    public OnAcceptHandler(Selector selector, SelectorHandler onMessageHandler) {
        this(selector, onMessageHandler, null);
    }

    public OnAcceptHandler(Selector selector, SelectorHandler onMessageHandler, NetCallback onAcceptCallback) {
        this.selector = selector;
        this.onMessageHandler = onMessageHandler;
        this.onAcceptCallback = onAcceptCallback;
    }

    @Override
    public void process(SelectionKey key) throws IOException {
        if (onMessageHandler == null) return;
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ, onMessageHandler);
        if (onAcceptCallback != null) {
            TcpConnection tcpConnection = new TcpConnection(socketChannel, key);
            tcpConnection.reset(socketChannel, key);
            onAcceptCallback.onAccept(tcpConnection);
        }
    }
}
