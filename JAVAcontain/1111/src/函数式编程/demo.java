package 函数式编程;

import 接口案例.Student;

import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;

public class demo {
    static void main(String [] args) {
        FunctionalInterface fi = () -> {
            System.out.println("函数式接口");
            System.out.println("函数式接口");
        };
        fi.print();
        System.out.println("===========================");
        //给我几个学生对象
        Student []all=new Student[5];
        all[0]=new Student("小王",1,91);
        all[1]=new Student("小刘",1,93);
        all[2]=new Student("小谢",1,67);
        all[3]=new Student("小美",1,4);
        all[4]=new Student("小李",1,93);
        Arrays.sort(all,(s1,s2)->{
            return (int) (s1.getScore()-s2.getScore());
        });
        for(int i=0;i<all.length;i++)
        {
            System.out.println(all[i].getName()+"  "+all[i].getScore());
        }
        Arrays.sort(all,(s1,s2)-> (int) (s2.getScore()-s1.getScore()));
        for(int i=0;i<all.length;i++)
        {
            System.out.println(all[i].getName()+"  "+all[i].getScore());
        }
        System.out.println("===========================");
        System.out.println("静态方法函数式编程");
        Arrays.sort(all,(s1,s2)-> Student.Comparable(s1,s2));
        System.out.println("=================================");
        System.out.println("实例方法函数式编程");
        Student s=new Student();
        Arrays.sort(all,(s1,s2)-> s.comparescore(s1,s2));
        Arrays.sort(all,s::comparescore);//实例方法函数式编程,与上面相同

    }

}

interface FunctionalInterface {
    void print();
}
