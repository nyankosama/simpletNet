package com.nyankosama.nio.net.utils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hlr@superid.cn on 2014/10/27.
 */
public final class LockFreeBlockingQueue<T> extends LinkedList<T> implements BlockingQueue<T>{
    //NOTE 为了避免LinkedList addLast和removeFirst的不一致状态
    //这里通过保证first != last，及如果同时addLast和removeFirst保证其操作的所有变量不交叉

    private int capacity;
    private AtomicInteger size;
    private AtomicBoolean isFull;
    private AtomicBoolean isEmpty;
    private Lock lock = new ReentrantLock();
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();

    private static final int EMPTY_SIZE_THREASHHOLD = 1;

    public LockFreeBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.size = new AtomicInteger(0);
    }

    @Override
    public final boolean add(T t) {
        if (size.get() == capacity) throw new IllegalStateException("ThreadLocalBlockingQueue is full!");
        super.addLast(t);
        size.incrementAndGet();
        return true;
    }

    @Override
    public final void put(T t) throws InterruptedException {
        while (size.get() == capacity) {
            System.out.println("warning! threadLocalQueue has been blocked!");
            //NOTE 这里存在不一致性，可能出现队列非满，但是仍然等待的情况
            //不过最终一定会因为take而使得条件队列返回
            try{
                lock.lock();
                notFull.await();
            } finally {
                lock.unlock();
            }
        }
        add(t);//NOTE 由于外部条件队列保护的关系，这里LinkedList的操作不会发生race condition
        if (size.get() == EMPTY_SIZE_THREASHHOLD) {
            try{
                lock.lock();
                notEmpty.signalAll();//FIXME 这个地方会有问题，如果同时从while跳出来，就可能会发生状态不一致性的问题
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public final boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final T take() throws InterruptedException {
        while (size.get() < EMPTY_SIZE_THREASHHOLD) {//NOTE first和last之间保证还有一个Element
            try{
                lock.lock();
                notEmpty.await();
            } finally {
                lock.unlock();
            }
            //NOTE 这里存在不一致性，可能出现队列非空，但是仍然等待的情况
            //不过最终一定会因为put而使得条件队列返回
        }
        size.decrementAndGet();
        T t = removeFirst();
        if (size.get() == )
        notFull.signalAll();
        return t;
    }

    @Override
    public final T poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int remainingCapacity() {
        return capacity - size.get();
    }

    @Override
    public synchronized final int drainTo(Collection<? super T> c) {
        this.removeAll(c);
        int result = size.get();
        size.set(0);
        return result;
    }

    @Override
    public synchronized final int drainTo(Collection<? super T> c, int maxElements) {
        if (maxElements >= size.get()) maxElements = size.get();
        ListIterator<T> it = listIterator(0);
        for (int i = 0, n = maxElements - 0; i < n; i++) {
            c.add(it.next());
            it.remove();
        }
        return maxElements;
    }
}
