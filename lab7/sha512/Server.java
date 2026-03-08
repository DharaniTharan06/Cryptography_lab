import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(6060);

        System.out.println("Server waiting on port 6060");

        Socket socket = serverSocket.accept();

        BufferedReader in =
                new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String originalMessage = in.readLine();

        System.out.println("Message from client: " + originalMessage);

        for (int i = 0; i < 16; i++) {

            String word = in.readLine();

            BigInteger bi = new BigInteger(word, 2);

            String hex = String.format("%016X", bi);

            System.out.println("W" + i + " = " + hex);
        }

        socket.close();
        serverSocket.close();
    }
}