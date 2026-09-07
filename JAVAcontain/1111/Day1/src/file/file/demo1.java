package file.file;

import java.io.*;

public class demo1 {
    public static void main(String[] args) throws IOException {
        //创建一个文件对象
        File f1=new File("D:\\Java\\file\\demo1.txt");
        //创建文件输入输出流
        try (  InputStream in=new FileInputStream(f1)){
            byte[] b=new byte[1024];
            int len=0;
            b=in.readAllBytes();
            System.out.println(new String(b));//输出文件内容,字节转换为字符串
        } catch (Exception e) {
            e.printStackTrace();
        }

        try(OutputStream out=new FileOutputStream(f1)) {
            //输出步骤
            String s="hello world";
            byte[] b=s.getBytes();//字符串转换为字节,存入字节数组，方便输出
            out.write(b);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }
}
