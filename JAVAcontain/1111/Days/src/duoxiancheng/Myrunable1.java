package duoxiancheng;

public class Myrunable1  implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 100; i++)
            System.out.println("线程1："+i);
    }
}
