package demo_2;

public class Threaddemo3 {
    public static void main(String[] args) {
        //模拟线程问题
        Account account = new Account(100000, "ICOD");
        //模拟两个线程同时对这个账户取钱
        new withdrawthread("xm", account).start();
        new withdrawthread("xh", account).start();
        //解决这个线程问题，创建一个同步方法，锁对象
        //第二 种方法，锁方法


    }
}


