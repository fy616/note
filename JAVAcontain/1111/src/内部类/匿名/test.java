package 内部类.匿名;

import 接口示例.Airplane;

public class test {
    static  void main() {
        animal a = new animal() {
            @Override
            public void cry() {
                System.out.println("匿名类直接重写");
            }
        };
        a.cry();

        System.out.println("=================================");
        swim s = ()  -> {
                System.out.println("老师会游泳h");
        };
        sw(s);
        System.out.println("=================================");
        sw(new swim() {
            @Override
            public void swiming() {
                System.out.println("学生会游泳");
            }
        });

    }
    static void sw(swim s) {
        System.out.println("kaishi");
        s.swiming();
        System.out.println("jieshu");
    }
}
interface swim{
    void swiming();
}
