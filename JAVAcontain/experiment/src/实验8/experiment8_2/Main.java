package 实验8.experiment8_2;

import java.io.*;
public class Main {
    public static void main(String []args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String content = br.readLine();
        br.close();

        //2.FileOutputStream写入文件
        FileOutputStream fos = new FileOutputStream("test.txt");
        fos.write(content.getBytes());
        fos.close();

        //3.FileInputStream读取文件并输出
        FileInputStream fis = new FileInputStream("test.txt");
        byte[] buf = new byte[1024];
        int len;
        while((len = fis.read(buf)) != -1){
            System.out.print(new String(buf,0,len));
        }
        fis.close();
        }

}
