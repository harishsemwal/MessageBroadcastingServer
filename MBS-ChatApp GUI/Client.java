import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    public Client() {
        try {
            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1", 7755); // Connect to the server
            System.out.println("Connection established.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true); // Auto flush enabled

            startReading(); // Start reading from the server
            startWriting(); // Start writing to the server

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startReading() {
        // thread - reading purpose
        Runnable r1 = () -> {
            System.out.println("Reader started..");
            try {
                String msg;
                while (true) {
                    msg = br.readLine();
                    if (msg == null || msg.equals("Bye")) {
                        System.out.println("Server terminated the chat..");
                        break;
                    }
                    System.out.println("Server: " + msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        // thread - data will be taken from user and sent to the server
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    String content = br1.readLine();
                    out.println(content);
                    if (content.equals("Bye")) {
                        System.out.println("Client terminated the chat..");
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
        System.out.println("This is Client...");
        new Client(); // Start the client
    }
}
