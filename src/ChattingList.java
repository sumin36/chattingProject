import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.util.Arrays;
import java.util.List;

public class ChattingList extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private DefaultTableModel tableModel;
    private JTable table;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChattingList() {
        setTitle("Chating List");
        setFont(new Font("HY헤드라인M", Font.PLAIN, 12));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 550);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnNewMenu = new JMenu("+");
        mnNewMenu.setFont(new Font("휴먼모음T", Font.BOLD, 18));
        mnNewMenu.setBackground(new Color(255, 255, 255));
        mnNewMenu.setForeground(new Color(0, 0, 0));
        menuBar.add(mnNewMenu);

        JMenuItem mntmNewMenuItem = new JMenuItem("채팅방 생성");
        mntmNewMenuItem.setFont(new Font("휴먼모음T", Font.PLAIN, 13));
        mntmNewMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FriendListFrame friendListFrame = new FriendListFrame();
                ChooseFriendFrame frame = new ChooseFriendFrame(friendListFrame, ChattingList.this);
                frame.setVisible(true);
            }
        });
        mnNewMenu.add(mntmNewMenuItem);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 204, 102));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        tableModel = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Profile", "Name" }
        );

        table = new JTable(tableModel);
        table.setShowGrid(false);
        table.setEnabled(false);
        table.setRowHeight(60);
        table.getColumn("Profile").setMaxWidth(80);
        table.setFont(new Font("휴먼모음T", Font.BOLD, 12));
        table.getTableHeader().setReorderingAllowed(false);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블 클릭 시
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        String chatRoomName = (String) tableModel.getValueAt(row, 1);
                        openChatRoom(chatRoomName);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setFont(new Font("휴먼모음T", Font.PLAIN, 12));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JLabel NewLabel = new JLabel("");
        NewLabel.setFont(new Font("휴먼모음T", Font.BOLD, 12));
        NewLabel.setBackground(new Color(255, 255, 255));
        ImageIcon icon1 = new ImageIcon(getClass().getResource("/img/person.png"));

        NewLabel.setText("<html>"
                + "<table>"
                + "<tr>"
                + "<td><img src='" + icon1 + "'></td>"
                + "<td width='120'></td>"
                + "</tr>"
                + "</table>"
                + "</html>");

        NewLabel.setOpaque(true);
        NewLabel.setHorizontalAlignment(SwingConstants.LEFT);
        NewLabel.setPreferredSize(new Dimension(100, 50));
        NewLabel.setBorder(new EmptyBorder(0, 160, 0, 0));
        contentPane.add(NewLabel, BorderLayout.SOUTH);

        NewLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TransChatingPage();
            }
        });

        contentPane.add(NewLabel, BorderLayout.SOUTH);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(0,0,0,0));
        contentPane.add(panel, BorderLayout.NORTH);

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 30000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println();

            // 서버로부터 메시지를 받는 스레드 시작
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("CREATE_CHAT:")) {
                    String[] parts = message.split(":", 2);
                    final String participants = parts[1];
                    List<String> participantList = Arrays.asList(participants.split(","));

                    // 클라이언트 이름이 포함된 경우에만 채팅방 생성
                    if (participantList.contains(FriendListFrame.getUserName())) {
                        SwingUtilities.invokeLater(() -> addNewChatRoom(participantList));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openChatRoom(String chatRoomName) {
        List<String> participants = Arrays.asList(chatRoomName.split(", "));
        ChattingRoomFrame chatRoom = new ChattingRoomFrame(chatRoomName, participants, socket, out, in);
        chatRoom.setParentLocation(getLocation()); // ChattingList 창의 위치를 전달
        chatRoom.setVisible(true);
    }

    public void addNewChatRoom(List<String> participants) {
        String chatRoomName = String.join(", ", participants);
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (chatRoomName.equals(tableModel.getValueAt(i, 1))) {
                    return; // 이미 채팅방이 존재하면 중복 추가 방지
                }
            }
            tableModel.addRow(new Object[]{null, chatRoomName});
            table.repaint(); // UI를 강제로 새로고침
        });
    }


    private void TransChatingPage() {
        FriendListFrame friendList = new FriendListFrame();
        friendList.setLocation(getLocation());  // 현재 위치 전달
        friendList.setVisible(true);
        setVisible(false);
    }

    public void createNewChatRoom(List<String> participants) {
        String participantsStr = String.join(",", participants);
        out.println("CREATE_CHAT:" + participantsStr);
        // 서버 응답을 기다리지 않고 즉시 로컬에 채팅방 추가
        SwingUtilities.invokeLater(() -> addNewChatRoom(participants));
    }


}
