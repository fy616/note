package demo_move;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.Files.delete;


public class MoveServes {
    private List<Move> moves = new ArrayList<>();
    public void start() {
        while (true) {
            System.out.println("==========================");
            System.out.println("欢迎来到电影管理系统");
            System.out.println("1.添加电影");
            System.out.println("2.下架电影");
            System.out.println("3.电影名称查询电影");
            System.out.println("4.封杀某个明星");
            System.out.println("请输入数字");
            Scanner sc = new Scanner(System.in);
            int num = sc.nextInt();
            switch (num) {
                case 1:
                    add();
                    break;
                case 2:
                    delete();
                    break;
                case 3:
                    serche();
                    break;
                case 4:
                    kill();
                    break;
                case 5:
                    System.out.println("退出");
                    return;
                default:
                    System.out.println("输入有毛病");
            }
        }
    }

    private void kill() {
        System.out.println("请输入明星名称");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();
        for(Move move:moves){
            if(move.getActer().contains(name)){
                moves.remove(move);
                System.out.println("封杀成功");
                return;
            }
        }
        System.out.println("没有此明星");
    }

    private void serche() {
        System.out.println("请输入电影名称");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();
        for(Move move:moves){
            if(move.getName().contains(name)){
                System.out.println(move.getId()+"\t"+move.getName()+"\t"+move.getPrice()+"\t"+move.getActer());
                return;
            }
        }
        System.out.println("没有此电影");


    }

    private void delete() {
        System.out.println("请输入电影编号");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        for(Move move:moves){
            if(move.getId()==id){
                moves.remove(move);
                System.out.println("下架成功");
                return;
            }
        }
        System.out.println("没有此电影");


    }

    private void add() {
        //先添加电影类
        Move move = new Move();
        //注入数据
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入编号");
        move.setId(sc.nextInt());
        System.out.println("请输入电影名称");
        move.setName(sc.next());
        System.out.println("请输入价格");
        move.setPrice(sc.nextDouble());
        System.out.println("请输入主演");
        move.setActer(sc.next());
        //添加到列表去
        moves.add(move);
        System.out.println("添加成功");
    }
}
