package 接口案例;

public class Student {
    private String name;
    private int sex;
    private double score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    //有参构造器
    public Student(String name, int sex, double score) {
        this.name = name;
        this.sex = sex;
        this.score = score;
    }

}
