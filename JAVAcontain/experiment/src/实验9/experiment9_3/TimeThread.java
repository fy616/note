package 实验9.experiment9_3;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeThread implements Runnable {
    private String timeType; // 时间类型：时、分、秒
    private JLabel displayLabel; // 显示标签
    
    public TimeThread(String timeType, JLabel displayLabel) {
        this.timeType = timeType;
        this.displayLabel = displayLabel;
    }
    
    @Override
    public void run() {
        SimpleDateFormat format;
        int sleepTime;
        
        // 根据时间类型设置不同的格式和刷新间隔
        switch (timeType) {
            case "时":
                format = new SimpleDateFormat("HH");
                sleepTime = 60000; // 每分钟更新一次小时
                break;
            case "分":
                format = new SimpleDateFormat("mm");
                sleepTime = 1000; // 每秒更新一次分钟
                break;
            case "秒":
                format = new SimpleDateFormat("ss");
                sleepTime = 1000; // 每秒更新一次秒
                break;
            default:
                format = new SimpleDateFormat("HH:mm:ss");
                sleepTime = 1000;
        }
        
        while (true) {
            try {
                Date now = new Date();
                String timeStr = format.format(now);
                
                // 在Swing线程中更新UI
                SwingUtilities.invokeLater(() -> {
                    displayLabel.setText(timeStr);
                });
                
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.out.println(timeType + "线程被中断");
                break;
            }
        }
    }
}
