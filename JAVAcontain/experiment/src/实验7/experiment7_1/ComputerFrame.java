package 实验7.experiment7_1;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
public class ComputerFrame extends JFrame {
    JMenuBar menubar;
    JMenu choiceGrade; //选择级别的菜单
    JMenuItem  grade1,grade2;
    JTextField textOne,textTwo,textResult;
    JButton getProblem,giveAnwser;
    JLabel operatorLabel,message;
    Teacher teacherZhang;
    ComputerFrame() {
        teacherZhang=new Teacher();
        teacherZhang.setMaxInteger(20);
        setLayout(new FlowLayout());
        menubar = new JMenuBar();
        choiceGrade = new JMenu("选择级别");
        grade1 = new JMenuItem("幼儿级别");
        grade2 = new JMenuItem("儿童级别");
        grade1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                teacherZhang.setMaxInteger(10);
            }
        });
        grade2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                teacherZhang.setMaxInteger(50);
            }
        });
        choiceGrade.add(grade1);
        choiceGrade.add(grade2);
        menubar.add(choiceGrade);
        setJMenuBar(menubar);
      //【代码1】           //创建textOne,其可见字符长是5
        textOne=new JTextField(5);
        textTwo=new JTextField(5);
        textResult=new JTextField(5);
        operatorLabel=new JLabel("+");
        operatorLabel.setFont(new Font("Arial",Font.BOLD,20));
        message=new JLabel("你还没有回答呢");
        getProblem=new JButton("获取题目");
        giveAnwser=new JButton("确认答案");
        add(getProblem);
        add(textOne);
        add(operatorLabel);
        add(textTwo);
        add(new JLabel("="));
        add(textResult);
        add(giveAnwser);
        add(message);
        textResult.requestFocus();
        textOne.setEditable(false);
        textTwo.setEditable(false);
        getProblem.setActionCommand("getProblem");
        textResult.setActionCommand("answer");
        giveAnwser.setActionCommand("answer");
        teacherZhang.setJTextField(textOne,textTwo,textResult);
        teacherZhang.setJLabel(operatorLabel,message);
      //【代码2】//将teacherZhang注册为getProblem的ActionEvent事件监视器
        getProblem.addActionListener(teacherZhang);
//      【代码3】//将teacherZhang注册为giveAnwser的ActionEvent事件监视器
        giveAnwser.addActionListener(teacherZhang);
//      【代码4】//将teacherZhang注册为textResult的ActionEvent事件监视器
        textResult.addActionListener(teacherZhang);
        setVisible(true);
        validate();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
