package 继承.extents3;

import 继承.extents2.FU;

public class son extends FU {
    public void show() {
        protectedshow();
        publicshow();
        //子类只能访问父类的protected和public方法
    }
}
