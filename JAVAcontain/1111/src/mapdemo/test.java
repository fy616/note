package mapdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public record test() {
    static void main() {
        //现在有四个景点，请记录一个班级上80个人的意愿，求出投票人数最高的景点
        String [] spots = {"长白山", "峨眉山", "天安门", "上海外滩"};
        //用集合存储
        List<String> list = new ArrayList<>();
        Random random = new Random();
        for(int i=0; i<80; i++)
        {
            int index = random.nextInt(spots.length);
            list.add(spots[index]);
        }
        //统计
        Map<String, Integer> map = new java.util.HashMap<>();
        for(String spot : list)
        {
            if(map.containsKey(spot))
            {
                map.put(spot, map.get(spot) + 1);
            }
            else
            {
                map.put(spot, 1);
            }
        }
        //输出投票最高的景点
        String maxSpot = "";
        int maxCount = 0;
        for(Map.Entry<String, Integer> entry : map.entrySet())
        {
            if(entry.getValue() > maxCount)
            {
                maxSpot = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        System.out.println("投票最高的景点是：" + maxSpot + "，共有" + maxCount + "人投票");
        System.out.println("投票结果：" + map);

    }
}
