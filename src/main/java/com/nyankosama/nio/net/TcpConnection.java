package com.nyankosama.nio.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */

//FIXME 这里应该用对象池。。
public class TcpConnection {
    private SocketChannel channel;
    private SelectionKey selectionKey;
    private ByteBuffer buffer;//FIXME 是否可以重用TcpBuffer中的ByteBuffer？

    public TcpConnection(SocketChannel channel, SelectionKey key) {
        this.channel = channel;
        this.selectionKey = key;
        //FIXME 应该使用对象池
        this.buffer = ByteBuffer.allocate(TcpBuffer.FIXED_BUFFER_SIZE);
    }

    public final void reset(SocketChannel channel, SelectionKey selectionKey){
        this.channel = channel;
        this.selectionKey = selectionKey;
    }

    public final void send(String msg){
        resetBufferForWrite(msg.getBytes());
        try {
            channel.write(buffer);
        } catch (IOException e) {
            //FIXME 异常处理
            e.printStackTrace();
        }
    }

    public final void send(byte[] bytes){
        resetBufferForWrite(bytes);
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

    private void resetBufferForWrite(byte[] bytes) {
        buffer.clear();
        buffer.put(bytes);
        buffer.flip();
    }
}
