package 实验4.experiment4_2;

/**
 * 主类：程序入口，模拟用户使用计算机放入CD的操作
 */
public class User {
    public static void main(String[] args) {
        // 1. 创建一台计算机对象
        Computer myComputer = new Computer();
        System.out.println("------------------------");

        // 2. 创建一张CD光盘对象
        CD musicCD = new CD("周杰伦精选集", "音乐光盘");

        // 3. 计算机放入这张CD
        myComputer.putCD(musicCD);
        System.out.println("------------------------");

        // 4. 计算机读取CD信息
        myComputer.readCD();
        System.out.println("------------------------");

        // 5. 计算机弹出CD
        myComputer.popCD();
        System.out.println("------------------------");

        // 测试：无光盘时读取
        myComputer.readCD();
    }
}