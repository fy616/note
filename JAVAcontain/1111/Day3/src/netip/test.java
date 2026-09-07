package netip;

import java.net.InetAddress;

public class test {
    public static void main(String[] args) {
        //学习InetAddress获取本机IP对象
        try {
            InetAddress  localHost= InetAddress.getLocalHost();
//            System.out.println( localHost);
            System.out.println( localHost.getHostName());
            System.out.println( localHost.getHostAddress());
            //获取对方的IP
            InetAddress  inetAddress= InetAddress.getByName("www.baidu.com");
//            System.out.println( inetAddress);
            System.out.println( inetAddress.getHostName());
            System.out.println( inetAddress.getHostAddress());
            //判断本机是否可以与他访问
            System.out.println( inetAddress.isReachable(5000));
            //获取IP地址
            InetAddress  inetAddress1= InetAddress.getByName("192.168.1.1");
            System.out.println( inetAddress1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
