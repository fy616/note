package 实验9.experiment9_1;

public class TypeChinese {
    public static void main(String args[]) {
        System.out.println("输入汉字练习(输入#结束程序)");
        System.out.printf("输入显示的汉字(回车)\n");
        Chinese hanzi;
        hanzi = new Chinese();
        GiveChineseThread giveHanzi;
        InputChineseThread typeHanzi;
          giveHanzi = new GiveChineseThread();    //【代码1】创建线程giveHanzi
        giveHanzi.setChinese(hanzi);
        giveHanzi.setSleepLength(6000);
         typeHanzi = new InputChineseThread();  //【代码2】创建线程typeHanzi
        typeHanzi.setChinese(hanzi);
        giveHanzi.start();
        try{
            Thread.sleep(200);
        }
        catch(Exception exp){}
        typeHanzi.start();
    }
}
