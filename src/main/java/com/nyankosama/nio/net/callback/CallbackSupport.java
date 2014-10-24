package com.nyankosama.nio.net.callback;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public abstract class CallbackSupport implements NetCallback {
    //这些方法是用来override的
    public void onMessage(TcpConnection connection, TcpBuffer buffer){}
    public void onAccept(TcpConnection sourceConnection){}
    public void onConnect(TcpConnection sourceConnection){}
}
