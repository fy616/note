package 继承.this的用法;

public class test {
    static void main() {
        Students s=new Students(18,'a',"jj","cddax");
        System.out.println(s.getAge()+"\t"+s.getX()+"\t"+s.getName()+"\t"+s.getSchool());
    }
}
