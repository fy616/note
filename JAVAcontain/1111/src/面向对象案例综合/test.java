package 面向对象案例综合;

public class test {
    public static void main(String[] args) {
        JD jd[]=new JD[4];
        jd[0]=new tv("tv",true);
        jd[1]=new lamp("lamp",false);
        jd[2]=new air("air",true);
        jd[3]=new whis("whis",false);
        Smartcontrol sc=new Smartcontrol();
        sc.control(jd[0]);
    }
}
