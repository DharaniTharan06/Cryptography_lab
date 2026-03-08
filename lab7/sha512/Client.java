import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter message: ");
        String message = sc.nextLine();

        String preprocessed = SHA512Utils.preprocess(message);

        String[] words = SHA512Utils.extractMessageSchedule(preprocessed);

        Socket socket = new Socket("localhost", 6060);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        out.println(message);

        for (int i = 0; i < 16; i++) {
            out.println(words[i]);
        }

        socket.close();
        sc.close();
    }
}