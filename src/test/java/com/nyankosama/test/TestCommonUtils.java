package com.nyankosama.test;

import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.CallbackSupport;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.utils.CommonUtils;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public class TestCommonUtils {

    @Test
    public void testDecideCallbackOverride() {
        NetCallback callback = new CallbackSupport() {
            @Override
            public void onMessage(TcpConnection connection, TcpBuffer buffer) {
                System.out.println("onMessage");
            }
        };
        assert CommonUtils.decideCallbackOverride(callback, NetCallback.ON_MESSAGE) == true;
    }
}
