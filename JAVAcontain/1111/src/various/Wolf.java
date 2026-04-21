package various;

public class Wolf extends Animal{
    String name="Wolf";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void run(){
        System.out.println("Wolf is fast");
    }
    public void attack(){
        System.out.println("Wolf is attack");
    }
}
