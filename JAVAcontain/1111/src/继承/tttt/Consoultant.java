package 继承.tttt;

public class Consoultant extends People {
    private int number;
    public Consoultant(String name, int  age) {
        super(name,age);
        this.number=1001;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
