package file.file;

import java.io.*;

public class demo2 {
    public static void main(String[] args) {
        //建立一个与文件联通的IO流
        try (FileInputStream in = new FileInputStream("D:\\JAVAcontain\\1111\\Day1\\src\\dilei.txt");
             FileOutputStream out = new FileOutputStream("D:\\JAVAcontain\\1111\\Day1\\src\\file\\file\\demo.txt", true);) {

            //读取1文件内容并写入到目标文件
//            int b;
//            while ((b = in.read()) != -1) {
//                out.write(b);
//            }
            //字节数组
            byte[] bytes = new byte[1024];
            int len;
            while ((len = in.read(bytes)) != -1) {
                out.write(bytes, 0, len);
            }

            //一次性读取所有字节
//            byte[] bytes;
//            bytes = in.readAllBytes();
//            out.write(bytes);
            System.out.println("文件复制完成!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
