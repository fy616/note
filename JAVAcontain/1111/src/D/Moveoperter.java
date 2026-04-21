package D;

import java.util.Scanner;

public class Moveoperter {
    private Move[] move;

    public Moveoperter() {
    }
    public Moveoperter(Move[] move) {
        this.move = move;
    }
    public void show() {
        for(int i=0;i<move.length;i++){
            Move m=move[i];
            System.out.println(move[i].getId()+"\t"+m.getName()+"\t"+m.getPrice()+"\t"+m.getActer());

        }

    }

    public void serche( int id) {
        for(int i=0;i<move.length;i++){
            if(move[i].getId()==id){
                System.out.println(move[i].getId()+"\t"+move[i].getName()+"\t"+move[i].getPrice()+"\t"+move[i].getActer());
            }
        }

    }
}
