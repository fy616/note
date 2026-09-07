package lists;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class tsst {
    static void main() {
        //并开发异常
        List<String> list = new ArrayList<>();
        list.add("js");
        list.add("json枸杞");
        list.add("鹿晗");
        list.add("黑枸杞");
        list.add("红枸杞");
        System.out.println(list);
//        for(int i=0;i<list.size();i++)
//        {
//            String s1 = list.get(i);
//            if(s1.contains("枸杞"))
//            {
//                list.remove(s1);
//                i--;
//            }
//        }
//        System.out.println(list);
        //解决方法
        //用迭代器
        //创建迭代器
        ListIterator<String> it = list.listIterator();
        while(it.hasNext())
        {
            String s1 = it.next();
            if(s1.contains("枸杞"))
                it.remove();//迭代器删除是最好解决方法
        }
        System.out.println(list);


        }
    }

