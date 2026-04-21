package 继承.extent5;

public class Text {
    static void main() {
        son s = new son();

    }
}
class  son extends fu{

    public son(){
//        super();//调用父类无参构造器写不写都有 效
//        super(88);//调用父类有参构造器
        System.out.println("子类无参构造器");
    }
    public son(int a){
        super(88);
        System.out.println("子类有参构造器");
    }
}
class  fu {
    public fu(){
        System.out.println("父类无参构造器");
    }
    public fu(int a){
        System.out.println("父类有参构造器");
    }
}

