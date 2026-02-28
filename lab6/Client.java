import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        int e = 7;
        int n = 143;

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter message (M < 143): ");
        long message = sc.nextLong();

        long cipher = HomoRsa.encrypt(message, e, n);

        Socket socket = new Socket("localhost", 5000);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(cipher);

        System.out.println("Encrypted message sent.");

        socket.close();
    }
}