package 实验7.experiment7_2;

import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ButtonListener 按钮事件监听器类
 *
 * 【知识点说明】
 * - 这个类实现了 ActionListener 接口，专门处理按钮点击事件
 * - 当按钮被点击时，actionPerformed 方法会被自动调用
 * - ActionEvent 对象包含了触发事件的信息，比如点击的是哪个按钮
 *
 * 【工作原理】
 * 1. 点击按钮 → 2. 触发事件 → 3. 调用 actionPerformed → 4. 更新标签
 */
public class ButtonListener implements ActionListener {

    private JLabel displayLabel;

    /**
     * 构造函数
     * @param label 要更新的标签组件
     */
    public ButtonListener(JLabel label) {
        this.displayLabel = label;
    }

    /**
     * 当按钮被点击时，这个方法会被调用
     * @param e 事件对象，包含了点击的按钮信息
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String buttonText = e.getActionCommand();

        displayLabel.setText("你点击了：" + buttonText);
    }
}

