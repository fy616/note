package D;

import com.sun.jdi.PathSearchingVirtualMachine;

public class Test {
    static void main() {
        //完成案例
        //设计电影类
        //封装电影数据
        Move arr[] = new Move[5];
        arr[0] = new Move(1,"《唐顿庄园》", 200.0, "郭文景");
        //多来几个电影
        arr[1] = new Move(2,"《唐顿庄园2》", 200.0, "郭文静");
        arr[2] = new Move(3,"《唐顿庄园3》", 200.0, "郭文节");
        arr[3] = new Move(4,"《唐顿庄园4》", 200.0, "郭文杰");
        arr[4] = new Move(5,"《唐顿庄园5》", 200.0, "吴京");
        //负责业务操作
        Moveoperter mo = new Moveoperter(arr);
        mo.show();
        mo.serche(3);

    }
}
