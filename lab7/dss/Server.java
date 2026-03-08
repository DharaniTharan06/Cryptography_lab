import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(7070);
        System.out.println("Server waiting on port 7070");
        Socket s = ss.accept();
        System.out.println("Client connected");

        DataInputStream dis = new DataInputStream(s.getInputStream());
        PrintStream ps = new PrintStream(s.getOutputStream());

        String transaction = dis.readUTF();
        long signature = dis.readLong();
        long publicKey = dis.readLong();

        System.out.println("Transaction Received: " + transaction);
        System.out.println("Signature Received: " + signature);
        System.out.println("Verifying using Public Key = " + publicKey);

        long hash = Math.abs(transaction.hashCode()) % 100;

        if(signature == hash * publicKey)
        {
            System.out.println("VERIFIED: Signature valid");
            ps.println("VERIFIED: Signature valid");
        }
        else
        {
            System.out.println("VERIFIED: Signature invalid");
            ps.println("VERIFIED: Signature invalid");
        }

        s.close();
        ss.close();
    }
}