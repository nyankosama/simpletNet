package com.nyankosama.test;

import java.io.*;

/**
 * Created by hlr@superid.cn on 2014/10/22.
 */
public class JavaIOTest {

    public static void main(String args[]) throws FileNotFoundException {
        PushbackReader reader = new PushbackReader(new FileReader("1.txt"));
    }
}
