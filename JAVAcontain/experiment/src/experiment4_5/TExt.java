package experiment4_5;

import java.util.Scanner;

/**
 * 复数测试类：键盘输入 + 运算测试
 */
public class TExt {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 输入第一个复数
        System.out.print("请输入第一个复数的实部：");
        int x1 = sc.nextInt();
        System.out.print("请输入第一个复数的虚部：");
        int y1 = sc.nextInt();
        Complex c1 = new Complex(x1, y1);

        // 输入第二个复数
        System.out.print("请输入第二个复数的实部：");
        int x2 = sc.nextInt();
        System.out.print("请输入第二个复数的虚部：");
        int y2 = sc.nextInt();
        Complex c2 = new Complex(x2, y2);

        System.out.println("------------------------------------");
        System.out.print("复数1：");
        c1.showComp();
        System.out.print("复数2：");
        c2.showComp();
        System.out.println("------------------------------------");

        // 创建运算工具对象
        Complex tool = new Complex();

        // 测试加法
        Complex add = tool.addComp(c1, c2);
        System.out.print("两复数之和：");
        add.showComp();

        // 测试减法
        Complex sub = tool.subComp(c1, c2);
        System.out.print("两复数之差：");
        sub.showComp();

        // 测试乘法
        Complex multi = tool.multiComp(c1, c2);
        System.out.print("两复数之积：");
        multi.showComp();

        // 测试相等
        boolean eq = tool.equalComp(c1, c2);
        System.out.println("两个复数是否相等：" + eq);

        sc.close();
    }
}
