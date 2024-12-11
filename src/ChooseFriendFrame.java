import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.LineBorder;

public class ChooseFriendFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel friendListPanel;
    private List<JCheckBox> friendCheckBoxes;
    private FriendListFrame friendListFrame;
    private ChattingList chatingList;

    public ChooseFriendFrame(FriendListFrame friendListFrame, ChattingList chatingList) {
        this.friendListFrame = friendListFrame;
        this.chatingList = chatingList;
        initComponents();
        updateFriendList();
    }

    private void initComponents() {
        setTitle("친구 선택");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(500, 100, 300, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        friendListPanel = new JPanel();
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

    public void updateFriendList() {
        SwingUtilities.invokeLater(() -> {
            friendListPanel.removeAll();
            friendCheckBoxes.clear();

            DefaultTableModel model = (DefaultTableModel) friendListFrame.getTable().getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String friendName = (String) model.getValueAt(i, 1);
                JCheckBox checkBox = new JCheckBox(friendName);
                checkBox.setFont(new Font("휴먼모음T", Font.PLAIN, 14));
                friendCheckBoxes.add(checkBox);
                friendListPanel.add(checkBox);
            }

            friendListPanel.revalidate();
            friendListPanel.repaint();
        });
    }

    private void createChatRoom() {
        List<String> selectedFriends = getSelectedFriends();
        String userName = FriendListFrame.getUserName();
        if (!selectedFriends.isEmpty()) {
            selectedFriends.add(userName);
            chatingList.createNewChatRoom(selectedFriends);
            JOptionPane.showMessageDialog(this, "채팅방이 생성되었습니다: " + String.join(", ", selectedFriends));
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "친구를 선택해주세요.");
        }
    }


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