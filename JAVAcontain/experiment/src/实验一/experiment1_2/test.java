package 实验一.experiment1_2;

public class test {
    static void main(String[] args) {
        student s=new student();
        s.show();
        teacher t=new teacher();
        t.show();


    }
}
class student{
    public void show(){
        System.out.println("I am a student");
    }
}
class teacher{
    public void show(){
        System.out.println("I am a teacher");
    }
}
