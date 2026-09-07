package 抢红包;

import java.util.List;
import java.util.Random;


public class getredpackg extends Thread{
    private List<Integer> list;
    public getredpackg(List<Integer> list, String name) {
        super( name);
        this.list = list;
    }

    @Override
    public void run() {
        //抢红包
        String name = Thread.currentThread().getName();
        //防止线程安全漏洞
        while(true){
            synchronized (list){
                if(list.size()==0){
                    break;
                }
                Random r=new Random();
                int index = r.nextInt(list.size());
                int money = list.remove( index);
                System.out.println(name+"抢到红包"+money);
                if(list.size()==0){
                    System.out.println("红包结束");
                }

            }
        }


    }
}
