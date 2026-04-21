package various;

public class test {
    static void main() {
        Animal a = new Animal();
        Animal a1=new Tortoise();
        a.run();
        a1.run();
        System.out.println(a.name);
        System.out.println(a1.name);
        ff( a);
        ff(a1);
        //强制类型转换
        Tortoise a2=(Tortoise)a1;
        a2.shrink();
        System.out.println(a2.name);
        System.out.println("----------------------------------------");
        //运行时错误,运行时类型转换错误,编译时没有错误
//        Wolf a3= (Wolf) a1;
        //转换前用instanceof判断
        if(a1 instanceof Wolf)
        {
            Wolf a3=(Wolf) a1;
            a3.attack();

        }else if(a1 instanceof Tortoise)
        {
            Tortoise a3=(Tortoise) a1;
            a3.shrink();

        }
    }
    public static void ff(Animal a)
    {
        a.run();
    }

}
