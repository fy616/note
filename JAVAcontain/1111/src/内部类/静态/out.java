package 内部类.静态;

public class out {
    //静态内部类属于外部类本身
    private static String name="五菱宏光";
    public static class inner{
        public void show(){
            System.out.println("静态内部类");
            System.out.println(name);//访问外部类静态属性
            //静态内部类不能访问外部类非静态属性
        }
    }
}
