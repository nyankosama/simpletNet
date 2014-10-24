package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;

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
            System.out.println("on message");
            SocketChannel channel = (SocketChannel) key.channel();
            //FIXME 使用对象池
            ByteBuffer buffer = ByteBuffer.allocate(TcpBuffer.FIXED_BUFFER_SIZE);
            //TODO Buffer连接
            StringBuilder builder = new StringBuilder();
            int read = channel.read(buffer);
            if (read == -1) {
                //handle close
                channel.close();
                key.cancel();
                continue;
            }
            do {
                builder.append(new String(buffer.array()));
                buffer.clear();
            }
            while ((read = channel.read(buffer)) != 0);
            if (messageHandler != null) messageHandler.onMessage(builder.toString(), channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
