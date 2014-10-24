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
    private NetCallback callback;
    private Selector selector;

    public OnAcceptHandler(Selector selector){
        this.selector = selector;
    }

    public OnAcceptHandler(Selector selector, NetCallback callback) {
        this.selector = selector;
        this.callback = callback;
    }

    @Override
    public void process(SelectionKey key) throws IOException {
        System.out.println("on accept");
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = channel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        TcpConnection tcpConnection = new TcpConnection(socketChannel, key);
        tcpConnection.reset(socketChannel, key);
        if (callback != null) callback.onAccept(tcpConnection);
    }
}
