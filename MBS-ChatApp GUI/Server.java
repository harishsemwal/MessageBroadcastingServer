import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    private JFrame frame;
    private JTextArea messageArea;
    private JTextField messageInput;
    private Font font = new Font("Roboto", Font.PLAIN, 16);

    // Constructor
    public Server() {
        try {
            server = new ServerSocket(7755);
            System.out.println("Server is ready to accept connection...");
            socket = server.accept(); // Accept client connection

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Auto flush enabled

            createGUI(); // Setup GUI
            handleEvents(); // Setup event listeners

            startReading(); // Start reading from the client
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        frame = new JFrame("Server Chat");
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
                System.out.println("Server terminated the chat..");
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

    // Method to read messages from the client
    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader Started..");
            try {
                String msg;
                while (true) {
                    msg = br.readLine();
                    if (msg == null || msg.equalsIgnoreCase("Bye")) {
                        System.out.println("Client terminated the chat..");
                        JOptionPane.showMessageDialog(frame, "Client disconnected!");
                        messageInput.setEnabled(false);
                        break;
                    }
                    messageArea.append("Client: " + msg + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Server");
        new Server();
    }
}
