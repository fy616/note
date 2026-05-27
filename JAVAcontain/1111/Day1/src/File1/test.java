package FileDemo1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class test {
    static void main() {
        //字符流
        try (FileReader in = new FileReader("D:\\JAVAcontain\\1111\\Day1\\src\\FileDemo1\\demo1.txt");
             FileWriter out = new FileWriter("D:\\JAVAcontain\\1111\\Day1\\src\\FileDemo1\\demo1.txt", true))
            {
                //缓冲字符流包装in
                BufferedReader br = new BufferedReader(in);
                //创建一个字符数组
//                char[] chars = new char[1024];
//                int len;
//                while ((len = in.read(chars)) != -1) {
//                    System.out.println(new String(chars, 0, len));
//                    System.out.println(len);
//                }
//                String str = "我要拿到高薪工作";
//                out.write(str);//写入字符串
//                out.write(78);//写入一个字符，数字78对应字符N
//                out.flush();//把缓存中的数据写入到文件，确保数据写入
                System.out.println(br.readLine());// 读取一行数据,这是缓冲字符流独有的 方法
                br.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


    }
}
