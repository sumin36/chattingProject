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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChattingRoomFrame extends JFrame {
    private JPanel contentPane;
    private JTextField textField;
    private JTextPane chatArea;
    private PrintWriter out;
    private BufferedReader in;
    private String chatRoomName;
    private Socket socket;
    private JLabel pinnedMessageLabel;
    private String pinnedMessage = "공지 없음"; // 기본 공지 메시지

    public ChattingRoomFrame(String chatRoomName, List<String> participants, Socket socket, PrintWriter out, BufferedReader in) {
        this.chatRoomName = chatRoomName;
        this.out = out;
        this.in = in;

        setTitle(chatRoomName);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 550);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //공지 레이아웃 설정
        pinnedMessageLabel = new JLabel(pinnedMessage); // 초기 공지 설정
        pinnedMessageLabel.setOpaque(true);
        pinnedMessageLabel.setBackground(new Color(255, 250, 205)); // 밝은 노란색
        pinnedMessageLabel.setFont(new Font("휴먼모음T", Font.BOLD, 13));
        pinnedMessageLabel.setBorder(new LineBorder(new Color(255, 204, 102), 2));
        pinnedMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pinnedMessageLabel.setBounds(8, 50, 366, 30);
        contentPane.add(pinnedMessageLabel);

        //채팅방 상단 닉네임
        JLabel lblNewLabel = new JLabel(chatRoomName);
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("휴먼모음T", Font.BOLD, 16));
        lblNewLabel.setBounds(111, 10, 168, 24);
        contentPane.add(lblNewLabel);

        JLabel userNameLabel = new JLabel(FriendListFrame.getUserName());
        userNameLabel.setFont(new Font("휴먼모음T", Font.PLAIN, 13));
        userNameLabel.setBounds(10, 470, 100, 32);
        userNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        userNameLabel.setForeground(Color.GRAY);
        contentPane.add(userNameLabel);

        textField = new JTextField();
        textField.setBorder(new LineBorder(new Color(255, 204, 0), 3, true));
        textField.setBackground(new Color(255, 255, 255));
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField.setBounds(50, 470, 200, 32);
        contentPane.add(textField);
        textField.setColumns(10);

        // 이모티콘 버튼 설정
        JButton btnEmoji = new JButton("+");
        btnEmoji.setBorder(new LineBorder(new Color(255, 204, 0), 3, true));
        btnEmoji.setBackground(new Color(255, 255, 255));
        btnEmoji.setFont(new Font("휴먼모음T", Font.BOLD, 14));
        btnEmoji.setBounds(260, 470, 40, 32);

        btnEmoji.addActionListener(e -> {
            // EmoticonFrame 생성
            EmoticonFrame frame = new EmoticonFrame(this);

            // 현재 ChattingRoomFrame 위치를 가져와서 EmoticonFrame의 위치 설정
            Point location = getLocation();
            frame.setLocation(location.x + getWidth(), location.y); // 오른쪽에 띄우기 위해 x 좌표에 창 너비 추가

            // 이모티콘 창을 보이도록 설정
            frame.setVisible(true);
        });

        contentPane.add(btnEmoji);

        //전송 버튼 설정
        JButton btnSend = new JButton("전송");
        btnSend.setBorder(new LineBorder(new Color(255, 204, 0), 3, true));
        btnSend.setBackground(new Color(255, 204, 102));
        btnSend.setFont(new Font("휴먼모음T", Font.BOLD, 14));
        btnSend.setBounds(310, 470, 70, 32);
        contentPane.add(btnSend);

        textField.addActionListener(e -> sendMessage());
        btnSend.addActionListener(e -> sendMessage());

        // 예약 버튼 설정
        JButton btnNewButton_2 = new JButton("메시지 예약");
        btnNewButton_2.setBackground(new Color(255, 204, 102));
        btnNewButton_2.setBorder(new LineBorder(new Color(255, 204, 0), 2, true));
        btnNewButton_2.setFont(new Font("휴먼모음T", Font.PLAIN, 12));
        btnNewButton_2.setActionCommand("예약 메시지");
        btnNewButton_2.setBounds(281, 12, 93, 23);

        // 버튼 클릭 시 예약 메시지 창을 오른쪽에 띄우도록 수정
        btnNewButton_2.addActionListener(e -> {
            // ReservationMessageFrame 생성
            ReservationMessageFrame frame = new ReservationMessageFrame(socket, out, chatRoomName);

            // 현재 ChattingRoomFrame 위치를 가져와서 ReservationMessageFrame의 위치 설정
            Point location = getLocation();
            frame.setLocation(location.x + getWidth(), location.y); // 오른쪽에 띄우기 위해 x 좌표에 창 너비 추가

            // 예약 메시지 창을 보이도록 설정
            frame.setVisible(true);
        });

        contentPane.add(btnNewButton_2);

        //채팅방 표시하는 틀
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(new LineBorder(new Color(255, 255, 255)));
        scrollPane.setBounds(8, 80, 366, 380);
        contentPane.add(scrollPane);

        chatArea = new JTextPane();
        chatArea.setBackground(new Color(255, 255, 255));
        chatArea.setMargin(new Insets(15, 10, 2, 2));
        chatArea.setEditable(false);
        scrollPane.setViewportView(chatArea);
        chatArea.setFont(new Font("휴먼모음T", Font.PLAIN, 13));
        chatArea.setBorder(new LineBorder(new Color(255, 204, 102), 3, true));

        connectToServer();


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeChatConnection();
            }
        });

        pinnedMessageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (pinnedMessageLabel.getText().equals("공지 없음")) {
                        // 공지 없음일 때 ShowPinnedMessageFrame 띄우기
                        ShowPinnedMessageFrame showFrame = new ShowPinnedMessageFrame(pinnedMessageLabel, chatRoomName);

                        // 현재 ChattingRoomFrame 위치를 가져와서 오른쪽에 띄우기
                        Point location = getLocation();
                        showFrame.setLocation(location.x + getWidth(), location.y); // 오른쪽에 띄우기 위해 x 좌표에 창 너비 추가

                        showFrame.setVisible(true);
                    } else {
                        // 공지 있을 때 PinnedMessageEditorFrame 띄우기
                        PinnedMessageEditorFrame editorFrame = new PinnedMessageEditorFrame(pinnedMessageLabel.getText(), pinnedMessageLabel, chatRoomName);

                        // 현재 ChattingRoomFrame 위치를 가져와서 오른쪽에 띄우기
                        Point location = getLocation();
                        editorFrame.setLocation(location.x + getWidth(), location.y); // 오른쪽에 띄우기 위해 x 좌표에 창 너비 추가

                        editorFrame.setVisible(true);
                    }
                }
            }
        });

    }

    public void setParentLocation(Point parentLocation) {
        if (parentLocation != null) {
            setLocation(parentLocation);
        }
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 30000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 사용자 이름 전송
            out.println(FriendListFrame.getUserName());

            // 메시지 수신 스레드 시작
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("CHAT:")) {
                    String[] parts = message.split(":", 4);
                    final String chatRoomName = parts[1];
                    final String sender = parts[2];
                    final String chatMessage = parts[3];

                    if (chatRoomName.equals(this.chatRoomName)) {
                        SwingUtilities.invokeLater(() -> {
                            if (chatMessage.startsWith("[이모티콘_") && chatMessage.endsWith("]")) {
                                int emoticonNumber = Integer.parseInt(chatMessage.substring(6, chatMessage.length() - 1));
                                displayEmoticon(sender, emoticonNumber, sender.equals(FriendListFrame.getUserName()));
                            } else {
                                displayMessage(sender, chatMessage, sender.equals(FriendListFrame.getUserName()));
                            }
                        });
                    }
                } else if (message.startsWith("PINNED_MESSAGE:")) {
                    String[] parts = message.split(":", 3);
                    if (parts.length == 3 && parts[1].equals(this.chatRoomName)) {
                        final String pinnedMessage = parts[2];
                        SwingUtilities.invokeLater(() -> {
                            pinnedMessageLabel.setText(pinnedMessage);
                        });
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String message = textField.getText().trim();
        if (!message.isEmpty()) {
            out.println("CHAT:" + chatRoomName + ":" + FriendListFrame.getUserName() + ":" + message);
            textField.setText("");
        }
    }

    private void displayMessage(String sender, String message, boolean isOwnMessage) {
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWord, Color.BLACK);
        StyleConstants.setBackground(keyWord, isOwnMessage ? new Color(255, 255, 200) : Color.WHITE);
        StyleConstants.setAlignment(keyWord, isOwnMessage ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = now.format(formatter);

        try {
            doc.insertString(doc.getLength(), "[" + time + "] " + sender + ": " + message + "\n", keyWord);
            doc.setParagraphAttributes(doc.getLength() - 1, 1, keyWord, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        scrollToBottom();
    }

    // 이모티콘 보내기
    public void sendEmoticon(int emoticonNumber) {
        String emoticonMessage = "[이모티콘_" + emoticonNumber + "]";
        out.println("CHAT:" + chatRoomName + ":" + FriendListFrame.getUserName() + ":" + emoticonMessage);
    }

    // 이모티콘 표시
    private void displayEmoticon(String sender, int emoticonNumber, boolean isOwnMessage) {
        StyledDocument doc = chatArea.getStyledDocument();
        SimpleAttributeSet keyWord = new SimpleAttributeSet();
        StyleConstants.setAlignment(keyWord, isOwnMessage ? StyleConstants.ALIGN_RIGHT : StyleConstants.ALIGN_LEFT);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = now.format(formatter);

        try {
            doc.insertString(doc.getLength(), "[" + time + "] " + sender + ": ", keyWord);

            String imagePath = "/img/상상부기_" + emoticonNumber + ".png";
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
                chatArea.setCaretPosition(doc.getLength());
                chatArea.insertIcon(new ImageIcon(image));
            }

            doc.insertString(doc.getLength(), "\n", keyWord);
            doc.setParagraphAttributes(doc.getLength() - 1, 1, keyWord, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        scrollToBottom();
    }

    private void closeChatConnection() {
        try {
            if (out != null) {
                out.println("EXIT:" + chatRoomName + ":" + FriendListFrame.getUserName());
                out.flush();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) chatArea.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    // 공지 메시지 갱신 함수
    public void updatePinnedMessage(String newMessage) {
        out.println("UPDATE_PINNED_MESSAGE:" + newMessage+"," + chatRoomName);
        out.flush();
    }

}