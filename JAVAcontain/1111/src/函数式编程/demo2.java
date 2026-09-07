package 函数式编程;

import java.util.Arrays;
import java.util.Comparator;

public class demo2 {
    static void main() {
        String []all={"andy","Anfe","Gg","laoifd","Canf"};
        Arrays.sort(all, String::compareToIgnoreCase);
        //特定方法类型引用
        System.out.println(Arrays.toString(all));
        //上面的其他写法
        Arrays.sort(all, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });

    }
}
