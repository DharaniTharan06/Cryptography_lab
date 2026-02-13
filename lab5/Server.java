import java.io.*;
import java.net.*;

public class Server {

    static final int PORT = 5000;
    static long p = 23;
    static long g = 5;

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server running... Waiting for clients");

        Socket alice = serverSocket.accept();
        System.out.println("Alice connected");

        Socket bob = serverSocket.accept();
        System.out.println("Bob connected");

        Socket attacker = serverSocket.accept();
        System.out.println("Attacker connected");

        BufferedReader inAlice = new BufferedReader(new InputStreamReader(alice.getInputStream()));
        PrintWriter outAlice = new PrintWriter(alice.getOutputStream(), true);

        BufferedReader inBob = new BufferedReader(new InputStreamReader(bob.getInputStream()));
        PrintWriter outBob = new PrintWriter(bob.getOutputStream(), true);

        BufferedReader inAtt = new BufferedReader(new InputStreamReader(attacker.getInputStream()));
        PrintWriter outAtt = new PrintWriter(attacker.getOutputStream(), true);

        outAlice.println(p + "," + g);
        outBob.println(p + "," + g);
        outAtt.println(p + "," + g);

        long alicePub = Long.parseLong(inAlice.readLine());
        long bobPub = Long.parseLong(inBob.readLine());

        long fakeForAlice = Long.parseLong(inAtt.readLine());
        long fakeForBob = Long.parseLong(inAtt.readLine());

        outAlice.println(fakeForAlice);
        outBob.println(fakeForBob);

        System.out.println("MITM established. Keys replaced.");

        try {
            while (true) {

                String msgFromAlice = inAlice.readLine();
                outAtt.println(msgFromAlice);

                String modifiedByAttacker = inAtt.readLine();
                outBob.println(modifiedByAttacker);
                String replyFromBob = inBob.readLine();
                outAtt.println(replyFromBob);

                String modifiedReply = inAtt.readLine();
                outAlice.println(modifiedReply);
            }

        } finally {
            alice.close();
            bob.close();
            attacker.close();
            serverSocket.close();
        }
    }
}
