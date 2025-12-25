import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            System.out.println("[CLIENT] Connected to Server!");
            new Thread(() -> listenForMessages(socket)).start();
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
                if (parts.length == 3 && parts[0].equals("DES")) {
                    System.out.println("\n\n>>> ENCRYPTED MSG RECEIVED!");
                    System.out.println("   Ciphertext (Hex): " + parts[2]);
                    System.out.println("   Key Used: " + parts[1]);
                    
                    String plaintext = CipherUtils.decrypt(parts[2], parts[1]);
                    System.out.println("   [DECRYPTED]: " + plaintext);
                    System.out.print("\nPress Enter to continue...");
                }
            }
        } catch (IOException e) {
            System.out.println("\n[CLIENT] Connection closed.");
            System.exit(0);
        }
    }

    private static void handleUserMenu(Socket socket) {
        try (PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {
            while (true) {
                System.out.println("\n=== DES CLIENT MENU ===");
                System.out.println("1. Send Encrypted Message");
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
        System.out.print("Enter Plaintext: ");
        String text = scanner.nextLine();

        System.out.print("Enter 8-char Key (64-bit): ");
        String key = scanner.nextLine();
        if (key.length() > 8) key = key.substring(0, 8);
        while (key.length() < 8) key += " ";

        try {
            String ciphertextHex = CipherUtils.encrypt(text, key);
            output.println("DES:" + key + ":" + ciphertextHex);
            System.out.println("Sent Ciphertext: " + ciphertextHex);
        } catch (Exception e) {
            System.out.println("[CLIENT] Error: " + e.getMessage());
        }
    }
}