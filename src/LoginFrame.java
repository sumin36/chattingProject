import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Color;
import javax.swing.JPasswordField;
import javax.swing.border.LineBorder;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JLabel lblNewLabel_1;
    private JLabel lblNewLabel_2;
    private JLabel lblWelcome;
    private JPasswordField passwordField;
    private JLabel lblNewLabel_3;
    private JTextField portField;
    private Socket socket;
    private PrintWriter out;

    // 애플리케이션을 실행하는 메인 함수
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 로그인 프레임의 초기화 및 구성
    public LoginFrame() {
        setTitle("로그인");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 550);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 204, 102));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("Yellow Chat");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setBounds(0, 78, 386, 30);
        lblNewLabel.setFont(new Font("휴먼모음T", Font.BOLD, 22));
        contentPane.add(lblNewLabel);

        textField = new JTextField();
        textField.setBorder(new LineBorder(new Color(255, 204, 102), 1, true));
        textField.setFont(new Font("휴먼모음T", Font.PLAIN, 13));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setBounds(99, 132, 183, 30);
        contentPane.add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("로그인");
        btnNewButton.setBorder(new LineBorder(new Color(255, 204, 102), 1, true));
        btnNewButton.setBackground(new Color(255, 255, 255));
        btnNewButton.setFont(new Font("휴먼모음T", Font.BOLD, 12));
        btnNewButton.setBounds(99, 331, 183, 30);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String id = textField.getText().trim();
                    String port = portField.getText().trim();

                    socket = new Socket("localhost", Integer.parseInt(port));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.println(id);

                    FriendListFrame.setUserName(id);
                    FriendListFrame friendListFrame = new FriendListFrame(socket, out, in);
                    friendListFrame.setLocation(getLocation());
                    friendListFrame.setVisible(true);
                    dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "서버와 연결할 수 없습니다.", "연결 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        contentPane.add(btnNewButton);

        lblNewLabel_1 = new JLabel("Name");
        lblNewLabel_1.setFont(new Font("휴먼모음T", Font.BOLD, 15));
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setBounds(37, 140, 50, 15);
        contentPane.add(lblNewLabel_1);

        lblNewLabel_2 = new JLabel("PW");
        lblNewLabel_2.setFont(new Font("휴먼모음T", Font.BOLD, 15));
        lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_2.setBounds(37, 190, 50, 15);
        contentPane.add(lblNewLabel_2);

        lblNewLabel_3 = new JLabel("Port");
        lblNewLabel_3.setFont(new Font("휴먼모음T", Font.BOLD, 15));
        lblNewLabel_3.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_3.setBounds(37, 245, 50, 15);
        contentPane.add(lblNewLabel_3);

        lblWelcome = new JLabel("Welcome!");
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setFont(new Font("휴먼모음T", Font.BOLD, 22));
        lblWelcome.setBounds(0, 38, 386, 30);
        contentPane.add(lblWelcome);

        passwordField = new JPasswordField();
        passwordField.setBorder(new LineBorder(new Color(255, 204, 102), 1, true));
        passwordField.setBounds(99, 183, 183, 30);
        contentPane.add(passwordField);

        ImageIcon icon = new ImageIcon(getClass().getResource("/img/chat.png"));
        Image img = icon.getImage();
        Image updateImg = img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
        ImageIcon updateIcon = new ImageIcon(updateImg);
        JLabel imgLabel = new JLabel(updateIcon);
        imgLabel.setBounds(123, 390, 125, 82);
        contentPane.add(imgLabel);

        portField = new JTextField();
        portField.setBorder(new LineBorder(new Color(255, 204, 102), 1, true));
        portField.setFont(new Font("휴먼모음T", Font.PLAIN, 13));
        portField.setBounds(99, 237, 183, 30);
        contentPane.add(portField);
        portField.setColumns(10);
    }

}
