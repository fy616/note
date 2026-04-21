package 多态案例;

public class glodcard extends   Card {
    public glodcard(String name, String carid, String phone, double money) {
        super(name, carid, phone, money);
    }
    public void pay(int  money) {
        this.setMoney(this.getMoney()+money);
    }
    public void consume(int money) {
        //余额不足时
        if(this.getMoney()<money) {
            System.out.println("余额不足");
            return;
        }

        this.setMoney(this.getMoney()-money*0.8);
        System.out.println("使用金卡消费"+money+"元");
        if(money>=200)
        {
            System.out.println("您消费达到200，获得洗车卡");
        }
        System.out.println("当前余额为"+this.getMoney());
    }
}
