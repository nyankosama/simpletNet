package com.nyankosama.nio.net;

import java.nio.ByteBuffer;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public class TcpBuffer {
    public static final int FIXED_BUFFER_SIZE = 1024;
    //FIXME 应该用对象池
    private ByteBuffer buffer;

    public TcpBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    private byte[] retrieveAllAsBytes() {
        return buffer.array();
    }

    public String retrieveAllAsString() {
        return new String(buffer.array());
    }

    public int readAsInt16() {
        throw new UnsupportedOperationException();
    }

    public byte[] retrieveAsBytes(int length) {
        throw new UnsupportedOperationException();
    }

    public int readableBytes() {
        throw new UnsupportedOperationException();
    }
}
