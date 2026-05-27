package experiment4_2;

/**
 * Computer类：创建计算机对象，包含CD类型的成员变量
 */
public class Computer {
    // 核心：CD类型的成员变量，代表计算机中放入的光盘
    private CD includeCD;

    // 构造方法：初始化计算机
    public Computer() {
        System.out.println("计算机已启动，当前无光盘");
        this.includeCD = null; // 初始状态无光盘
    }

    // 方法：放入光盘（给includeCD赋值）
    public void putCD(CD cd) {
        if (this.includeCD == null) {
            this.includeCD = cd;
            System.out.println("成功放入光盘：" + cd.getCdName());
        } else {
            System.out.println("光驱内已有光盘，无法放入新光盘！");
        }
    }

    // 方法：弹出光盘
    public void popCD() {
        if (this.includeCD != null) {
            System.out.println("成功弹出光盘：" + this.includeCD.getCdName());
            this.includeCD = null;
        } else {
            System.out.println("光驱内无光盘，无需弹出！");
        }
    }

    // 方法：读取光盘信息
    public void readCD() {
        if (this.includeCD != null) {
            System.out.println("正在读取光盘信息：");
            System.out.println("光盘名称：" + includeCD.getCdName());
            System.out.println("光盘类型：" + includeCD.getCdType());
        } else {
            System.out.println("读取失败：光驱内没有光盘！");
        }
    }
}
