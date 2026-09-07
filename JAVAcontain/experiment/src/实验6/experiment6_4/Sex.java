package 实验6.experiment6_4;

public class Sex extends RuntimeException {
    public Sex(String message) {
        super(message);
        System.out.println(message);
    }
}
