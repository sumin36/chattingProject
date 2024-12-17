import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.Image;
import java.awt.MediaTracker;

public class EmoticonFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    
    public EmoticonFrame(ChattingRoomFrame chattingRoom) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 400, 400);
        setTitle("이모티콘");
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JPanel scrollContent = new JPanel();
        scrollContent.setLayout(new GridLayout(0, 3, 10, 10));

        for (int i = 1; i <= 9; i++) {
            final int emoticonNumber = i;
            String imagePath = "/img/상상부기_" + i + ".png";
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                JLabel label = new JLabel(new ImageIcon(image));
                label.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        chattingRoom.sendEmoticon(emoticonNumber);
                    }
                });
                scrollContent.add(label);
            } else {
                System.out.println("이미지를 불러올 수 없습니다: " + imagePath);
            }
        }

        JScrollPane scrollPane = new JScrollPane(scrollContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(scrollPane);
    }
}
