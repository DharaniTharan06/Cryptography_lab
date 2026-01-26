import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter a message (max 16 chars): ");
            String input = sc.nextLine();

            byte[] pt = new byte[16];
            byte[] inputBytes = input.getBytes();
            
            System.arraycopy(inputBytes, 0, pt, 0, Math.min(inputBytes.length, 16));

            System.out.print("Sending Plaintext (Hex): ");
            for (byte b : pt) System.out.printf("%02x ", b);
            System.out.println();

            byte[] key = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
            byte[][][] roundKeys = new byte[11][4][4];
            AESCore.keyExpansion(key, roundKeys);

            byte[][] state = new byte[4][4];
            for (int i = 0; i < 16; i++) state[i % 4][i / 4] = pt[i];

            AESCore.encrypt(state, roundKeys);

            byte[] out = new byte[16];
            for (int i = 0; i < 16; i++) out[i] = state[i % 4][i / 4];

            try (Socket socket = new Socket("localhost", 12345);
                 OutputStream os = socket.getOutputStream()) {
                os.write(out);
                System.out.println("Encrypted Data Sent to Server.");
                System.out.print("Ciphertext sent: ");
                for (byte b : out) System.out.printf("%02x ", b);
                System.out.println();
            }

        } catch (IOException e) {
            System.out.println("Connection Error: Is the server running? " + e.getMessage());
        }
    }
}
