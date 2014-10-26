package com.nyankosama.nio.net.utils;

import java.nio.ByteBuffer;

/**
 * Created by hlr@superid.cn on 2014/10/26.
 */
public class ObjBufBindWrap {
    //NOTE 由于reflectasm无法refer static method，因此这里通过此类代替一些static method

    private static ObjBufBindWrap instance = new ObjBufBindWrap();
    private ObjBufBindWrap() {}

    public static ObjBufBindWrap getInstance() {
        return instance;
    }

    public ByteBuffer constructByteBuffer(int capacity) {
        return ByteBuffer.allocate(capacity);
    }

    public void resetByteBuffer(ByteBuffer buffer) {
        buffer.clear();
    }
}
