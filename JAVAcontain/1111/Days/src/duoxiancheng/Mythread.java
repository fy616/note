package duoxiancheng;

public class Mythread  extends Thread{
    public void run(){
        for(int i = 0; i < 100; i++){
            System.out.println("线程1："+i);
        }
    }
}
