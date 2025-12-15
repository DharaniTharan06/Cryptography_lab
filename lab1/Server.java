import java.io.*;
import java.net.*; 
import java.util.Scanner; 

public class Server {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[SERVER] Listening on port " + port + "...");

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
            new Thread(() -> listenForMessages()).start();
            handleUserMenu();
        }

        private void listenForMessages() {
            try {
                String received;
                while ((received = input.readLine()) != null) {
                    String[] parts = received.split(":", 3);
                    if (parts.length == 3) {
                        String algo = parts[0];
                        String key = parts[1];
                        String ciphertext = parts[2];

                        System.out.println("\n\n>>> MSG RECEIVED FROM " + socket.getInetAddress());
                        System.out.println("   Ciphertext: " + ciphertext);
                        System.out.println("   Key: " + key + " (Auto-detected)");
                        String plaintext = decrypt(algo, key, ciphertext);
                        System.out.println("   [DECRYPTED]: " + plaintext);
                        System.out.print("\nSelect Option: ");
                    }
                }
            } catch (IOException e) {
                System.out.println("[SERVER] Client disconnected: " + socket.getInetAddress());
            }
        }

        private void handleUserMenu() {
            try {
                while (true) {
                    System.out.println("\n=== SERVER SIDE ===");
                    System.out.println("1. Encrypt & Send");
                    System.out.println("2. Exit");
                    System.out.print("Select: ");
                    String choice = scanner.nextLine();
                    if (choice.equals("2")) break;
                    if (choice.equals("1")) performEncryptionAndSend();
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void performEncryptionAndSend() {
            try {
                System.out.println("1. Caesar | 2. Mono | 3. Playfair | 4. Hill | 5. Vigenere | 6. OTP | 7. Rail");
                int algo = Integer.parseInt(scanner.nextLine());
                System.out.print("Plaintext: ");
                String text = scanner.nextLine();

                String key = "", cipher = "", code = "";

                if (algo == 1) {
                    System.out.print("Shift: "); key = scanner.nextLine();
                    cipher = CipherUtils.caesar(text, Integer.parseInt(key), 1); code = "CAESAR";
                } else if (algo == 2) {
                    System.out.print("Key (26 chars): "); key = scanner.nextLine();
                    cipher = CipherUtils.monoalphabetic(text, key, 1); code = "MONO";
                } else if (algo == 3) {
                    System.out.print("Keyword: "); key = scanner.nextLine();
                    cipher = CipherUtils.playfair(text, key, 1); code = "PLAY";
                } else if (algo == 4) {
                    System.out.print("Key (4 chars): "); key = scanner.nextLine();
                    cipher = CipherUtils.hill(text, key, 1); code = "HILL";
                } else if (algo == 5) {
                    System.out.print("Keyword: "); key = scanner.nextLine();
                    cipher = CipherUtils.vigenere(text, key, 1); code = "VIG";
                } else if (algo == 6) {
                    System.out.print("Key (Length >= Text): "); key = scanner.nextLine();
                    cipher = CipherUtils.vigenere(text, key, 1); code = "OTP";
                } else if (algo == 7) {
                    System.out.print("Rails: "); key = scanner.nextLine();
                    cipher = CipherUtils.railFence(text, Integer.parseInt(key), 1); code = "RAIL";
                }

                output.println(code + ":" + key + ":" + cipher);
                System.out.println("Sent: " + cipher);
            } catch (Exception e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
        }

        private String decrypt(String algo, String key, String text) {
            try {
                switch (algo) {
                    case "CAESAR": return CipherUtils.caesar(text, Integer.parseInt(key), 0);
                    case "MONO":   return CipherUtils.monoalphabetic(text, key, 0);
                    case "PLAY":   return CipherUtils.playfair(text, key, 0);
                    case "HILL":   return CipherUtils.hill(text, key, 0);
                    case "VIG":    return CipherUtils.vigenere(text, key, 0);
                    case "OTP":    return CipherUtils.vigenere(text, key, 0);
                    case "RAIL":   return CipherUtils.railFence(text, Integer.parseInt(key), 0);
                    default: return "UNKNOWN ALGO";
                }
            } catch (Exception e) { return "Error decrypting: " + e.getMessage(); }
        }
    }
}
