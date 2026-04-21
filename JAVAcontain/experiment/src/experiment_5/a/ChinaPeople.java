package experiment_5.a;

public class ChinaPeople extends People {

    public ChinaPeople() {
        super();
    }

    public ChinaPeople(String name, int age, double height, double weight) {
        super(name, age, height, weight);
    }

    @Override
    public void speakHello() {
        System.out.println("您好！我是中国人 " + name);
    }

    public void chinaGongfu() {
        System.out.println(name + " 会中国功夫：坐如钟，站如松，睡如弓！");
    }
}
