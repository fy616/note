package 实验9.experiment9_2;

public class TwoThreadGuessNumber {
    public static void main(String args[]) {
        Number number=new Number();
        number.giveNumberThread.start();
        number.guessNumberThread.start();
    }
}