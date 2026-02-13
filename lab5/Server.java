import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {

    static final int PORT = 5000;

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== DIFFIE-HELLMAN KEY EXCHANGE SERVER ===");
        System.out.print("Enter prime number p: ");
        long p = sc.nextLong();

        System.out.print("Enter primitive root g: ");
        long g = sc.nextLong();

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("\n[SERVER] Running on port " + PORT);
        System.out.println("[SERVER] Waiting for clients to connect...\n");

        Socket alice = serverSocket.accept();
        System.out.println("[SERVER] Alice connected from " + alice.getInetAddress());

        Socket bob = serverSocket.accept();
        System.out.println("[SERVER] Bob connected from " + bob.getInetAddress());

        Socket attacker = serverSocket.accept();
        System.out.println("[SERVER] Attacker connected from " + attacker.getInetAddress());

        BufferedReader inAlice = new BufferedReader(new InputStreamReader(alice.getInputStream()));
        PrintWriter outAlice = new PrintWriter(alice.getOutputStream(), true);

        BufferedReader inBob = new BufferedReader(new InputStreamReader(bob.getInputStream()));
        PrintWriter outBob = new PrintWriter(bob.getOutputStream(), true);

        BufferedReader inAtt = new BufferedReader(new InputStreamReader(attacker.getInputStream()));
        PrintWriter outAtt = new PrintWriter(attacker.getOutputStream(), true);

        System.out.println("\n[SERVER] Broadcasting p=" + p + ", g=" + g + " to all clients");
        outAlice.println(p + "," + g);
        outBob.println(p + "," + g);
        outAtt.println(p + "," + g);

        long alicePub = Long.parseLong(inAlice.readLine());
        System.out.println("\n[SERVER] Received Alice's public key: " + alicePub);
        
        long bobPub = Long.parseLong(inBob.readLine());
        System.out.println("[SERVER] Received Bob's public key: " + bobPub);

        long fakeForAlice = Long.parseLong(inAtt.readLine());
        long fakeForBob = Long.parseLong(inAtt.readLine());
        
        System.out.println("\n[SERVER] *** MITM ATTACK DETECTED ***");
        System.out.println("[SERVER] Attacker's fake key for Alice: " + fakeForAlice);
        System.out.println("[SERVER] Attacker's fake key for Bob: " + fakeForBob);

        System.out.println("\n[SERVER] Sending real keys to attacker...");
        outAtt.println(alicePub);
        outAtt.println(bobPub);
        Thread.sleep(100);

        System.out.println("[SERVER] Sending FAKE keys to victims...");
        outAlice.println(fakeForAlice);
        outBob.println(fakeForBob);      

        Thread aliceToBob = new Thread(() -> {
            try {
                while (true) {
                    String msgFromAlice = inAlice.readLine();
                    if (msgFromAlice == null) break;

                    System.out.println("[SERVER] Relaying encrypted message: Alice -> Attacker");
                    outAtt.println("ALICE:" + msgFromAlice);
                    
                    String modified = inAtt.readLine();
                    if (modified == null) break;
                    
                    System.out.println("[SERVER] Relaying modified message: Attacker -> Bob");
                    outBob.println(modified);
                }
            } catch (Exception e) {
                System.out.println("[SERVER] Alice channel closed.");
            }
        });

        Thread bobToAlice = new Thread(() -> {
            try {
                while (true) {
                    String msgFromBob = inBob.readLine();
                    if (msgFromBob == null) break;

                    System.out.println("[SERVER] Relaying encrypted message: Bob -> Attacker");
                    outAtt.println("BOB:" + msgFromBob);
                    
                    String modifiedReply = inAtt.readLine();
                    if (modifiedReply == null) break;
                    
                    System.out.println("[SERVER] Relaying modified message: Attacker -> Alice");
                    outAlice.println(modifiedReply);
                }
            } catch (Exception e) {
                System.out.println("[SERVER] Bob channel closed.");
            }
        });

        aliceToBob.start();
        bobToAlice.start();

        aliceToBob.join();
        bobToAlice.join();

        alice.close();
        bob.close();
        attacker.close();
        serverSocket.close();
        sc.close();
    }
}