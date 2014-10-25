package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class OnMessageHandler implements SelectorHandler{

    private NetCallback callback;

    public OnMessageHandler() {}

    public OnMessageHandler(NetCallback callback) {
        this.callback = callback;
    }

    @Override
    public void process(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            //FIXME 使用对象池
            ByteBuffer buffer = ByteBuffer.allocate(TcpBuffer.FIXED_BUFFER_SIZE);
            //FIXME 使用ByteArrayOutputStream，不知效率如何
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int read = channel.read(buffer);
            if (read == -1) {
                //handle close
                System.out.println("on close");
                channel.close();
                key.cancel();
                return;
            }
            System.out.println("on message");
            do {
                outputStream.write(buffer.array());
                buffer.clear();
            }
            while ((read = channel.read(buffer)) != 0);
            if (callback != null) {
                TcpConnection tcpConnection = new TcpConnection(channel, key);
                //FIXME 有一次arraycopy
                TcpBuffer tcpBuffer = new TcpBuffer(outputStream.toByteArray());
                callback.onMessage(tcpConnection, tcpBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
