package com.nyankosama.nio.net.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by hlr@superid.cn on 2014/10/24.
 */
public interface SelectorHandler {

    public void process(SelectionKey key) throws IOException;

}
