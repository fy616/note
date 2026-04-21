package 面向对象案例综合;

public class Smartcontrol {
    public void control(JD jd) {
        //输出名字和状态
        System.out.println("名字："+jd.getName()+"  状态："+jd.isStatus());
        System.out.println("开始操作");
        jd.pres();
        System.out.println("操作结束");
        System.out.println("名字："+jd.getName()+"  状态："+jd.isStatus());
    }
}
