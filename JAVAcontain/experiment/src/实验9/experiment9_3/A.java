package 实验9.experiment9_3;

import javax.swing.*;
import java.awt.*;

public class A {
    public static void main(String[] args) {
        // 创建主窗口
        JFrame frame = new JFrame("多线程模拟时钟");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1)); // 三行一列布局
        
        // 创建三个显示标签，分别用于显示时、分、秒
        JLabel hourLabel = new JLabel("00", SwingConstants.CENTER);
        hourLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        hourLabel.setForeground(Color.RED);
        
        JLabel minuteLabel = new JLabel("00", SwingConstants.CENTER);
        minuteLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        minuteLabel.setForeground(Color.GREEN);
        
        JLabel secondLabel = new JLabel("00", SwingConstants.CENTER);
        secondLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        secondLabel.setForeground(Color.BLUE);
        
        // 添加标题标签
        JLabel hourTitle = new JLabel("时", SwingConstants.CENTER);
        hourTitle.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        
        JLabel minuteTitle = new JLabel("分", SwingConstants.CENTER);
        minuteTitle.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        
        JLabel secondTitle = new JLabel("秒", SwingConstants.CENTER);
        secondTitle.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        
        // 创建面板并添加到窗口
        JPanel hourPanel = new JPanel();
        hourPanel.setLayout(new BorderLayout());
        hourPanel.add(hourTitle, BorderLayout.NORTH);
        hourPanel.add(hourLabel, BorderLayout.CENTER);
        
        JPanel minutePanel = new JPanel();
        minutePanel.setLayout(new BorderLayout());
        minutePanel.add(minuteTitle, BorderLayout.NORTH);
        minutePanel.add(minuteLabel, BorderLayout.CENTER);
        
        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new BorderLayout());
        secondPanel.add(secondTitle, BorderLayout.NORTH);
        secondPanel.add(secondLabel, BorderLayout.CENTER);
        
        frame.add(hourPanel);
        frame.add(minutePanel);
        frame.add(secondPanel);
        
        // 设置窗口大小和位置
        frame.setSize(300, 400);
        frame.setLocationRelativeTo(null); // 居中显示
        
        // 显示窗口
        frame.setVisible(true);
        
        // 创建并启动三个线程，分别显示时、分、秒
        Thread hourThread = new Thread(new TimeThread("时", hourLabel));
        Thread minuteThread = new Thread(new TimeThread("分", minuteLabel));
        Thread secondThread = new Thread(new TimeThread("秒", secondLabel));
        
        hourThread.start();
        minuteThread.start();
        secondThread.start();
        
        System.out.println("多线程时钟程序已启动！");
        System.out.println("创建了三个线程：");
        System.out.println("- 时针线程：" + hourThread.getName());
        System.out.println("- 分针线程：" + minuteThread.getName());
        System.out.println("- 秒针线程：" + secondThread.getName());
    }
}
