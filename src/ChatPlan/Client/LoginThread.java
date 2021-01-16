package ChatPlan.Client;

import ChatPlan.uitl.MD5;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * 登录线程
 */
public class LoginThread extends Thread {
    private JFrame loginf;

    private JTextField t;

    public void run() {
        /*
         * 设置登录界面
         */
        loginf = new JFrame();
        loginf.setResizable(false);
        loginf.setLocation(300, 200);
        loginf.setSize(400, 150);
        loginf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginf.setTitle("聊天室" + " - 登录");

        t = new JTextField("Version " + "1.1.0" + "        By liwei");
        t.setHorizontalAlignment(JTextField.CENTER);
        t.setEditable(false);
        loginf.getContentPane().add(t, BorderLayout.SOUTH);

        JPanel loginp = new JPanel(new GridLayout(3, 2));
        loginf.getContentPane().add(loginp);

        JTextField t1 = new JTextField("登录名:");
        t1.setHorizontalAlignment(JTextField.CENTER);
        t1.setEditable(false);
        loginp.add(t1);

        final JTextField loginname = new JTextField("liwei");
        loginname.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginname);

        JTextField t2 = new JTextField("密码:");
        t2.setHorizontalAlignment(JTextField.CENTER);
        t2.setEditable(false);
        loginp.add(t2);

        final JTextField loginPassword = new JTextField("lw1234");
        loginPassword.setHorizontalAlignment(JTextField.CENTER);
        loginp.add(loginPassword);
        /*
         * 监听退出按钮(匿名内部类)
         */
        JButton b1 = new JButton("退  出");
        loginp.add(b1);
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        final JButton b2 = new JButton("登  录");
        loginp.add(b2);

        loginf.setVisible(true);

        /**
         * 监听器,监听"登录"Button的点击和TextField的回车
         */
        class ButtonListener implements ActionListener {
            private Socket s;

            public void actionPerformed(ActionEvent e) {
                String username = loginname.getText();
                String password = loginPassword.getText();
                PreparedStatement pstmt = null;
                String sql = "";
                try {
                    String url = "jdbc:oracle:thin:@localhost:1521:orcl";
                    String username_db = "opts";
                    String password_db = "opts1234";
                    Connection conn = DriverManager.getConnection(url, username_db, password_db);
                    sql = "SELECT password FROM users WHERE username=?";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String encodePassword = rs.getString("PASSWORD");
                        if (MD5.checkpassword(password, encodePassword)) {
                            //获取IP地址，将其添加到数据库中
                            InetAddress addr = InetAddress.getLocalHost();
                            System.out.println("本机IP地址: " + addr.getHostAddress());

                            //异常处理端口占用问题
                            int port = 1688;
                            while (true) {
                                try {
                                    //创建在port端口基础上的连接，如果被占用则++
                                    DatagramSocket soc = new DatagramSocket(port);
                                    System.out.println("port：" + port + "，调试完成！");
                                    break;
                                } catch (SocketException ex) {
                                    port++;
                                    System.out.println("端口被占用，正在调试，请稍等");
                                }
                            }

                            sql = "UPDATE users SET ip=?,port=?,status=? WHERE username=?";
                            pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, addr.getHostAddress());
                            pstmt.setInt(2, port);
                            pstmt.setString(3, "online");//如果登录成功，则将登录状态该为online
                            pstmt.setString(4, username);
                            pstmt.executeUpdate();
                            //隐藏登陆窗口，打开聊天窗口
                            loginf.setVisible(false);
                            ChatThreadWindow chatThreadWindow = new ChatThreadWindow();
                        } else {
                            System.out.println("登录失败");
                        }
                    } else {
                        System.out.println("未能获取数据");
                    }

                } catch (SQLException ee) {
                    ee.printStackTrace();
                } catch (NoSuchAlgorithmException ex) {
                    ex.printStackTrace();
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }
				/*
				1、根据用户去数据库把加密后的密码拿到
				SELECT password FROM users WHERE username='liwei';
				2、把登录界面输入的密码和数据库里加密后的进行比对（调用MD5类的checkpassword方法）
				 */
            }
        }


        ButtonListener bl = new ButtonListener();
        b2.addActionListener(bl);
        loginname.addActionListener(bl);
        loginPassword.addActionListener(bl);
    }
}