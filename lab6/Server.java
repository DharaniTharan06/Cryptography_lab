import java.io.*;
import java.net.*;

public class Server{
    public static void main(String[] args) throws Exception {

        int p = 11;
        int q = 13;
        int n = p * q;
        int phi = (p - 1) * (q - 1);
        int e = 7;

        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("n = " + n);
        System.out.println("phi(n) = " + phi);

        if (HomoRsa.gcd(e, phi) != 1) {
            System.out.println("Invalid e");
            return;
        }

        int d = HomoRsa.modInverse(e, phi);

        System.out.println("Public Key (e, n) = (" + e + ", " + n + ")");
        System.out.println("Private Key (d, n) = (" + d + ", " + n + ")");

        int m1 = 4;
        int m2 = 5;

        long c1 = HomoRsa.encrypt(m1, e, n);
        long c2 = HomoRsa.encrypt(m2, e, n);

        long cMul = (c1 * c2) % n;
        long decryptedMul = HomoRsa.decrypt(cMul, d, n);

        long expected = (m1 * m2) % n;

        if (decryptedMul == expected)
            System.out.println("Homomorphic Property VERIFIED");
        else
            System.out.println("Homomorphic Property NOT VERIFIED");

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server waiting on port 5000...");

        Socket socket = serverSocket.accept();
        System.out.println("Client connected.");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
        );

        long receivedCipher = Long.parseLong(in.readLine());

        System.out.println("Encrypted message received: " + receivedCipher);

        long decryptedMessage = HomoRsa.decrypt(receivedCipher, d, n);

        System.out.println("Final Decrypted Message at Server: " + decryptedMessage);

        socket.close();
        serverSocket.close();
    }
}