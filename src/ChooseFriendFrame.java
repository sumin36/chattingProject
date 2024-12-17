import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;

// 친구 선택 프레임
public class ChooseFriendFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel friendListPanel;
    private List<JCheckBox> friendCheckBoxes;
    private FriendListFrame friendListFrame;
    private ChattingList chattingList;

    public ChooseFriendFrame(FriendListFrame friendListFrame, ChattingList chatingList) {
        this.friendListFrame = friendListFrame;
        this.chattingList = chatingList;
        initComponents();
        updateFriendList();
    }

    private void initComponents() {
        // UI 설정
        setTitle("친구 선택");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(500, 100, 300, 400);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(255, 255, 255));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        friendListPanel = new JPanel();
        friendListPanel.setBackground(new Color(255, 255, 255));
        friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(friendListPanel);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        friendCheckBoxes = new ArrayList<>();

        JButton selectButton = new JButton("채팅방 만들기");
        selectButton.setActionCommand("");
        selectButton.setBorder(new LineBorder(new Color(255, 204, 102), 1, true));
        selectButton.setBackground(new Color(255, 204, 102));
        selectButton.setFont(new Font("휴먼모음T", Font.BOLD, 14));
        selectButton.addActionListener(e -> createChatRoom());
        contentPane.add(selectButton, BorderLayout.SOUTH);
    }

    // 친구리스트 업데이트
    public void updateFriendList() {
        SwingUtilities.invokeLater(() -> {
            friendListPanel.removeAll();
            friendCheckBoxes.clear();

            DefaultTableModel model = (DefaultTableModel) friendListFrame.getTable().getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String friendName = (String) model.getValueAt(i, 1);
                JCheckBox checkBox = new JCheckBox(friendName);
                checkBox.setBackground(new Color(255, 255, 255));
                checkBox.setFont(new Font("휴먼모음T", Font.PLAIN, 14));
                friendCheckBoxes.add(checkBox);
                friendListPanel.add(checkBox);
            }

            friendListPanel.revalidate();
            friendListPanel.repaint();
        });
    }

    // 채팅방 생성
    private void createChatRoom() {
        List<String> selectedFriends = getSelectedFriends();
        String userName = FriendListFrame.getUserName();
        if (!selectedFriends.isEmpty()) {
            selectedFriends.add(userName);
            chattingList.createNewChatRoom(selectedFriends);
            JOptionPane.showMessageDialog(this, "채팅방이 생성되었습니다: " + String.join(", ", selectedFriends));
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "친구를 선택해주세요.");
        }
    }

    // 선택한 친구 불러오기
    private List<String> getSelectedFriends() {
        List<String> selectedFriends = new ArrayList<>();
        for (JCheckBox checkBox : friendCheckBoxes) {
            if (checkBox.isSelected()) {
                selectedFriends.add(checkBox.getText());
            }
        }
        return selectedFriends;
    }
}