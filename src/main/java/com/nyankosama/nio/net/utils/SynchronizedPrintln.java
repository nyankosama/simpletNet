package com.nyankosama.nio.net.utils;

/**
 * Created by hlr@superid.cn on 2014/10/27.
 */
public class SynchronizedPrintln {

    public synchronized static void println(String str) {
        System.out.println(str);
    }
}
