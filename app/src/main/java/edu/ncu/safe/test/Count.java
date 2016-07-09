package edu.ncu.safe.test;

/**
 * Created by Mr_Yang on 2016/7/3.
 */

public class Count {
    static class MyObject {
        static int mycount = 0;
    }

    public static void inc() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MyObject.mycount++;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    inc();
                }
            }).start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ;
        System.out.println("Counter.count=" + MyObject.mycount);
    }
}