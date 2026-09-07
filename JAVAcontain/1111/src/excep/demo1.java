
    package excep;

    public class demo1 {
        static void main() {
            try {
                show(10,0);
            } catch (Exception e) {
                System.out.println("异常");
                System.out.println(e.getMessage());
            }
        }
        public static void show(int a, int b) throws Exception {
            int c=a/b;
            System.out.println(c);

        }
    }

