package 实验7.experiment7_2;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;

/**
 * Main 主程序类
 *
 * 【题目要求】
 * 在 JFrame 中加入 2 个按钮（JButton）和 1 个标签（JLabel）
 * 单击两个按钮，显示按钮的标签于 JLabel
 *
 * 【知识点说明】
 * - JFrame：窗口容器，是 Swing GUI 程序的基础
 * - JButton：按钮组件，用户可以点击
 * - JLabel：标签组件，用于显示文字
 * - FlowLayout：流式布局，组件从左到右排列
 * - ActionListener：动作监听器，处理按钮点击事件
 */
public class Main {
    public static void main(String[] args) {

        // ========== 创建窗口 ==========
        JFrame frame = new JFrame("按钮事件演示");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // ========== 创建按钮 ==========
        JButton button1 = new JButton("按钮一");
        JButton button2 = new JButton("按钮二");

        // ========== 创建标签 ==========
        JLabel label = new JLabel("请点击按钮");

        // ========== 注册事件监听器 ==========
        ButtonListener listener = new ButtonListener(label);
        button1.addActionListener(listener);
        button2.addActionListener(listener);

        // ========== 添加组件到窗口 ==========
        frame.add(button1);
        frame.add(button2);
        frame.add(label);

        // ========== 显示窗口 ==========
        frame.setVisible(true);
    }
}
