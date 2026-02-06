import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== RSA SERVER ===");
        System.out.println("--- RSA Key Setup ---");

        System.out.print("Enter prime p: ");
        BigInteger p = sc.nextBigInteger();

        System.out.print("Enter prime q: ");
        BigInteger q = sc.nextBigInteger();

        BigInteger n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        System.out.print("Enter public exponent e (coprime with " + phi + "): ");
        BigInteger e = sc.nextBigInteger();

        BigInteger d = e.modInverse(phi);

        System.out.println("Generated Private Key d: " + d);

        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("\nServer waiting on port 1234...");

        Socket socket = serverSocket.accept();
        System.out.println("Client connected!");

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(e);
        out.writeObject(n);
        BigInteger[] ciphertext = (BigInteger[]) in.readObject();

        System.out.print("\nCiphertext received: ");
        StringBuilder decryptedMessage = new StringBuilder();

        for (BigInteger c : ciphertext) {
            System.out.print(c + " ");
            BigInteger m = RSACore.decrypt(c, d, n);
            decryptedMessage.append((char) m.intValue());
        }

        System.out.println("\nDecrypted Message: " + decryptedMessage.toString());

        socket.close();
        serverSocket.close();
        sc.close();
    }
}
