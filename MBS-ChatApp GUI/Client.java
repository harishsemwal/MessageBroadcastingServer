import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageInput;
    private Font font = new Font("Roboto", Font.PLAIN, 16);

    // Constructor
    public Client() {
        try {
            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1", 7755); // Connect to the server
            System.out.println("Connection established.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Auto flush enabled

            createGUI(); // Setup GUI
            handleEvents(); // Setup event listeners

            startReading(); // Start reading from the server
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        frame = new JFrame("Client Chat");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        messageArea = new JTextArea();
        messageArea.setFont(font);
        messageArea.setEditable(false);

        messageInput = new JTextField();
        messageInput.setFont(font);

        JScrollPane scrollPane = new JScrollPane(messageArea);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(messageInput, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void handleEvents() {
        messageInput.addActionListener(e -> {
            String content = messageInput.getText();
            messageArea.append("Me: " + content + "\n");
            out.println(content);
            messageInput.setText("");

            if (content.equalsIgnoreCase("Bye")) {
                System.out.println("Client terminated the chat..");
                try {
                    socket.close();
                    br.close();
                    out.close();
                    frame.dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    // Method to read messages from the server
    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started..");
            try {
                String msg;
                while (true) {
                    msg = br.readLine();
                    if (msg == null || msg.equalsIgnoreCase("Bye")) {
                        System.out.println("Server terminated the chat..");
                        JOptionPane.showMessageDialog(frame, "Server disconnected!");
                        messageInput.setEnabled(false);
                        break;
                    }
                    messageArea.append("Server: " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Client...");
        new Client();
    }
}
