import java.awt.EventQueue;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

public class ChatServer extends JFrame {
    private JPanel contentPane;
    private JTextArea textArea;
    private ServerSocket serverSocket;
    private Set<ClientHandler> clients = new HashSet<>();
    private List<String> chatRooms = new ArrayList<>();
    private Map<String, String> chatRoomPinnedMessages = new HashMap<>(); // 각 채팅방의 공지 메시지 관리

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ChatServer frame = new ChatServer();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ChatServer() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(600, 100, 338, 386);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(12, 10, 300, 244);
        contentPane.add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        JButton btnServerStart = new JButton("Server Start");
        btnServerStart.addActionListener(e -> startServer());
        btnServerStart.setBounds(12, 300, 300, 35);
        contentPane.add(btnServerStart);
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(30000);
            appendText("채팅 서버 실행 중...");
            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(clientSocket);
                        clients.add(clientHandler);
                        clientHandler.start();
                    } catch (IOException e) {
                        appendText("accept 중 오류 발생");
                        break;
                    }
                }
            }).start();
        } catch (IOException e) {
            appendText("서버 소켓 생성 오류");
        }
    }

    private void appendText(String str) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(str + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    private class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                name = in.readLine();
                if (name == null) return;

                // 새 사용자에게 기존 사용자 목록 전송
                for (ClientHandler client : clients) {
                    if (client != this && client.name != null) {
                        out.println("USER:" + client.name);
                    }
                }

                for (String chatRoom : chatRooms) {
                    out.println("CREATE_CHAT:" + chatRoom);
                    if (chatRoomPinnedMessages.containsKey(chatRoom)) {
                        out.println("PINNED_MESSAGE:" + chatRoomPinnedMessages.get(chatRoom));
                    }
                }

                // 모든 클라이언트에게 새 사용자 알림
                broadcast("USER:" + name);
                appendText(name + "님이 접속했습니다.");

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("CREATE_CHAT:")) {
                        broadcast(message);
                    } else if (message.startsWith("CHAT:")) {
                        // 채팅 메시지 처리 (직접 브로드캐스트하지 않음)
                        handleChatMessage(message);
                    } else if (message.startsWith("UPDATE_PINNED_MESSAGE:")) {
                        updatePinnedMessage(message); // 공지 메시지 갱신 처리
                    } else {
                        broadcast(name + ": " + message);
                    }
                }
            } catch (IOException e) {
                appendText(e.toString());
            } finally {
                if (name != null) {
                    clients.remove(this);
                    broadcast("LEAVE:" + name);
                    appendText(name + "님이 나갔습니다.");
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }

        private void handleChatMessage(String message) {
            String[] parts = message.split(":", 4);
            if (parts.length == 4) {
                String chatRoomName = parts[1];
                String[] participants = chatRoomName.split(", ");
                Set<String> participantSet = new HashSet<>(Arrays.asList(participants));

                for (ClientHandler client : clients) {
                    if (participantSet.contains(client.name)) {
                        client.out.println(message);
                    }
                }
            }
        }

        private void broadcast(String message) {
            if (message.startsWith("CREATE_CHAT:")) {
                String[] parts = message.split(":", 2);
                String participantsStr = parts[1];

                // 채팅방 목록에 추가 (중복 방지)
                if (!chatRooms.contains(participantsStr)) {
                    chatRooms.add(participantsStr);
                }

                // 모든 클라이언트에게 브로드캐스트
                for (ClientHandler client : clients) {
                    client.out.println(message);
                }
            }
            else if (message.startsWith("CHAT:")) {
                String[] parts = message.split(":", 4);
                String chatRoomName = parts[1];
                String[] participants = chatRoomName.split(", ");
                Set<String> participantSet = new HashSet<>(Arrays.asList(participants));

                for (ClientHandler client : clients) {
                    if (participantSet.contains(client.name)) {
                        client.out.println(message);
                    }
                }
            } else {
                for (ClientHandler client : clients) {
                    client.out.println(message);
                }
            }
        }

        private void updatePinnedMessage(String message) {
            String[] parts = message.split(":", 2);
            String[] chatRoomAndMessage = parts[1].split(",", 2);
            String chatRoomName = chatRoomAndMessage[1].trim();
            String newMessage = chatRoomAndMessage[0].trim();

            chatRoomPinnedMessages.put(chatRoomName, newMessage);
            System.out.println("공지 메시지가 갱신되었습니다: " + newMessage);

            // 해당 채팅방에 있는 모든 클라이언트에게 공지 메시지 전송
            for (ClientHandler client : clients) {
                if (client.name != null) {
                    String[] participants = chatRoomName.split(", ");
                    if (Arrays.asList(participants).contains(client.name)) {
                        client.out.println("PINNED_MESSAGE:" + chatRoomName + ":" + newMessage);
                    }
                }
            }
        }
    }
}