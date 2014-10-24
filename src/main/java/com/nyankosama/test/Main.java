package com.nyankosama.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by hlr@superid.cn on 2014/10/19.
 */
public class Main {

    static class JoinTask extends RecursiveTask<Integer>{
        private int A[];
        private int interval;
        private List<ForkJoinTask> taskList;

        public JoinTask(int A[], int interval){
            this.A = A;
            this.interval = interval;
            taskList = new ArrayList<>(A.length / interval + 10);
        }

        @Override
        protected Integer compute() {
            int index = 0;
            int size = A.length;
            while (index < size) {
                int low = index;
                int high = index + interval;
                if (high < size) {
                    ForkJoinTask<Integer> task = new SplitTask(low, high, A);
                    task.fork();
                    taskList.add(task);
                } else {
                    ForkJoinTask<Integer> task = new SplitTask(low, size, A);
                    task.fork();
                    taskList.add(task);
                }
                index += interval;
            }

            int sum = 0;
            for (ForkJoinTask<Integer> task : taskList) {
                sum += task.join();
            }
            return sum;
        }
    }

    static class SplitTask extends RecursiveTask<Integer>{
        private int low;
        private int high;
        private int A[];

        public SplitTask(int low, int high, int A[]){
            this.low = low;
            this.high = high;
            this.A = A;
        }

        @Override
        protected Integer compute() {
            int sum = 0;
            for (int i = low; i < high; i++){
                sum += A[i];
            }
            return sum;
        }
    }

    private static int computeSum(int A[]) {
        int sum = 0;
        for (int i : A) {
            sum += i;
        }
        return sum;
    }

    public static void main(String args[]) throws NoSuchFieldException, IllegalAccessException {
        ForkJoinPool pool = new ForkJoinPool();
        int A[] = new int[1000000];
        for (int i = 0; i < 1000000; i++) {
            A[i] = i;
        }
        System.out.println("fork begin!");
        long begin = System.currentTimeMillis();
        int sum = pool.invoke(new JoinTask(A, 10000));
        long end = System.currentTimeMillis();
        System.out.println("sum = " + sum);
        System.out.println("cost time = " + (end - begin) + " ms");
        begin = System.currentTimeMillis();
        System.out.println("check sum = " + computeSum(A));
        end = System.currentTimeMillis();
        System.out.println("cost time = " + (end - begin) + " ms");
    }
}
