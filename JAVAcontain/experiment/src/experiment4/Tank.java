package experiment4;

public class Tank {
    int speed;
    int  bulletAmount;
    public void speeddown(int d) {
        if (this.speed - d > 0) {
            this.speed -= d;
            System.out.println("减速");

        }
    }
    public void speedup(int u) {
        this.speed += u;
        System.out.println("加速");
    }
    public void setBulletAmount(int  b) {
        this.bulletAmount = b;
        System.out.println("装炮弹数量");
    }
    public void fire() {
        if (this.bulletAmount > 0) {
            System.out.println("开炮");
            this.bulletAmount--;
        }
    }
}
