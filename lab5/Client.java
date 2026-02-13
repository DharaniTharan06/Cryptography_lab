import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter role (alice/bob/attacker): ");
        String role = sc.nextLine();

        Socket socket = new Socket("localhost", 5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] params = in.readLine().split(",");
        long p = Long.parseLong(params[0]);
        long g = Long.parseLong(params[1]);

        if(role.equalsIgnoreCase("attacker")) {
            long privA = HellmanUtils.generatePrivateKey();
            long privB = HellmanUtils.generatePrivateKey();

            long pubA = HellmanUtils.generatePublicKey(g, privA, p);
            long pubB = HellmanUtils.generatePublicKey(g, privB, p);

            out.println(pubA);
            out.println(pubB);

            while(true) {
                String intercepted = in.readLine();
                System.out.println("Intercepted: " + intercepted);

                String modified = "[HACKED] " + intercepted;
                out.println(modified);
            }
        }

        else {

            long privateKey = HellmanUtils.generatePrivateKey();
            long publicKey = HellmanUtils.generatePublicKey(g, privateKey, p);

            out.println(publicKey);

            long receivedPub = Long.parseLong(in.readLine());

            long sharedKey = HellmanUtils.computeSharedSecret(receivedPub, privateKey, p);
            System.out.println("Shared key: " + sharedKey);
            try {
                while(true) {
                    System.out.print("Enter message: ");
                    String msg = sc.nextLine();

                    String encrypted = HellmanUtils.encrypt(msg, sharedKey);
                    out.println(encrypted);

                    String reply = in.readLine();
                    String decrypted = HellmanUtils.decrypt(reply, sharedKey);

                    System.out.println("Received: " + decrypted);
                }
            } finally {
                socket.close();
                sc.close();
            }
        }
    }
}

