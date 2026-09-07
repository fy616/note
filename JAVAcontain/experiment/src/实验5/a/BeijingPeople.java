package 实验5.a;

public class BeijingPeople extends ChinaPeople {

    public BeijingPeople() {
        super();
    }

    public BeijingPeople(String name, int age, double height, double weight) {
        super(name, age, height, weight);
    }

    @Override
    public void speakHello() {
        System.out.println("您好！我是北京人 " + name);
    }

    public void beijingOpera() {
        System.out.println(name + " 会唱京剧：苏三离了洪洞县~");
    }
}
