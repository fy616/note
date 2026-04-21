package 接口案例;

public class test {
    public static void main(String[] args) {
        //准备学生 数据
        Student []all=new Student[10];
        all[0]=new Student("小王",1,90);
        all[1]=new Student("小张",0,80);
        all[2]=new Student("小李",1,70);
        all[3]=new Student("小赵",0,60);
        all[4]=new Student("小孙",1,50);
        all[5]=new Student("小周",0,40);
        all[6]=new Student("小吴",1,30);
        all[7]=new Student("小郑",0,20);
        all[8]=new Student("小王",1,10);
        all[9]=new Student("小王",1,90);
        
        System.out.println("====== 第一个类:普通平均 ======");
        // 使用第一个类 - 普通平均
        SimpleStudentPrinter simplePrinter = new SimpleStudentPrinter(all);
        simplePrinter.printStudentInfo();
        simplePrinter.printAverageScore();
        
        System.out.println("\n====== 第二个类:去极值平均 ======");
        // 使用第二个类 - 减去最高分和最低分
        AdvancedStudentPrinter advancedPrinter = new AdvancedStudentPrinter(all);
        advancedPrinter.printStudentInfo();
        advancedPrinter.printAverageScore();
    }
}
