package 继承.tttt;

public class Teacher extends People {
    private String skill;

    public Teacher(String name, int age, String skill) {
        super(name, age);//调用父类的有参构造器
        this.skill = skill;
    }


    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }
}
