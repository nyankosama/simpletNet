package com.nyankosama.test;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.nyankosama.nio.net.TcpBuffer;
import com.nyankosama.nio.net.TcpConnection;
import com.nyankosama.nio.net.callback.CallbackSupport;
import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.utils.BindFunction;
import com.nyankosama.nio.net.utils.CommonUtils;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @Test
    public void testCommon() {
        Lock lock = new ReentrantLock();
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            try {
                lock.lock();
            } finally {
                lock.unlock();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("cost time:" + (end - begin) + " ms, qps:" + ((double) 50000 / (end - begin) * 1000));
    }

    private static class Base {
        public void print() {
            System.out.println("base");
        }

        public void basePrint() {

        }
    }

    private static class Sub extends Base {
        @Override
        public void print() {
            System.out.println("sub");
        }

        public void subPrint() {

        }
    }

    @Test
    public void testBindFunction() {
        Sub ptr = new Sub();
        MethodAccess ma = MethodAccess.get(ptr.getClass());
        int index = ma.getIndex("print");
        String names[] = ma.getMethodNames();
        ma.invoke(ptr, index);
    }
}
