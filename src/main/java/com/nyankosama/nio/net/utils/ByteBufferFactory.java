package com.nyankosama.nio.net.utils;

import com.nyankosama.nio.net.TcpBuffer;

import java.nio.ByteBuffer;

/**
 * Created by hlr@superid.cn on 2014/10/26.
 */
public class ByteBufferFactory {
    //NOTE 由于reflectasm无法refer static method，因此这里通过此类代替一些static method

    private static ByteBufferFactory instance = new ByteBufferFactory();
    private static ObjectBuffer<ByteBuffer> objectBuffer;

    static {
        instance = new ByteBufferFactory();
        BindFunction<ByteBuffer> createBind =
                BindFunction.bind(instance, "constructByteBuffer", TcpBuffer.FIXED_BUFFER_SIZE);
        BindFunction resetBind = BindFunction.bind(instance, "resetByteBuffer");
        objectBuffer = new ObjectBuffer<>(createBind, resetBind);
    }


    private ByteBufferFactory() {}

    public static ByteBufferFactory getInstance() {
        return instance;
    }

    public static ObjectBuffer<ByteBuffer> getObjectBuffer() {
        return objectBuffer;
    }

    public ByteBuffer constructByteBuffer(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    public void resetByteBuffer(ByteBuffer buffer) {
        buffer.clear();
    }
}
