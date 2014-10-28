package com.nyankosama.nio.net.handler.impl;

import com.nyankosama.nio.net.handler.SelectorHandler;

import java.nio.channels.SelectableChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by hlr@superid.cn on 2014/10/28.
 */
public abstract class AbstractSelectorHandler implements SelectorHandler{

    private Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    public Runnable createTask(SelectableChannel channel){
        return null;
    }

    @Override
    public void registerTask(SelectableChannel channel) {
        Runnable task = createTask(channel);
        if (task != null) taskQueue.add(task);
    }


}
