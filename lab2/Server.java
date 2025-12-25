import java.io.*;
import java.net.*; 
import java.util.Scanner; 

public class Server {
    private static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Listening for DES Clients on port " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVER] Client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter output;
        private BufferedReader input;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                this.output = new PrintWriter(socket.getOutputStream(), true);
                this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            new Thread(this::listenForMessages).start();
            handleServerInput();
        }

        private void listenForMessages() {
            try {
                String received;
                while ((received = input.readLine()) != null) {
                    String[] parts = received.split(":", 3);
                    if (parts.length == 3 && parts[0].equals("DES")) {
                        String key = parts[1];
                        String ciphertextHex = parts[2];

                        System.out.println("\n\n>>> MSG RECEIVED FROM CLIENT");
                        System.out.println("   Ciphertext (Hex): " + ciphertextHex);
                        
                        String plaintext = CipherUtils.decrypt(ciphertextHex, key);
                        System.out.println("   [DECRYPTED]: " + plaintext);
                        System.out.print("\nServer Menu (1 to Send, 2 Exit): ");
                    }
                }
            } catch (IOException e) {
                System.out.println("[SERVER] Client disconnected.");
            }
        }

        private void handleServerInput() {
            try {
                while (true) {
                    if (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.equals("1")) {
                            System.out.print("Enter Reply Plaintext: ");
                            String text = scanner.nextLine();
                            System.out.print("Enter Key (8 chars): ");
                            String key = scanner.nextLine();
                            
                            if (key.length() > 8) key = key.substring(0, 8);
                            while (key.length() < 8) key += " ";

                            String cipher = CipherUtils.encrypt(text, key);
                            output.println("DES:" + key + ":" + cipher);
                            System.out.println("Reply Sent.");
                            System.out.print("\nServer Menu (1 to Send, 2 Exit): ");
                        } else if (line.equals("2")) {
                            break;
                        }
                    }
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
