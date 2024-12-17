import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class PinnedMessageEditorFrame extends JFrame {

    //공지 수정 페이지
    public PinnedMessageEditorFrame(String currentMessage, JLabel pinnedMessageLabel, String chatRoomName) {
    	getContentPane().setBackground(new Color(255, 255, 255));
        setTitle("공지 수정하기");
        setBounds(100, 100, 400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblMessage = new JLabel(chatRoomName);
        lblMessage.setFont(new Font("휴먼모음T", Font.BOLD, 14));
        lblMessage.setBounds(10, 10, 360, 30);
        getContentPane().add(lblMessage);

        JTextArea messageArea = new JTextArea();
        messageArea.setFont(new Font("휴먼모음T", Font.PLAIN, 14));
        messageArea.setBorder(new LineBorder(new Color(255, 204, 51), 3));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBounds(10, 45, 360, 150);
        getContentPane().add(messageArea);

        JButton btnUpdate = new JButton("수정 완료");
        btnUpdate.setBackground(new Color(255, 204, 51));
        btnUpdate.setFont(new Font("휴먼모음T", Font.BOLD, 12));
        btnUpdate.setBorder(new LineBorder(new Color(255, 204, 51), 1, true));
        btnUpdate.setBounds(10, 210, 84, 25);
        btnUpdate.addActionListener(e -> {
            // 공지 수정 동작 처리
            String message = messageArea.getText().trim();
            if (!message.isEmpty()) {
                pinnedMessageLabel.setText(message);
                // ChattingRoomFrame의 updatePinnedMessage 메소드 호출
                ((ChattingRoomFrame) SwingUtilities.getWindowAncestor(pinnedMessageLabel)).updatePinnedMessage(message);
                dispose();
            }
        });
        getContentPane().add(btnUpdate);

        getContentPane().setLayout(null);
    }
}