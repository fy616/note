package 实验3.experiment3_5;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
            int a[]=new int[10];
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入10个数字：");
        for(int i=0;i<10;i++){
            a[i]=scanner.nextInt();
        }
        //计算平局值，最小值，最大值，数据的和
        int sum=0;
        int max=a[0];
        int min=a[0];
        for(int i=0;i<10;i++){
            sum+=a[i];
            if(a[i]>max){
                max=a[i];
            }
            if(a[i]<min){
                min=a[i];
            }
        }
        System.out.println("平均值为："+(sum/10));
        System.out.println("最小值为："+min);
        System.out.println("最大值为："+max);
        System.out.println("数据的和为："+sum);
    }
}
