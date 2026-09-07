package 实验6.experiment6_4;


import java.util.Scanner;

public class San {
    static void main( String args[]) {
        System.out.println("========================================");
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入三角形的三边：");
        int a = sc.nextInt();
        int b = sc.nextInt();
        int c = sc.nextInt();
        //判断是不是三角型，不是就捕获异常
        try {
            sanjiao(a,b,c);
        } catch (Sex e) {
            throw e;
        }


    }
    public   static void  sanjiao(int a,int b,int c) throws Sex{
        if(a+b>c&&a+c>b&&b+c>a){
            System.out.println("是三角形");
        }
        else{
            throw new Sex("不是三角形");
        }

    }

}

