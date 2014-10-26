package com.nyankosama.nio.net;

import com.nyankosama.nio.net.utils.ByteBufferFactory;
import com.nyankosama.nio.net.utils.ObjectBuffer;

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
    private ObjectBuffer<ByteBuffer> objectBuffer = ByteBufferFactory.getObjectBuffer();

    public TcpConnection(SocketChannel channel, SelectionKey key) {
        this.channel = channel;
        this.selectionKey = key;
    }

    public final void reset(SocketChannel channel, SelectionKey selectionKey){
        this.channel = channel;
        this.selectionKey = selectionKey;
    }

    public final void send(String msg){
        ByteBuffer buffer = objectBuffer.getObject();
        resetBufferForWrite(buffer, msg.getBytes());
        try {
            channel.write(buffer);
        } catch (IOException e) {
            //FIXME 异常处理
            e.printStackTrace();
        }
        objectBuffer.returnObject(buffer);
    }

    public final void send(byte[] bytes){
        ByteBuffer buffer = objectBuffer.getObject();
        resetBufferForWrite(buffer, bytes);
        try {
            channel.write(buffer);
        } catch (IOException e) {
            //FIXME 异常处理
            e.printStackTrace();
        }
        objectBuffer.returnObject(buffer);
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
