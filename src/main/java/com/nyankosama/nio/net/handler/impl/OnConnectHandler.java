package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;
import com.nyankosama.nio.net.utils.ObjectBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public class OnConnectHandler implements SelectorHandler {

    private NetCallback callback;
    private ObjectBuffer<ByteBuffer> objectBuffer;

    public OnConnectHandler(NetCallback callback) {
        this.callback = callback;
    }

    @Override
    public void process(SelectionKey key) throws IOException {
//        System.out.println("on connect");
        if (callback != null) {
            TcpConnection tcpConnection = new TcpConnection((java.nio.channels.SocketChannel) key.channel(), key);
            callback.onConnect(tcpConnection);
        }
    }
}
