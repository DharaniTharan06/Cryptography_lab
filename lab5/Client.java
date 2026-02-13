import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.println("=== DIFFIE-HELLMAN CLIENT ===");
        System.out.print("Enter role (alice/bob/attacker): ");
        String role = sc.nextLine();

        Socket socket = new Socket("localhost", 5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        String[] params = in.readLine().split(",");
        long p = Long.parseLong(params[0]);
        long g = Long.parseLong(params[1]);

        System.out.println("\n[RECEIVED] p = " + p + ", g = " + g);

        if (role.equalsIgnoreCase("attacker")) {

            System.out.println("\n=== ATTACKER MODE: MAN-IN-THE-MIDDLE ATTACK ===\n");

            long privA = HellmanUtils.generatePrivateKey();
            long privB = HellmanUtils.generatePrivateKey();

            long pubA = HellmanUtils.generatePublicKey(g, privA, p);
            long pubB = HellmanUtils.generatePublicKey(g, privB, p);

            System.out.println("[ATTACKER] Generated key pair for Alice:");
            System.out.println("  Private key: " + privA);
            System.out.println("  Public key: " + pubA);
            
            System.out.println("\n[ATTACKER] Generated key pair for Bob:");
            System.out.println("  Private key: " + privB);
            System.out.println("  Public key: " + pubB);

            out.println(pubA);  
            out.println(pubB);

            long alicePub = Long.parseLong(in.readLine());
            long bobPub = Long.parseLong(in.readLine());

            System.out.println("\n[ATTACKER] Intercepted real public keys:");
            System.out.println("  Alice's public key: " + alicePub);
            System.out.println("  Bob's public key: " + bobPub);

            long sharedAlice = HellmanUtils.computeSharedSecret(alicePub, privA, p);
            long sharedBob = HellmanUtils.computeSharedSecret(bobPub, privB, p);

            System.out.println("\n[ATTACKER] *** MITM SUCCESSFUL ***");
            System.out.println("  Shared key with Alice: " + sharedAlice);
            System.out.println("  Shared key with Bob: " + sharedBob);
            
            long maliciousKey = (sharedAlice + sharedBob + 7) % 26;
            if (maliciousKey == 0) maliciousKey = 13;
            
            System.out.println("  Malicious re-encryption key: " + maliciousKey);
            System.out.println("\n[ATTACKER] Waiting to intercept messages...\n");

            try {
                while (true) {
                    String taggedMessage = in.readLine();
                    if (taggedMessage == null) break;

                    if (taggedMessage.startsWith("ALICE:")) {
                        String encrypted = taggedMessage.substring(6);
                        
                        String decrypted = HellmanUtils.decrypt(encrypted, sharedAlice);
                        
                        System.out.println("\n╔════════════════════════════════════════╗");
                        System.out.println("║   INTERCEPTED: Alice -> Bob            ║");
                        System.out.println("╚════════════════════════════════════════╝");
                        System.out.println("Encrypted message: " + encrypted);
                        System.out.println("Decrypted message: " + decrypted);
                        System.out.println("Modified message: [HACKED] " + decrypted);

                        String modified =  decrypted;
                        String reEncrypted = HellmanUtils.encrypt(modified, maliciousKey);
                        
                        System.out.println("Re-encrypted with malicious key " + maliciousKey + ": " + reEncrypted);
                        System.out.println("→ Forwarding corrupted message to Bob...\n");
                        
                        out.println(reEncrypted);
                        
                    } else if (taggedMessage.startsWith("BOB:")) {
                        String encrypted = taggedMessage.substring(4);
                        
                        String decrypted = HellmanUtils.decrypt(encrypted, sharedBob);
                        
                        System.out.println("\n╔════════════════════════════════════════╗");
                        System.out.println("║   INTERCEPTED: Bob -> Alice            ║");
                        System.out.println("╚════════════════════════════════════════╝");
                        System.out.println("Encrypted message: " + encrypted);
                        System.out.println("Decrypted message: " + decrypted);
                        System.out.println("Modified message: [HACKED] " + decrypted);

                        String modified = decrypted;
                        String reEncrypted = HellmanUtils.encrypt(modified, maliciousKey);
                        
                        System.out.println("Re-encrypted with malicious key " + maliciousKey + ": " + reEncrypted);
                        System.out.println("→ Forwarding corrupted message to Alice...\n");
                        
                        out.println(reEncrypted);
                    }
                }
            } catch (Exception e) {
                System.out.println("[ATTACKER] Connection closed.");
            }

        } else {
            System.out.println("\n=== " + role.toUpperCase() + " MODE ===\n");

            long privateKey = HellmanUtils.generatePrivateKey();
            long publicKey = HellmanUtils.generatePublicKey(g, privateKey, p);

            System.out.println("[" + role.toUpperCase() + "] My private key: " + privateKey);
            System.out.println("[" + role.toUpperCase() + "] My public key: " + publicKey);
            System.out.println("[" + role.toUpperCase() + "] Sending public key to server...");

            out.println(publicKey);
            long receivedPub = Long.parseLong(in.readLine());
            System.out.println("[" + role.toUpperCase() + "] Received other party's public key: " + receivedPub);

            long sharedKey = HellmanUtils.computeSharedSecret(receivedPub, privateKey, p);
            System.out.println("[" + role.toUpperCase() + "] Computed shared secret key: " + sharedKey);
            System.out.println("\n[" + role.toUpperCase() + "] *** KEY EXCHANGE COMPLETE ***");
            System.out.println("[" + role.toUpperCase() + "] Ready for encrypted communication\n");
            
            Thread receiveThread = new Thread(() -> {
                try {
                    while (true) {
                        String reply = in.readLine();
                        if (reply == null) break;
                        
                        String decrypted = HellmanUtils.decrypt(reply, sharedKey);
                        System.out.println("\n[RECEIVED] Encrypted: " + reply);
                        System.out.println("[DECRYPTED] Message: " + decrypted);
                        System.out.print("\nEnter message: ");
                    }
                } catch (Exception e) {
                    System.out.println("\n[" + role.toUpperCase() + "] Connection closed.");
                }
            });
            
            receiveThread.setDaemon(true);
            receiveThread.start();
            
            try {
                while (true) {
                    System.out.print("Enter message: ");
                    String msg = sc.nextLine();
                    
                    if (msg.isEmpty()) continue;

                    String encrypted = HellmanUtils.encrypt(msg, sharedKey);
                    System.out.println("[ENCRYPTED] " + encrypted);
                    out.println(encrypted);
                }
            } finally {
                socket.close();
                sc.close();
            }
        }
    }
}