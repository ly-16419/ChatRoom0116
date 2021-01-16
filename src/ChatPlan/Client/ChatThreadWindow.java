package ChatPlan.Client;

import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import java.awt.BorderLayout;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * 聊天线程
 */
public class ChatThreadWindow {
    private String name;
    private JComboBox cb;
    private JFrame f;
    private JTextArea ta;
    private JTextField tf;
    private static int total;// 在线人数统计

    public ChatThreadWindow() {
        /*
         * 设置聊天室窗口界面
         */
        f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setTitle("聊天室" + " - " + name + "     当前在线人数:" + ++total);
        f.setLocation(300, 200);
        ta = new JTextArea();
        JScrollPane sp = new JScrollPane(ta);
        ta.setEditable(false);
        tf = new JTextField();
        cb = new JComboBox();
        cb.addItem("All");
        JButton jb = new JButton("私聊窗口");
        JPanel pl = new JPanel(new BorderLayout());
        pl.add(cb);
        pl.add(jb, BorderLayout.WEST);
        JPanel p = new JPanel(new BorderLayout());
        p.add(pl, BorderLayout.WEST);
        p.add(tf);
        f.getContentPane().add(p, BorderLayout.SOUTH);
        f.getContentPane().add(sp);
        f.setVisible(true);

        /*
            在聊天窗口提示"XXX进入聊天室"
            思路：
            提示相当于Socket收发消息
            1、使用SQL语句查询status状态为"online"的用户的名字、IP、端口
            2、使用分割处理得到的IP
            3、创立UDP连接
            4、向客户端传输message
         */
        showXXXInputRoom();
    }

    public void showXXXInputRoom(){
        String url = "jdbc:oracle:thin:@localhost:1521:orcl";
        String username_db = "opts";
        String password_db = "opts1234";
        Connection conn = null;
        String sql="";
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection(url, username_db, password_db);
            sql = "SELECT password FROM users WHERE username=?";
            pstmt = conn.prepareStatement(sql);
            //pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}