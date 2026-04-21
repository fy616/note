package 多态案例;

public class test {
    public static void main(String[] args) {
        //创建一个金卡对象
        glodcard gc = new glodcard( "张三","1234567890","12345678901",10000);
        //创建一个白金卡对象
        sliver sl = new sliver( "张三","1234567890","12345678901",1000);
        consum(gc);
    }
    public static void consum(Card c){
        c.consume(500);

    }
}
