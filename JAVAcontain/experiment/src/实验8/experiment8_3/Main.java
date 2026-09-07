package 实验8.experiment8_3;

import  java.io.*;
public class Main {
    public static void main(String []args){
        File file=new File("D:\\JAVAcontain\\experiment\\test.txt");
        File file1=new File("D:\\JAVAcontain\\experiment\\src\\experiment8_3\\new");
        try(   FileInputStream fileInputStream=new FileInputStream(file);
               FileOutputStream fileOutputStream=new FileOutputStream(file1);
                InputStreamReader s=new InputStreamReader(fileInputStream);
                OutputStreamWriter ss=new OutputStreamWriter(fileOutputStream);
                BufferedReader bufferedReader=new BufferedReader(s);
                BufferedWriter bufferedWriter=new BufferedWriter(ss);
        ) {
            String sf;
            while( (sf=bufferedReader.readLine())!=null ){
                StringBuilder stringBuilder=new StringBuilder(sf);
                stringBuilder.append(";");
                String op=new String(stringBuilder);
                bufferedWriter.write(op);
                bufferedWriter.newLine();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
