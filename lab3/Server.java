import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(12345);
        System.out.println("SERVER STARTED. Listening on port 12345...");

        byte[] key = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        byte[][][] roundKeys = new byte[11][4][4];
        AESCore.keyExpansion(key, roundKeys);

        while (true) {
            try (Socket client = server.accept()) {
                System.out.println("\n[Server] Client connected.");

                byte[] buffer = new byte[16];
                int bytesRead = client.getInputStream().read(buffer);

                if (bytesRead != -1) {
                    System.out.print("[Server] Received Ciphertext: ");
                    for (byte b : buffer) System.out.printf("%02x ", b);
                    System.out.println();

                    byte[][] state = new byte[4][4];
                    for (int i = 0; i < 16; i++) state[i % 4][i / 4] = buffer[i];

                    AESCore.decrypt(state, roundKeys);

                    System.out.print("[Server] Decrypted Plaintext (Hex): ");
                    for (int i = 0; i < 16; i++) System.out.printf("%02x ", state[i % 4][i / 4]);
                    System.out.println();

                    byte[] decryptedBytes = new byte[16];
                    for (int i = 0; i < 16; i++) decryptedBytes[i] = state[i % 4][i / 4];
                    String msg = new String(decryptedBytes).trim();
                    System.out.println("[Server] Decrypted Message (String): " + msg);
                }
            } catch (Exception e) {
                System.out.println("Error handling client: " + e.getMessage());
            }
        }
    }
}
