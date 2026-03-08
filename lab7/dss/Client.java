import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) throws Exception {

        Socket s = new Socket("localhost",7070);

        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        Scanner sc = new Scanner(System.in);

        long privateKey = 1;
        long publicKey = 4;

        System.out.println("Private Key = " + privateKey);
        System.out.println("Public Key = " + publicKey);

        System.out.print("Enter transaction: ");
        String transaction = sc.nextLine();

        long hash = Math.abs(transaction.hashCode()) % 100;
        long signature = hash * privateKey;

        dos.writeUTF(transaction);
        dos.writeLong(signature);
        dos.writeLong(publicKey);

        String result = br.readLine();
        System.out.println(result);

        s.close();
    }
}