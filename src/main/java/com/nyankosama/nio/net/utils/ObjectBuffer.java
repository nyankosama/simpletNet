package com.nyankosama.nio.net.utils;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by hlr@superid.cn on 2014/10/25.
 */
public class ObjectBuffer<T> {
    //NOTE 基于ArrayList实现

    private static final int BUF_INIT_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 10;
    private static final double LOAD_FACTOR = 0.25;
    private ArrayList<T> objectBuf = new ArrayList<>(BUF_INIT_SIZE);
    private BindFunction<T> objectCreate;
    private BindFunction objectReset;
    private Unsafe unsafe;

    private volatile int tail;
    private volatile int capacity;
    private volatile int size;
    private AtomicBoolean isGrowing = new AtomicBoolean(false);

    private long tailOffset;
    private long sizeOffset;

    public ObjectBuffer(BindFunction<T> objectCreate, BindFunction objectReset) {
        this.unsafe = getUnsafe();
        initOffset();
        this.objectCreate = objectCreate;
        this.objectReset = objectReset;
        for (int i = 0; i < BUF_INIT_SIZE; i++) {
            objectBuf.add(objectCreate.call());
        }
        tail = BUF_INIT_SIZE - 1;
        capacity = BUF_INIT_SIZE;
        size = BUF_INIT_SIZE;
    }

    public T getObject() {
        waitGrowingIfNeeded();//忙等待
        ensureCapacityInternal();
        //NOTE 返回tail所指向的元素, 使用无锁算法
        for (; ; ) {
            T obj = objectBuf.get(tail);//这里可以保证返回的object绝对不会因为多线程的关系而重复
            int newTail = tail - 1;
            //NOTE 这里会产生不一致状态，但是并不影响本类正常工作
            if (compareAndSet(tailOffset, tail, newTail)) {
                for (; ; ) {
                    int newSize = size - 1;
                    if (compareAndSet(sizeOffset, size, newSize)) {
                        return obj;
                    }
                }
            }
        }
    }

    public void returnObject(T t) {
        waitGrowingIfNeeded();//忙等待
        //NOTE 把使用完毕的对象返还到tail，这里不是真的put，而是只操作tail和size
        //为了避免用户困惑，这里的参数还是保留unused的t
        objectReset.call(t);
        for (; ; ){
            int newTail = tail + 1;
            //NOTE 这里会产生不一致状态，但是并不影响本类正常工作
            if (compareAndSet(tailOffset, tail, newTail)) {
                for (; ; ){
                    int newSize = size + 1;
                    if (compareAndSet(sizeOffset, size, newSize)) {
                        return;
                    }
                }
            }
        }
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    private void ensureCapacityInternal() {
        //NOTE: 无锁实现，使得同一时间只可能有一次grow被调用
        if (size < capacity * LOAD_FACTOR) {
            if (isGrowing.compareAndSet(false, true)){
                grow();
                isGrowing.set(false);
            }else {
                //已经有线程调用grow这里忙等待
                waitGrowingIfNeeded();
            }
        }
    }

    private void grow() {
        System.out.println("growing!");
        System.out.println("tail=" + tail + ", size=" + size + ", capacity=" + capacity);
        //NOTE 使得capacity变为原来两倍，添加原来的capacity那么多的初始化对象进入pool
        tail += capacity;
        size += capacity;
        for (int i = 0; i < capacity; i++) {
            objectBuf.add(objectCreate.call());
        }
        capacity *= 2;
    }

    private Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initOffset() {
        try {
            tailOffset = unsafe.objectFieldOffset(ObjectBuffer.class.getDeclaredField("tail"));
            sizeOffset = unsafe.objectFieldOffset(ObjectBuffer.class.getDeclaredField("size"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private boolean compareAndSet(long offset, int current, int next) {
        return unsafe.compareAndSwapInt(this, offset, current, next);
    }

    private void waitGrowingIfNeeded() {
        for (; ; ) {
            if (isGrowing.get() == false) break;
        }
    }
}
