package com.nyankosama.nio.net;


import com.nyankosama.nio.net.callback.CallbackSupport;
import com.nyankosama.nio.net.callback.NetCallback;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public class Startup {
    public static void main(String args[]) {
        TcpServer server = new TcpServer(9123);
        NetCallback callback = new CallbackSupport() {
            @Override
            public void onMessage(TcpConnection connection, TcpBuffer buffer) {
                connection.send(buffer.retrieveAllAsString());
            }
        };
        server.setCallback(callback);
        server.startServer();
    }
}
