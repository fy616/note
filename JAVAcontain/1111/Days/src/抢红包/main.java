package 抢红包;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class main {
    static void main( String[] args) {
        //创建两百个红包
        List<Integer> list = gethongbao();
        //创建100个线程来抢红包，
        for(int i=1;i<=100;i++){
            new getredpackg(list,i+"号员工").start();
        }



    }
    public static  List<Integer> gethongbao(){
        ArrayList<Integer> list = new ArrayList<>();
        //模拟红包
        //创建一个随机 数
        Random r=new Random();
        for(int i=1;i<=160;i++){
            int money=r.nextInt(31)+1;
            list.add(money);
        }
        for(int i=161;i<=200;i++){
            int money=r.nextInt(101)+31;
            list.add(money);
        }
        return list;

    }
}
