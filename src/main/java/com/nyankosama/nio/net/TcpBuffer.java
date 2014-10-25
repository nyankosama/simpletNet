package com.nyankosama.nio.net;

import java.nio.ByteBuffer;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public class TcpBuffer {
    public static final int FIXED_BUFFER_SIZE = 1024;
    //FIXME 应该用对象池
    private byte[] bytes;

    public TcpBuffer(byte[] bytes) {
        this.bytes = bytes;
    }

    private byte[] retrieveAllAsBytes() {
        return bytes;
    }

    public String retrieveAllAsString() {
        return new String(bytes);
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
