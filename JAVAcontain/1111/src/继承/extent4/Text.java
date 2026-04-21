package 继承.extent4;

public class Text {
    static void main() {
        Cat c=new Cat();
        c.show();
    }
}
class  Cat extends  Animal{
    @Override
    public void show() {
        System.out.println("喵喵喵！！");
    }
}
class  Animal{
    public void show() {
        System.out.println("动物会叫");
    }
}
