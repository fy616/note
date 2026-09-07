package 实验4.experiment4_5;

/**
 * 复数类：z = x + iy
 */
public class Complex {
    // 复数属性：实部x 和 虚部y
    private double x;
    private double y;

    // 默认构造函数：x=0, y=0
    public Complex() {
        this.x = 0;
        this.y = 0;
    }

    // 带参数的构造函数
    public Complex(int i, int j) {
        this.x = i;
        this.y = j;
    }

    // 显示复数：格式 5+8i 或 5-8i
    public void showComp() {
        if (y >= 0) {
            System.out.println(x + "+" + y + "i");
        } else {
            System.out.println(x + "" + y + "i");
        }
    }

    // 求两个复数的和
    public Complex addComp(Complex C1, Complex C2) {
        double real = C1.x + C2.x;
        double imag = C1.y + C2.y;
        Complex temp = new Complex();
        temp.x = real;
        temp.y = imag;
        return temp;
    }

    // 求两个复数的差
    public Complex subComp(Complex C1, Complex C2) {
        double real = C1.x - C2.x;
        double imag = C1.y - C2.y;
        Complex temp = new Complex();
        temp.x = real;
        temp.y = imag;
        return temp;
    }

    // 求两个复数的乘积
    public Complex multiComp(Complex C1, Complex C2) {
        // 公式：(x1+iy1)*(x2+iy2) = (x1x2-y1y2) + (x1y2+x2y1)i
        double real = C1.x * C2.x - C1.y * C2.y;
        double imag = C1.x * C2.y + C2.x * C1.y;
        Complex temp = new Complex();
        temp.x = real;
        temp.y = imag;
        return temp;
    }

    // 比较两个复数是否相等
    public boolean equalComp(Complex C1, Complex C2) {
        return (C1.x == C2.x) && (C1.y == C2.y);
    }
}