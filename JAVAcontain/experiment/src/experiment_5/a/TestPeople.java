package experiment_5.a;

public class TestPeople {
    public static void main(String[] args) {
        System.out.println("=== 创建中国人对象 ===");
        ChinaPeople chinese = new ChinaPeople("张三", 25, 175.5, 70.0);
        chinese.speakHello();
        chinese.averageHeight();
        chinese.averageWeight();
        chinese.chinaGongfu();

        System.out.println("\n=== 创建美国人对象 ===");
        AmericanPeople american = new AmericanPeople("John", 30, 180.0, 85.5);
        american.speakHello();
        american.averageHeight();
        american.averageWeight();
        american.americanBoxing();

        System.out.println("\n=== 创建北京人对象 ===");
        BeijingPeople beijingPerson = new BeijingPeople("李四", 28, 178.0, 75.0);
        beijingPerson.speakHello();
        beijingPerson.averageHeight();
        beijingPerson.averageWeight();
        beijingPerson.chinaGongfu();
        beijingPerson.beijingOpera();
    }
}
