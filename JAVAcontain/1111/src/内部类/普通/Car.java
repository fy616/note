package 内部类.普通;

public class Car {
    public static String name="五菱宏光";
    public static void test(){
        System.out.println("我是五菱宏光");
    }
    //内部类属
    public class engin{
        void show(){
            System.out.println("我是v8发动机");
            test();//访问外部类静态方法
            System.out.println(name);//访问外部类静态属性


        }


    }

}
