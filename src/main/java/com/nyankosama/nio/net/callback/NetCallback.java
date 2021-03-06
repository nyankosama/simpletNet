package com.nyankosama.nio.net.callback;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public interface NetCallback {
    public void onMessage(TcpConnection connection, TcpBuffer buffer);
    public void onAccept(TcpConnection sourceConnection);
    public void onConnect(TcpConnection sourceConnection);

    public final String ON_MESSAGE = "onMessage";
    public final String ON_ACCET = "onAccept";
    public final String ON_CONNECT = "onConnect";
}
