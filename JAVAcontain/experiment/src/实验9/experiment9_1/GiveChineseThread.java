package 实验9.experiment9_1;

public class GiveChineseThread extends Thread {
    Chinese hanzi;
    char startChar =(char)22909,endChar = (char)(startChar+100);
    int sleepLength = 5000;
    public void setChinese(Chinese hanzi) {
        this.hanzi = hanzi;
    }
    public void setSleepLength(int n){
        sleepLength = n;
    }
    public void run() {
        char c = startChar;
        while(true) {
            hanzi.setChinese(c);
            System.out.printf("显示的汉字:%c\n ",hanzi.getChinese());
            try{  sleep(sleepLength);//调用sleep方法使得线程中断sleepLength豪秒
            }
            catch(InterruptedException e){}
            c = (char)(c+1);
            if(c>endChar)
                c = startChar;
        }
    }
}
