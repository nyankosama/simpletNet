package com.nyankosama.nio.net;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public class TcpBuffer {
    public static final int FIXED_BUFFER_SIZE = 1024;
    //FIXME 应该用对象池
    private byte[] bytes;
    private int size;

    public TcpBuffer(byte[] bytes, int size) {
        this.size = size;
        this.bytes = bytes;
    }

    private byte[] retrieveAllAsBytes() {
        return Arrays.copyOf(bytes, size);
    }

    public String retrieveAllAsString() {
        return new String(bytes, 0, size);
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
