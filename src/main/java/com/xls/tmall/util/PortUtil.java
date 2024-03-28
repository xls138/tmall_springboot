//工具类，用于判断某个端口是否启动。
package com.xls.tmall.util;

import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JOptionPane;

public class PortUtil {

    //尝试在指定的端口上创建一个ServerSocket。
    //如果创建成功并且能够立即关闭这个ServerSocket，那么这个方法会返回false，表示该端口当前没有被使用。
    //如果在尝试创建ServerSocket时抛出BindException，这意味着端口已被占用，因此该方法返回true。
    //如果抛出其他类型的IOException，也认为端口不可用，同样返回true。
    public static boolean testPort(int port) {
        try {
            ServerSocket ss = new ServerSocket(port);
            ss.close();
            return false;
        } catch (java.net.BindException e) {
            return true;
        } catch (IOException e) {
            return true;
        }
    }


    //使用testPort方法来检查指定端口是否被占用。如果testPort返回false（表示端口未被占用），根据shutdown参数的值执行不同的操作：
    //如果shutdown为true，则会显示一个消息对话框，内容是指定的server服务在port端口未启动的信息，然后通过System.exit(1)终止程序。
    //如果shutdown为false，则会显示一个确认对话框，询问用户是否继续。如果用户选择不继续（即不点击确认按钮），程序同样会通过System.exit(1)终止。
    public static void checkPort(int port, String server, boolean shutdown) {
        if (!testPort(port)) {
            if (shutdown) {
                String message = String.format("在端口 %d 未检查得到 %s 启动%n", port, server);
                JOptionPane.showMessageDialog(null, message);
                System.exit(1);
            } else {
                String message = String.format("在端口 %d 未检查得到 %s 启动%n,是否继续?", port, server);
                if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(null, message))
                    System.exit(1);


            }
        }
    }

}

