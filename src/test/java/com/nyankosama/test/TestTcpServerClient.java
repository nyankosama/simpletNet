package com.nyankosama.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class TestTcpServerClient {

    public static void main(String args[]) throws IOException {
        testClient();
    }

    public static void testClient() throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1", 9123));
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        long begin = System.currentTimeMillis();
        int num = 500000;
        for (int i = 0; i < num; i++) {
            writer.println("hello world!");
            writer.flush();
            reader.readLine();
        }
        socket.close();
        long end = System.currentTimeMillis();
        System.out.println("cost time:" + (end - begin) + " ms, qps:" + ((double)num / (end - begin) * 1000));
    }
}
