import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SpinnerNumberModel;

public class ReservationMessageFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	private JLabel lblNewLabel_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReservationMessageFrame frame = new ReservationMessageFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ReservationMessageFrame() {
		setTitle("메시지 예약");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 550);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("메시지 예약");
		lblNewLabel.setFont(new Font("휴먼모음T", Font.BOLD, 17));
		lblNewLabel.setBounds(12, 10, 90, 31);
		contentPane.add(lblNewLabel);

		textArea = new JTextArea();
		textArea.setToolTipText("");
		textArea.setBorder(new LineBorder(new Color(255, 204, 102), 2, true));
		textArea.setForeground(new Color(0, 0, 0));
		textArea.setLineWrap(true); // 자동 줄 바꿈 활성화
		textArea.setWrapStyleWord(true); // 단어 단위로 줄 바꿈
		textArea.setFont(new Font("휴먼모음T", Font.PLAIN, 14));
		textArea.setBounds(12, 51, 362, 268);
		contentPane.add(textArea);
		textArea.setColumns(10);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(new LineBorder(new Color(255, 204, 102), 2, true));
		scrollPane.setBounds(12, 51, 362, 268);

		contentPane.add(scrollPane);

		lblNewLabel_1 = new JLabel("윤소정");
		lblNewLabel_1.setFont(new Font("휴먼모음T", Font.PLAIN, 18));
		lblNewLabel_1.setBounds(12, 339, 362, 20);
		contentPane.add(lblNewLabel_1);

		JLabel lblNewLabel_1_1 = new JLabel("날짜");
		lblNewLabel_1_1.setFont(new Font("휴먼모음T", Font.PLAIN, 18));
		lblNewLabel_1_1.setBounds(12, 376, 35, 20);
		contentPane.add(lblNewLabel_1_1);

		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(24, 24, 29, 1));
		spinner.setBounds(72, 377, 44, 22);
		contentPane.add(spinner);

		JSpinner spinner_1 = new JSpinner();
		spinner_1.setModel(new SpinnerNumberModel(12, 1, 12, 1));
		spinner_1.setBounds(152, 377, 44, 22);
		contentPane.add(spinner_1);

		JSpinner spinner_2 = new JSpinner();
		spinner_2.setModel(new SpinnerNumberModel(1, 1, 31, 1));
		spinner_2.setBounds(232, 377, 44, 22);
		contentPane.add(spinner_2);

		JLabel lblNewLabel_1_1_1 = new JLabel("시간");
		lblNewLabel_1_1_1.setFont(new Font("휴먼모음T", Font.PLAIN, 18));
		lblNewLabel_1_1_1.setBounds(12, 415, 35, 20);
		contentPane.add(lblNewLabel_1_1_1);

		JSpinner spinner_4 = new JSpinner();
		spinner_4.setModel(new SpinnerNumberModel(12, 0, 23, 1));
		spinner_4.setBounds(72, 416, 44, 22);
		contentPane.add(spinner_4);

		JSpinner spinner_5 = new JSpinner();
		spinner_5.setModel(new SpinnerNumberModel(0, 0, 60, 5));
		spinner_5.setBounds(153, 416, 43, 22);
		contentPane.add(spinner_5);

		JButton btnNewButton = new JButton("예약");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		btnNewButton.setBackground(new Color(255, 204, 102));
		btnNewButton.setBorder(new LineBorder(new Color(255, 204, 102), 1, true));
		btnNewButton.setFont(new Font("휴먼모음T", Font.PLAIN, 16));
		btnNewButton.setBounds(316, 468, 58, 23);
		contentPane.add(btnNewButton);

		JLabel lblNewLabel_2 = new JLabel("년");
		lblNewLabel_2.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2.setBounds(123, 380, 12, 21);
		contentPane.add(lblNewLabel_2);

		JLabel lblNewLabel_2_1 = new JLabel("월");
		lblNewLabel_2_1.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_1.setBounds(203, 380, 12, 21);
		contentPane.add(lblNewLabel_2_1);

		JLabel lblNewLabel_2_2 = new JLabel("일");
		lblNewLabel_2_2.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_2.setBounds(284, 380, 12, 21);
		contentPane.add(lblNewLabel_2_2);

		JLabel lblNewLabel_2_3 = new JLabel("시");
		lblNewLabel_2_3.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_3.setBounds(123, 419, 12, 21);
		contentPane.add(lblNewLabel_2_3);

		JLabel lblNewLabel_2_4 = new JLabel("분");
		lblNewLabel_2_4.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_4.setBounds(203, 419, 12, 21);
		contentPane.add(lblNewLabel_2_4);
	}
}
