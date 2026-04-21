package experiment4;

public class Fight {
    static void main() {
        //编写一个Java程序，该程序中有两个类：Tank（用于刻画坦克）和Fight（主类）。
        // Tank类有一个double类型的变量speed，用于刻画坦克的速度；一个int型变量bulletAmount，
        // 用于刻画坦克的炮弹数量。Tank类定义了speedUp()和speedDown()方法，体现坦克有加速、减速行为
        // ；定义了setBulletAmount(int p)方法，用于设置坦克炮弹的数量；定义了fire()方法，
        // 体现坦克有开炮行为。在主类Fight的main方法中用Tank类创建坦克，并让坦克调用方法设置炮弹的数量
        // ，显示坦克的加速、减速和开炮等行为
        Tank tank = new Tank();
        tank.setBulletAmount(10);
        tank.speedup(10);
        tank.fire();
    }
}
