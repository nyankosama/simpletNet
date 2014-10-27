package com.nyankosama.nio.net.utils;

import com.nyankosama.nio.net.TcpBuffer;

import java.nio.ByteBuffer;

/**
 * Created by hlr@superid.cn on 2014/10/27.
 */
public final class ByteBufferThreadLocal extends ThreadLocal<ByteBuffer> {
    private static ByteBufferThreadLocal instance = new ByteBufferThreadLocal();

    private ByteBufferThreadLocal(){}

    public static ByteBufferThreadLocal getInstance() {
        return instance;
    }

    @Override
    protected final ByteBuffer initialValue() {
//        System.out.println("initialValue thread=" + Thread.currentThread().getName());
        return ByteBuffer.allocate(TcpBuffer.FIXED_BUFFER_SIZE);
    }

    @Override
    public final ByteBuffer get() {
        ByteBuffer buffer = super.get();
        buffer.clear();
        return buffer;
    }
}
