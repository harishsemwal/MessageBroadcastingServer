import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

class Server {
    // Constructor
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    public Server() {
        try {
            server = new ServerSocket(7755);
            System.out.println("Server is ready to accept connection...");
            socket = server.accept(); // Accept client connection

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Auto flush enabled

            startReading(); // Start reading from the client
            startWriting(); // Start writing to the client

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to read messages from the client
    public void startReading() {
        // thread - reading purpose
        Runnable r1 = () -> {
            System.out.println("Reader Started..");
            try {
                String msg;
                while (true) {
                    msg = br.readLine();
                    if (msg == null || msg.equals("Bye")) {
                        System.out.println("Client terminated the chat..");
                        break;
                    }
                    System.out.println("Client: " + msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    socket.close();
                    server.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r1).start();
    }

    // Method to write messages to the client
    public void startWriting() {
        // thread - data will be taken from user and sent to the client
        Runnable r2 = () -> {
            System.out.println("Writer Started...");
            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String content = br1.readLine();
                    out.println(content);
                    if (content.equals("Bye")) {
                        System.out.println("Server terminated the chat..");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Server");
        new Server(); // Start the server
    }
}
