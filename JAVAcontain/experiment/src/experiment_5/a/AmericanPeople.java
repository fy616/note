package experiment_5.a;

public class AmericanPeople extends People {

    public AmericanPeople() {
        super();
    }

    public AmericanPeople(String name, int age, double height, double weight) {
        super(name, age, height, weight);
    }

    @Override
    public void speakHello() {
        System.out.println("How do you do! I am " + name + " from America");
    }

    public void americanBoxing() {
        System.out.println(name + " 会美式拳击：直拳、勾拳、组合拳！");
    }
}
