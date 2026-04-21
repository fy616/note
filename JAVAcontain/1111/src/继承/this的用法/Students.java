package 继承.this的用法;

public class Students {
    private int age;
   private char x;
    private  String name;
    private String school;

    public Students() {
    }
    public Students(int age, char x, String name) {
        this(age,x,name,"黑马程序员");//this()的作用是调用本类的其他构造方法
    }
    public Students(int age, char x, String name, String school) {
        this.age = age;
        this.x = x;
        this.name = name;
        this.school = school;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getX() {
        return x;
    }

    public void setX(char x) {
        this.x = x;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
