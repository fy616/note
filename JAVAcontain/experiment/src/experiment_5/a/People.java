package experiment_5.a;

public class People {
    protected String name;
    protected int age;
    protected double height;
    protected double weight;

    public People() {
    }

    public People(String name, int age, double height, double weight) {
        this.name = name;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public void speakHello() {
        System.out.println("Hello! 我是 " + name);
    }

    public void averageHeight() {
        System.out.println(name + " 的身高是: " + height + " cm");
    }

    public void averageWeight() {
        System.out.println(name + " 的体重是: " + weight + " kg");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
