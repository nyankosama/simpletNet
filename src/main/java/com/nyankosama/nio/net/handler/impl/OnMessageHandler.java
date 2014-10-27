package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.handler.SelectorHandler;
import com.nyankosama.nio.net.utils.ByteBufferThreadLocal;
import com.nyankosama.nio.net.utils.NoCopyByteArrayOutputStream;
import com.nyankosama.nio.net.utils.SynchronizedPrintln;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class OnMessageHandler implements SelectorHandler{

    private NetCallback callback;

    public OnMessageHandler() {
    }

    public OnMessageHandler(NetCallback callback) {
        this.callback = callback;
    }

    @Override
    public void process(SelectionKey key) {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBufferThreadLocal.getInstance().get();
            NoCopyByteArrayOutputStream outputStream = new NoCopyByteArrayOutputStream(TcpBuffer.FIXED_BUFFER_SIZE);
            int read = channel.read(buffer);
            if (read == -1) {
                //handle close
                SynchronizedPrintln.println("on close");
                channel.close();
                key.cancel();
                return;
            }
            SynchronizedPrintln.println("on message");
            do {
                outputStream.write(buffer.array());
                buffer.clear();
            }
            while ((read = channel.read(buffer)) != 0);
            if (callback != null) {
                TcpConnection tcpConnection = new TcpConnection(channel, key);
                //NOTE TcpBuffer和ByteArrayOutputStream共享一个buf
                TcpBuffer tcpBuffer = new TcpBuffer(outputStream.getBuf(), outputStream.size());
                callback.onMessage(tcpConnection, tcpBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
