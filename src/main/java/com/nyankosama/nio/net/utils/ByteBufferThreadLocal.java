package com.nyankosama.nio.net.utils;

import com.nyankosama.nio.net.TcpBuffer;

import java.nio.ByteBuffer;

/**
 * Created by hlr@superid.cn on 2014/10/27.
 */
public class ByteBufferThreadLocal extends ThreadLocal<ByteBuffer> {
    private static ByteBufferThreadLocal instance = new ByteBufferThreadLocal();

    private ByteBufferThreadLocal(){}

    public static ByteBufferThreadLocal getInstance() {
        return instance;
    }

    @Override
    protected ByteBuffer initialValue() {
        return ByteBuffer.allocate(TcpBuffer.FIXED_BUFFER_SIZE);
    }

    @Override
    public ByteBuffer get() {
        ByteBuffer buffer = super.get();
        buffer.clear();
        return buffer;
    }
}
