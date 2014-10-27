package com.nyankosama.nio.net;

import com.nyankosama.nio.net.utils.ByteBufferThreadLocal;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */

public class TcpConnection {
    private SocketChannel channel;
    private SelectionKey selectionKey;

    public TcpConnection(SocketChannel channel, SelectionKey key) {
        this.channel = channel;
        this.selectionKey = key;
    }

    public final void reset(SocketChannel channel, SelectionKey selectionKey){
        this.channel = channel;
        this.selectionKey = selectionKey;
    }

    public final void send(String msg){
        ByteBuffer buffer = ByteBufferThreadLocal.getInstance().get();
        resetBufferForWrite(buffer, msg.getBytes());
        try {
            channel.write(buffer);
        } catch (IOException e) {
            //FIXME 异常处理
            e.printStackTrace();
        }
    }

    public final void send(byte[] bytes){
        ByteBuffer buffer = ByteBufferThreadLocal.getInstance().get();
        resetBufferForWrite(buffer, bytes);
        try {
            channel.write(buffer);
        } catch (IOException e) {
            //FIXME 异常处理
            e.printStackTrace();
        }
    }

    public final void close() {
        selectionKey.cancel();
        try {
            channel.close();
        } catch (IOException e) {
            //FIXME 异常处理
            e.printStackTrace();
        }
    }

    private void resetBufferForWrite(ByteBuffer buffer, byte[] bytes) {
        buffer.clear();
        buffer.put(bytes);
        buffer.flip();
    }
}
