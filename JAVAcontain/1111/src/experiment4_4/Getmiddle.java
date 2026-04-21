package experiment4_4;

public class Getmiddle {
    Point a,b;
    public Getmiddle(Point a, Point b) {
        this.a = a;
        this.b = b;
    }
    public static void getMiddle(Point p1, Point p2) {
        Point  middle = new Point();
         middle.setX((p1.getX() + p2.getX()) / 2);
         middle.setY((p1.getY() + p2.getY()) / 2);
         middle.show();

    }
}
