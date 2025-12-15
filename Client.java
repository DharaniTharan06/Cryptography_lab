import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            System.out.println("[CLIENT] Connected to Server!");

            // Start a thread to listen for incoming messages
            new Thread(() -> listenForMessages(socket)).start();

            // Handle the menu-driven user input
            handleUserMenu(socket);

        } catch (IOException e) {
            System.out.println("[CLIENT] Server not found.");
        }
    }

    private static void listenForMessages(Socket socket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String received;
            while ((received = input.readLine()) != null) {
                String[] parts = received.split(":", 3);
                if (parts.length == 3) {
                    System.out.println("\n\n>>> MSG RECEIVED!");
                    System.out.println("   Ciphertext: " + parts[2]);
                    System.out.println("   Key: " + parts[1]);
                    System.out.println("   [DECRYPTED]: " + decrypt(parts[0], parts[1], parts[2]));
                    System.out.print("\nSelect: ");
                }
            }
        } catch (IOException e) {
            System.out.println("[CLIENT] Connection closed.");
            System.exit(0);
        }
    }

    private static String decrypt(String algo, String key, String text) {
        try {
            switch (algo) {
                case "CAESAR": return CipherUtils.caesar(text, Integer.parseInt(key), 0);
                case "MONO":   return CipherUtils.monoalphabetic(text, key, 0);
                case "PLAY":   return CipherUtils.playfair(text, key, 0);
                case "HILL":   return CipherUtils.hill(text, key, 0);
                case "VIG":    return CipherUtils.vigenere(text, key, 0);
                case "OTP":    return CipherUtils.vigenere(text, key, 0);
                case "RAIL":   return CipherUtils.railFence(text, Integer.parseInt(key), 0);
                default: return "UNKNOWN";
            }
        } catch (Exception e) {
            return "Error decrypting";
        }
    }

    private static void handleUserMenu(Socket socket) {
        try (PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {
            while (true) {
                System.out.println("\n=== CLIENT MENU ===");
                System.out.println("1. Encrypt & Send");
                System.out.println("2. Exit");
                System.out.print("Select: ");
                String choice = scanner.nextLine();

                if (choice.equals("2")) System.exit(0);
                if (choice.equals("1")) performEncryptionAndSend(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void performEncryptionAndSend(PrintWriter output) {
        System.out.println("1. Caesar | 2. Mono | 3. Playfair | 4. Hill | 5. Vigenere | 6. OTP | 7. Rail");
        int algo = Integer.parseInt(scanner.nextLine());
        System.out.print("Plaintext: ");
        String text = scanner.nextLine();

        String key = "", cipher = "", code = "";

        try {
            switch (algo) {
                case 1 -> {
                    System.out.print("Shift: "); key = scanner.nextLine();
                    cipher = CipherUtils.caesar(text, Integer.parseInt(key), 1); code = "CAESAR";
                }
                case 2 -> {
                    System.out.print("Key (26 chars): "); key = scanner.nextLine();
                    cipher = CipherUtils.monoalphabetic(text, key, 1); code = "MONO";
                }
                case 3 -> {
                    System.out.print("Keyword: "); key = scanner.nextLine();
                    cipher = CipherUtils.playfair(text, key, 1); code = "PLAY";
                }
                case 4 -> {
                    System.out.print("Key (4 chars): "); key = scanner.nextLine();
                    cipher = CipherUtils.hill(text, key, 1); code = "HILL";
                }
                case 5 -> {
                    System.out.print("Keyword: "); key = scanner.nextLine();
                    cipher = CipherUtils.vigenere(text, key, 1); code = "VIG";
                }
                case 6 -> {
                    System.out.print("Key (Length >= Text): "); key = scanner.nextLine();
                    cipher = CipherUtils.vigenere(text, key, 1); code = "OTP";
                }
                case 7 -> {
                    System.out.print("Rails: "); key = scanner.nextLine();
                    cipher = CipherUtils.railFence(text, Integer.parseInt(key), 1); code = "RAIL";
                }
            }

            output.println(code + ":" + key + ":" + cipher);
            System.out.println("Sent: " + cipher);
        } catch (Exception e) {
            System.out.println("[CLIENT] Error sending message: " + e.getMessage());
        }
    }
}
