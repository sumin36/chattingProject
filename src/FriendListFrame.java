import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FriendListFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private static String userName = "Guest";
    private JTable table;
    private DefaultTableModel model;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Set<String> userSet = new HashSet<>();

    // 애플리케이션을 실행하는 메인 함수
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                FriendListFrame frame = new FriendListFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // 기본 생성자: UI 초기화 및 서버 연결
    public FriendListFrame() {
        initComponents();
        connectToServer();
    }

    // 서버와의 연결을 위해 소켓, 출력 스트림, 입력 스트림을 받는 생성자
    public FriendListFrame(Socket socket, PrintWriter out, BufferedReader in) {
        this.socket = socket;
        this.out = out;
        this.in = in;
        initComponents();
        startMessageReceiver();
    }

    // 서버에서 메시지를 수신하는 스레드를 시작하는 메서드
    private void startMessageReceiver() {
        new Thread(this::receiveMessages).start();
    }

    // UI 구성 요소를 초기화하는 메서드
    private void initComponents() {
        setTitle("Friends List");
        setFont(new Font("HY헤드라인M", Font.PLAIN, 12));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 550);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 204, 102));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        String[] columnNames = {"Profile", "Name"};
        model = new DefaultTableModel(columnNames, 0) {
            public Class<?> getColumnClass(int column) {
                return (column == 0) ? ImageIcon.class : String.class;
            }
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 테이블 설정
        table = new JTable(model);
        table.setShowHorizontalLines(false);
        table.setShowGrid(false);
        table.setFocusable(false);
        table.setFocusTraversalKeysEnabled(false);
        table.setAutoscrolls(false);
        table.setAutoCreateColumnsFromModel(false);
        table.setEnabled(false);
        table.setRowHeight(60);
        table.getColumn("Profile").setMaxWidth(80);
        table.getTableHeader().setReorderingAllowed(false);
        table.setFont(new Font("휴먼모음T", Font.BOLD, 12));

        // 스크롤 패널 추가
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // 하단 라벨 설정 (채팅 페이지로 이동하는 버튼)
        JLabel NewLabel = new JLabel("");
        NewLabel.setBackground(new Color(255, 255, 255));
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/img/text.png"));
        NewLabel.setText("<html>"
                + "<table>"
                + "<td width='120'></td>"
                + "<td><img src='" + icon1 + "'></td>"
                + "</table>"
                + "</html>");
        NewLabel.setOpaque(true);
        NewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        NewLabel.setPreferredSize(new Dimension(100, 50));
        NewLabel.setBorder(new EmptyBorder(0, 40, 0, 0));
        contentPane.add(NewLabel, BorderLayout.SOUTH);

        // 채팅 페이지로 이동하는 클릭 이벤트 처리
        NewLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TransChatingPage();
            }
        });

        // 상단 프로필 라벨 설정
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(0,0,0,0));
        contentPane.add(panel, BorderLayout.NORTH);

        JLabel profile = new JLabel("");
        profile.setBackground(new Color(255, 255, 255));
        profile.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
        ImageIcon icon3 = new ImageIcon(getClass().getResource("/img/profile.png"));
        profile.setText("<html>"
                + "<table>"
                + "<tr>"
                + "<td><img src='" + icon3 + "'></td>"
                + "<td>" + userName + "</td>"
                + "</tr>"
                + "</table>"
                + "</html>");
        profile.setOpaque(true);
        profile.setBorder(new EmptyBorder(0, 0, 0, 0));
        profile.setHorizontalAlignment(SwingConstants.LEFT);
        profile.setPreferredSize(new Dimension(300, 50));
        panel.add(profile);
    }

    // 테이블을 반환하는 메서드
    public JTable getTable() {
        return table;
    }

    // 서버에 연결하는 메서드
    private void connectToServer() {
        try {
            socket = new Socket("localhost", 30000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 사용자 이름을 서버에 전송
            out.println(userName);

            // 메시지를 수신하는 스레드 시작
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버 연결 실패", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 서버로부터 메시지를 수신하고 처리하는 메서드
    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("USER:")) {

                    String newUser = message.substring(5).trim();
                    if (!newUser.isEmpty() && !newUser.equals(this.userName)) {
                        addUserToTable(newUser);
                    }
                } else if (message.startsWith("LEAVE:")) {

                    String leftUser = message.substring(6).trim();
                    if (!leftUser.isEmpty()) {
                        removeUserFromTable(leftUser);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 새로운 사용자를 테이블에 추가하는 메서드
    private void addUserToTable(String userName) {
        SwingUtilities.invokeLater(() -> {
            if (!userSet.contains(userName) && !userName.equals(this.userName)) {
                ImageIcon defaultIcon = new ImageIcon(getClass().getResource("/img/profile.png"));
                model.addRow(new Object[]{defaultIcon, userName});
                userSet.add(userName);
            }
        });
    }

    // 테이블에서 사용자를 제거하는 메서드
    private void removeUserFromTable(String userName) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 1).equals(userName)) {
                    model.removeRow(i);
                    userSet.remove(userName);
                    break;
                }
            }
        });
    }

    // 사용자 이름을 설정하는 메서드
    public static void setUserName(String name) {
        userName = name;
    }

    // 현재 사용자 이름을 반환하는 메서드
    public static String getUserName() {
        return userName;
    }

    // 채팅 페이지로 이동하는 메서드
    private void TransChatingPage() {
        ChattingList chatPage = new ChattingList();
        chatPage.setLocation(getLocation());  // 현재 창의 위치를 ChattingList 창에 전달
        chatPage.setVisible(true);
        setVisible(false);  // 현재 창 닫기
    }
}
