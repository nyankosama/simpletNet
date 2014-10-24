package com.nyankosama.nio.net;

import com.nyankosama.nio.net.handler.OnMessageHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class Startup {
    public static void main(String args[]) {
        TcpServer server = new TcpServer(9123);
        server.setMessageHandler(new OnMessageHandler() {
            @Override
            public void onMessage(String fullMessage, SocketChannel channel) {
                ByteBuffer buffer = ByteBuffer.allocate(512);
                buffer.put(fullMessage.getBytes());
                buffer.flip();
                try {
                    channel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        server.startServer();
    }
}
