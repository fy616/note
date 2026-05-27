package demo_2;

public class Account{
    private double money;
    private String Card;
    //构造方法
    public Account(double money, String card) {
        this.Card = card;
        this.money = money;
    }
    public Account() {
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getCard() {
        return Card;
    }

    public void setCard(String card) {
        Card = card;
    }

    public  void withdraw(double i) {
        String name = Thread.currentThread().getName();
        synchronized (this) {//锁对象this
            if (i <= money) {
                money -= i;
                System.out.println(name + "取钱成功，余额为：" + money);
                System.out.println("取出"+ i+"元");
            } else {
                System.out.println(name + "取钱失败，余额不足");
            }
        }

    }
}
