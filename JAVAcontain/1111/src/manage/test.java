package manage;

import jdk.swing.interop.LightweightContentWrapper;

import javax.swing.*;
import javax.xml.transform.Source;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class test {
    static Scanner input=new Scanner(System.in);
    public static void main(String[] args) {

        System.out.println("学生信息管理系统");
        while(true){
            int k;
            menu();
            k=input.nextInt();
            if(k==0) break;
            switch (k){
                case 1:
                    addStudent();
                    break;
                case 2:
                    deleteStudent();
                    break;
                case 3:
                    changeStudent();
                    break;
                case 4:
                    break;

            }
        }
        System.out.println("操作完成结束");

    }
    static void menu(){
        System.out.println("请输入");
        System.out.println("1.添加学生数据");
        System.out.println("2.删除学生数据");
        System.out.println("3.改动学生数据");
        System.out.println("4.查询学生数据");
        System.out.println("0.退出");
    }
    static void addStudent(){
        ArrayList<student> list=new ArrayList<>();
        System.out.println("请输入学生信息");
        System.out.println("请输入学生id");
        int id=input.nextInt();
        System.out.println("请输入学生姓名");
        String name=input.next();
        System.out.println("请输入学生专业");
        String major=input.next();
        System.out.println("请输入学生年龄");
        int age=input.nextInt();
        student s=new student(id,name,major,age);
        list.add(s);
        File f=new File("D:\\JAVAcontain\\1111\\src\\manage\\data.txt");

        try(
                FileOutputStream outputStream= new FileOutputStream(f, true);
                OutputStreamWriter writer=new OutputStreamWriter(outputStream);
        ) {
            if(f.length()>0)
                writer.write("\n");
            writer.write(s.getId()+" "+s.getName()+" "+s.getMajor()+" "+s.getAge());
            writer.flush();
            System.out.println("写入成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //删除学生数据,先读取数据，并把不是目标数据的学生重新写入文件
    static void deleteStudent(){
        File f=new File("D:\\JAVAcontain\\1111\\src\\manage\\data.txt");
        ArrayList<student> list=new ArrayList<>();
        try(
                FileInputStream inputStream= new FileInputStream(f);
                InputStreamReader reader=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(reader);)
            {
                System.out.println("请输入要删除的id");
                int id=input.nextInt();
                String line;
                while((line=br.readLine())!=null){
                    String[] split = line.split(" ");
                    student s=new student(Integer.parseInt(split[0]),split[1],split[2],Integer.parseInt(split[3]));
                    if(s.getId()!=id)
                        list.add(s);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        try(
                FileOutputStream outputStream= new FileOutputStream(f);
                OutputStreamWriter writer=new OutputStreamWriter(outputStream);
        ) {
            for(student s:list){
                writer.write(s.getId()+" "+s.getName()+" "+s.getMajor()+" "+s.getAge());
                writer.write("\n");
            }
            writer.flush();
            System.out.println("删除成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void changeStudent(){
        File f=new File("D:\\JAVAcontain\\1111\\src\\manage\\data.txt");
        ArrayList<student> list=new ArrayList<>();
        try(
                FileInputStream inputStream= new FileInputStream(f);
                InputStreamReader reader=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(reader);
                ){
            System.out.println("请输入要修改的id");
            int id=input.nextInt();
            String line;
            while((line=br.readLine())!=null){
                String[] split = line.split(" ");
                student s=new student(Integer.parseInt(split[0]),split[1],split[2],Integer.parseInt(split[3]));
                if(s.getId()==id){
                    System.out.println("请输入修改后的信息");
                    System.out.println("请输入学生id");
                    int newid=input.nextInt();
                    System.out.println("请输入学生姓名");
                    String name=input.next();
                    System.out.println("请输入学生专业");
                    String major=input.next();
                    System.out.println("请输入学生年龄");
                    int age=input.nextInt();
                    s=new student(newid,name,major,age);
                    list.add(s);
                    System.out.println("修改成功");
                }
                else
                    list.add(s);
                //添加到文件中,覆盖
                try(
                        FileOutputStream outputStream= new FileOutputStream(f);
                        OutputStreamWriter writer=new OutputStreamWriter(outputStream);){
                    for(student as:list){
                        writer.write(as.getId()+" "+as.getName()+" "+as.getMajor()+" "+as.getAge());
                        writer.write("\n");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static void queryStudent(){
        File f=new File("D:\\JAVAcontain\\1111\\src\\manage\\data.txt");
        try(
                FileInputStream inputStream= new FileInputStream(f);
                InputStreamReader reader=new InputStreamReader(inputStream);
                BufferedReader br=new BufferedReader(reader);)
        {
            System.out.println("请输入要查询的id");
            int id=input.nextInt();
            String line;
            while((line=br.readLine())!=null){
                String[] split = line.split(" ");
                student s=new student(Integer.parseInt(split[0]),split[1],split[2],Integer.parseInt(split[3]));
                if(s.getId()==id){
                    System.out.println("查询结果");
                    System.out.println("id:"+s.getId());
                    System.out.println("姓名:"+s.getName());
                    System.out.println("专业:"+s.getMajor());
                    System.out.println("年龄:"+s.getAge());
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
