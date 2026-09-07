package demo_move;

public class Move {
    private int id;// 编号
    private String name;
    private double price;
    private String acter;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getActer() {
        return acter;
    }

    public void setActer(String acter) {
        this.acter = acter;
    }

    //有参构造器
    public Move(int id, String name, double price, String acter) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.acter = acter;
    }
    // 无参构造器
    public Move() {

    }


}
