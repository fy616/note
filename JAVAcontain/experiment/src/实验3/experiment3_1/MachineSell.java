package 实验3.experiment3_1;

import java.util.Scanner;
public class MachineSell {
    public static void main(String args[]){
        int money;
        int drinkKind;
        System.out.printf("投入金额:2或3元(回车确认):");
        Scanner reader=new Scanner(System.in);
        money=reader.nextInt();
        if(money==2) {
            System.out.printf("选择净净矿泉水(1),甜甜矿泉水(2)和美美矿泉水(3)之一:\n");
            System.out.printf("输入1,2或3:");
            drinkKind=reader.nextInt();
            switch(drinkKind) {
                case 1 : System.out.printf("得到净净矿泉水\n");
                    break;
                case 2 : System.out.printf("得到甜甜矿泉水\n");
                    break;
                case 3 : System.out.printf("得到美美矿泉水\n");
                    break;
                default: System.out.printf("选择错误");
            }
        }
        else if(money==3) {
            System.out.printf("选择爽口可乐(1),清凉雪碧(2),和雪山果汁(3)之一:\n");
            System.out.printf("输入1,2或3:");
            drinkKind=reader.nextInt();
            switch(drinkKind) {
                case 1 : System.out.printf("得到爽口可乐\n");
                    break;
                case 2 : System.out.printf("得到清凉雪碧\n");
                    break;
                case 3 : System.out.printf("得到雪山果汁\n");
                    break;
                default: System.out.printf("选择错误");
            }
        }
        else {
            System.out.printf("输入的钱币不符合要求");
        }
    }
}
