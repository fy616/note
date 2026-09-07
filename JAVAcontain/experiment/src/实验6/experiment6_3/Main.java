package 实验6.experiment6_3;

import java.util.Scanner;

public class Main {
    static void main( String args[]) {
        //输入整数，如果不是整数则抛出异常
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入一个整数：");
        String str = sc.next();
        try {
            int i = Integer.parseInt(str);
            System.out.println("输入的整数是：" + i);
        } catch (NumberFormatException e) {
            System.out.println("输入的整数格式错误！");
        }



    }
}
