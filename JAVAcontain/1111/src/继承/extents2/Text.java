package 继承.extents2;

public class Text {
    static void main() {
        FU f=new FU();
//        f.privateshow();不能访问私有
        f.method();
        f.publicshow();
        f.protectedshow();
    }
}
