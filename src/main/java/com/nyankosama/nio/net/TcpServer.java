package com.nyankosama.nio.net;

import com.nyankosama.nio.net.handler.OnMessageHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class TcpServer {
    private OnMessageHandler messageHandler;

    private int port;

    private Selector selector;

    private volatile boolean isStop = false;

    public TcpServer(int port) {
        this.port = port;
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress("0.0.0.0", port));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        System.out.println("start server");
        try {
            while (!isStop) {
                int ready = selector.select();
                if (ready == 0) continue;
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    if (key.isAcceptable()) {
                        System.out.println("on accept");
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                        SocketChannel socketChannel = channel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        System.out.println("on message");
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(512);
                        StringBuilder builder = new StringBuilder();
                        int read = channel.read(buffer);
                        if (read == -1) {
                            //handle close
                            channel.close();
                            key.cancel();
                            continue;
                        }
                        do{
                            builder.append(new String(buffer.array()));
                            buffer.clear();
                        }
                        while ((read = channel.read(buffer)) != 0);
                        if (messageHandler != null) messageHandler.onMessage(builder.toString(), channel);
                    }
                }
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

    public void setMessageHandler(OnMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}
