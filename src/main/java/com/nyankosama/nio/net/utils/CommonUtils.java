package com.nyankosama.nio.net.utils;

import com.nyankosama.nio.net.callback.CallbackSupport;
import com.nyankosama.nio.net.callback.NetCallback;

import java.lang.reflect.Method;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public class CommonUtils {

    public static boolean decideCallbackOverride(NetCallback callback, String overrideMethodName) {
        Method method = findFirstMethodByName(callback.getClass(), overrideMethodName);
        if (method == null) return false;
        if (method.getDeclaringClass() != CallbackSupport.class) {
            return true;
        }
        return false;
    }

    public static Method findFirstMethodByName(Class<?> clazz, String name) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) return method;
        }
        return null;
    }

}
