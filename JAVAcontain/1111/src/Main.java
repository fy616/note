import java.util.*;

import static java.lang.Thread.sleep;

public class Main{
    public static void main( String []args){
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        Thread t = new Thread(new e(n));
        t.start();
        


    }
}
class e implements Runnable{
    int n;
    e(int n){
        this.n = n;
    }
    public void run(){
        for(int i=n;i>=0;i--){
            System.out.println(i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}