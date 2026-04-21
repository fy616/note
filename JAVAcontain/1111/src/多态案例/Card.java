package 多态案例;

public class Card {
    private String name;
    private  String Carid;
    private String phone;
    private double money;
    private  Card()
    {}
    public Card(String name, String carid, String phone, double money) {
        this.name = name;
        this.Carid = carid;
        this.phone = phone;
        this.money = money;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarid() {
        return Carid;
    }

    public void setCarid(String carid) {
        Carid = carid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double  money) {
        this.money = money;
    }
    public void pay(double money)
    {
        this.money-=money;
    }

    public void consume(int  money) {

    }
}
