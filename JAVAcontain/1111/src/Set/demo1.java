package Set;

import java.util.HashSet;

public class demo1 {
    public static void main(String[] args) {
        //创建TreeSet对象,
        HashSet<Integer> set = new HashSet<>();
        set.add(1);
        set.add(9);
        set.add(5);
        set.add(2);
        //treeset的特点，会自动排序
        System.out.println(set);
    }
}
