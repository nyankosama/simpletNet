package com.nyankosama.nio.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class TestClientStartup {

    public static void main(String args[]) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: ./clientStartup.sh ip request-num");
            System.exit(1);
        }
        String ip = args[0];
        int requestNum = Integer.parseInt(args[1]);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(ip, 9123));
        System.out.println("connect");
        PrintWriter writer = new PrintWriter(socket.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        long begin = System.currentTimeMillis();
        int num = requestNum;
        for (int i = 0; i < num; i++) {
            writer.println("hello world!");
            writer.flush();
//            System.out.println("write");
            reader.readLine();
//            System.out.println("read");
        }
        socket.close();
        long end = System.currentTimeMillis();
        System.out.println("cost time:" + (end - begin) + " ms, qps:" + ((double) num / (end - begin) * 1000));
    }
}
