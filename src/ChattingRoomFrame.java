import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ChattingRoomFrame extends JFrame {
    private JPanel contentPane;
    private JTextField textField;
    private JTextPane chatArea;
    private PrintWriter out;
    private BufferedReader in;
    private String chatRoomName;
    private List<String> participants;

    public ChattingRoomFrame(String chatRoomName, List<String> participants, Socket socket, PrintWriter out, BufferedReader in) {
        this.chatRoomName = chatRoomName;
        this.participants = participants;
        this.out = out;
        this.in = in;

        setTitle(chatRoomName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 550);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel(chatRoomName);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("휴먼모음T", Font.BOLD, 16));
        lblNewLabel.setBounds(111, 10, 168, 24);
        contentPane.add(lblNewLabel);

        textField = new JTextField();
        textField.setBorder(new LineBorder(new Color(255, 204, 0), 3, true));
        textField.setBackground(new Color(255, 255, 255));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setBounds(8, 470, 302, 32);
        contentPane.add(textField);
        textField.setColumns(10);

        JButton btnNewButton = new JButton("전송");
        btnNewButton.setBorder(new LineBorder(new Color(255, 204, 0), 3, true));
        btnNewButton.setBackground(new Color(255, 204, 102));
        btnNewButton.setFont(new Font("휴먼모음T", Font.BOLD, 14));
        btnNewButton.setBounds(315, 470, 59, 32);
        contentPane.add(btnNewButton);

        textField.addActionListener(e -> sendMessage());
        btnNewButton.addActionListener(e -> sendMessage());
        new Thread(this::receiveMessages).start();

        JButton btnNewButton_2 = new JButton("메시지 예약");
        btnNewButton_2.setBackground(new Color(255, 204, 102));
        btnNewButton_2.setBorder(new LineBorder(new Color(255, 204, 0), 2, true));
        btnNewButton_2.setFont(new Font("휴먼모음T", Font.PLAIN, 12));
        btnNewButton_2.setActionCommand("예약 메시지");
        btnNewButton_2.setBounds(281, 12, 93, 23);
        btnNewButton_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ReservationMessageFrame frame=new ReservationMessageFrame();
                frame.setVisible(true);
                dispose(); //현재 로그인창 닫기
            }
        });
        contentPane.add(btnNewButton_2);


        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new LineBorder(new Color(255, 255, 255)));
        scrollPane.setBounds(8, 44, 366, 416);
        contentPane.add(scrollPane);

        chatArea = new JTextPane();
        chatArea.setBackground(new Color(255, 255, 255));
        chatArea.setMargin(new Insets(15, 10, 2, 2));
        chatArea.setEditable(false);
        scrollPane.setViewportView(chatArea);
        chatArea.setFont(new Font("휴먼모음T", Font.PLAIN, 13));
        chatArea.setBorder(new LineBorder(new Color(255, 204, 102), 3, true));
    }

    private void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            out.println("CHAT:" + chatRoomName + ":" + FriendListFrame.getUserName() + ":" + message);
            displayMessage(FriendListFrame.getUserName(), message, true);
            textField.setText("");
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("CHAT:" + chatRoomName + ":")) {
                    String[] parts = message.split(":", 4);
                    final String sender = parts[2];
                    final String chatMessage = parts[3];
                    SwingUtilities.invokeLater(() -> displayMessage(sender, chatMessage, false));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayMessage(String sender, String message, boolean isOwnMessage) {
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLACK);
        StyleConstants.setBackground(keyWord, isOwnMessage ? new Color(255, 255, 200) : Color.WHITE);
        StyleConstants.setAlignment(keyWord, isOwnMessage ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);

        try {
            doc.insertString(doc.getLength(), sender + ": " + message + "\n", keyWord);
            doc.setParagraphAttributes(doc.getLength() - 1, 1, keyWord, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
