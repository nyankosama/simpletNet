package com.nyankosama.nio.net.handler;

import com.nyankosama.nio.net.callback.NetCallback;
import com.nyankosama.nio.net.utils.BindFunction;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public interface SelectorHandler {

    public void process(SelectionKey key) throws IOException;

}
