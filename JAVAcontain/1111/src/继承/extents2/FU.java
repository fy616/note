package 继承.extents2;

public class FU {
    private void privateshow() {
        System.out.println("private2222");
    }
    void method() {
        System.out.println("wuxushi3333");
    }
    protected void protectedshow() {
        System.out.println("protected4444");
    }
    public void publicshow() {
        System.out.println("public5555");
    }
    public static void main(String[] args) {
        FU f=new FU();
        f.privateshow();
        f.method();
        f.publicshow();
        f.protectedshow();

    }
}
