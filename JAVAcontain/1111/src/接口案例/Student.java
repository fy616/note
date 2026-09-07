package 接口案例;

public class Student {
    private String name;
    private int sex;
    private double score;
    public Student() {
    }

    public static int Comparable(Student s1, Student s2) {
            return (int)(s1.score-s2.score);
    }

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

    public int Comparable(Student student) {
              return (int)(this.score-student.score);
    }

    public int comparescore(Student student, Student student1) {
            return (int)(student.score-student1.score);
    }
}
