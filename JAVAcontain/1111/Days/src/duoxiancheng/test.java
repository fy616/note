package duoxiancheng;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class test {
    static void main(   String[] args) {
        //第一种方法创建多线程
        Mythread t1=new Mythread();
        t1.start();
        Mythread t2=new Mythread();
        t2.start();
        //第二种方法创建多线程,用接口和匿名 类
//        Thread t6=new Thread(t5);
//        t6.start();
        Thread tt= new Thread(new Myrunable1());
        tt.start();
        Thread t3=new Thread(new Myrunable() {
            public void run() {
                System.out.println(Thread.currentThread().getName()+"创建的线程");
            }
        });
         //第三种方法创建多线程，用接口和lambda表达式
          Thread t4=new Thread(() -> {
              System.out.println("这是lambda表达式创建的线程");
          });
          t3.setName("线程3");
          t4.setName("线程4");
          t3.start();
          //有返回值的线程
//        Callable<Integer> t5=new Callable<Integer>() {
//            @Override
//            public Integer call() throws Exception {
//                return 50;
//            }
//        };
//        Callable<Integer> t6=()->{
//            return 50;
//        };
//        FutureTask<Integer> t7=new FutureTask<Integer>(t5);
//        Thread t9=new Thread(t7);
//        t9.start();
//        try {
//            System.out.println(t7.get());//获取线程的返回值
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        FutureTask<Integer> t8=new FutureTask<Integer>(t6);
//        Thread t10=new Thread(t8);




    }
}
