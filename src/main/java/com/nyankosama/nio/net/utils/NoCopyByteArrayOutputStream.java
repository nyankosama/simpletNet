package com.nyankosama.nio.net.utils;

import java.io.ByteArrayOutputStream;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public final class NoCopyByteArrayOutputStream extends ByteArrayOutputStream {

    public NoCopyByteArrayOutputStream() {
        super();
    }

    public NoCopyByteArrayOutputStream(int size) {
        super(size);
    }

    public final byte[] getBuf() {
        return buf;
    }
}
