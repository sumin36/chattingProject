import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReservationMessageFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea textArea;
	private PrintWriter out; // 서버로 메시지를 전송하기 위한 PrintWriter
	private String chatRoomName; // 채팅방 이름
	private JSpinner yearSpinner;
	private JSpinner monthSpinner;
	private JSpinner daySpinner;
	private JSpinner hourSpinner;
	private JSpinner minuteSpinner;

	//예약 페이지
	public ReservationMessageFrame(Socket socket, PrintWriter out, String chatRoomName) {
		this.out = out; // PrintWriter 초기화
		this.chatRoomName = chatRoomName; // 채팅방 이름 초기화

		setTitle("메시지 예약");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 550);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("메시지 예약");
		lblNewLabel.setFont(new Font("휴먼모음T", Font.BOLD, 18));
		lblNewLabel.setBounds(15, 10, 90, 31);
		contentPane.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel(chatRoomName);
		lblNewLabel_1.setFont(new Font("휴먼모음T", Font.PLAIN, 17));
		lblNewLabel_1.setBounds(12, 339, 362, 20);
		contentPane.add(lblNewLabel_1);

		textArea = new JTextArea();
		textArea.setBorder(new LineBorder(new Color(255, 204, 102)));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("휴먼모음T", Font.PLAIN, 14));
		textArea.setBounds(12, 51, 362, 268);
		contentPane.add(textArea);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBorder(new LineBorder(new Color(255, 204, 102), 2));
		scrollPane.setBounds(12, 51, 362, 268);
		contentPane.add(scrollPane);

		// 날짜 및 시간 선택 스피너
		JLabel lblNewLabel_1_1 = new JLabel("날짜");
		lblNewLabel_1_1.setFont(new Font("휴먼모음T", Font.PLAIN, 16));
		lblNewLabel_1_1.setBounds(12, 378, 35, 20);
		contentPane.add(lblNewLabel_1_1);

		yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 2023, 2100, 1));
		yearSpinner.setBounds(60, 377, 49, 22);
		contentPane.add(yearSpinner);

		JLabel lblNewLabel_2 = new JLabel("년");
		lblNewLabel_2.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2.setBounds(116, 380, 12, 21);
		contentPane.add(lblNewLabel_2);

		monthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
		monthSpinner.setBounds(141, 377, 44, 22);
		contentPane.add(monthSpinner);

		JLabel lblNewLabel_2_1 = new JLabel("월");
		lblNewLabel_2_1.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_1.setBounds(191, 380, 12, 21);
		contentPane.add(lblNewLabel_2_1);

		daySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
		daySpinner.setBounds(220, 377, 44, 22);
		contentPane.add(daySpinner);

		JLabel lblNewLabel_2_2 = new JLabel("일");
		lblNewLabel_2_2.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_2.setBounds(272, 380, 12, 21);
		contentPane.add(lblNewLabel_2_2);

		JLabel lblNewLabel_1_1_1 = new JLabel("시간");
		lblNewLabel_1_1_1.setFont(new Font("휴먼모음T", Font.PLAIN, 16));
		lblNewLabel_1_1_1.setBounds(12, 417, 35, 20);
		contentPane.add(lblNewLabel_1_1_1);

		hourSpinner = new JSpinner(new SpinnerNumberModel(12, 0, 23, 1));
		hourSpinner.setBounds(60, 416, 49, 22);
		contentPane.add(hourSpinner);

		JLabel lblNewLabel_2_3 = new JLabel("시");
		lblNewLabel_2_3.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_3.setBounds(116, 419, 12, 21);
		contentPane.add(lblNewLabel_2_3);

		minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		minuteSpinner.setBounds(141 ,416 ,43 ,22);
		contentPane.add(minuteSpinner);

		JLabel lblNewLabel_2_4 = new JLabel("분");
		lblNewLabel_2_4.setFont(new Font("휴먼모음T", Font.PLAIN, 15));
		lblNewLabel_2_4.setBounds(191, 419, 12, 21);
		contentPane.add(lblNewLabel_2_4);

		JButton btnNewButton = new JButton("예약");
		btnNewButton.addActionListener(e -> scheduleMessage());

		btnNewButton.setBackground(new Color(255 ,204 ,102));
		btnNewButton.setBorder(new LineBorder(new Color(255 ,204 ,102),1,true));
		btnNewButton.setFont(new Font("휴먼모음T", Font.PLAIN ,16));
		btnNewButton.setBounds(314 ,468 ,58 ,23);
		contentPane.add(btnNewButton);
	}

	private void scheduleMessage() {
		// 메시지와 날짜/시간 가져오기
		String message = textArea.getText().trim();
		int year = (int) yearSpinner.getValue();
		int month = (int) monthSpinner.getValue() - 1;
		int day = (int) daySpinner.getValue();
		int hour = (int) hourSpinner.getValue();
		int minute = (int) minuteSpinner.getValue();

		if (message.isEmpty()) {
			JOptionPane.showMessageDialog(this,"메시지를 입력하세요.");
			return;
		}

		// 예약된 메시지 처리 로직
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day, hour, minute, 0);

		long delay = calendar.getTimeInMillis() - System.currentTimeMillis();

		if (delay < 0) {
			JOptionPane.showMessageDialog(this,"현재 시간 이후로 예약해주세요.");
			return;
		}

		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(() -> sendMessage(message), delay, TimeUnit.MILLISECONDS);

		JOptionPane.showMessageDialog(this,"메시지가 " + year + "-" + (month + 1) + "-" + day + " " + hour + ":" + minute + "에 예약되었습니다.");

		textArea.setText("");
	}

	private void sendMessage(String message) {
		System.out.println("Sending message: " + message);
		out.println("CHAT:" + chatRoomName + ":" + FriendListFrame.getUserName() + ":" + message);
	}
}
