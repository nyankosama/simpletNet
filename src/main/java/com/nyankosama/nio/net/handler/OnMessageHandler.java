package com.nyankosama.nio.net.handler;

import java.nio.channels.SocketChannel;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public interface OnMessageHandler {
    public void onMessage(String fullMessage, SocketChannel channel);
}
