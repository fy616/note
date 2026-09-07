package 实验3.experiment3_3;

import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("请输入一段英文文字：");
        String input = scanner.nextLine();
        
        // 计算字符数（不包括空格）
        int charCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isWhitespace(input.charAt(i))) {
                charCount++;
            }
        }
        
        // 计算单词数
        String[] words = input.trim().split("\\s+");
        int wordCount = 0;
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount++;
            }
        }
        
        // 计算句子数（以"?"、"!"、"."结束）
        int sentenceCount = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '?' || c == '!' || c == '.') {
                sentenceCount++;
            }
        }
        
        System.out.println("\n统计结果：");
        System.out.println("字符数：" + charCount);
        System.out.println("单词数：" + wordCount);
        System.out.println("句子数：" + sentenceCount);
        
        scanner.close();
    }
}
