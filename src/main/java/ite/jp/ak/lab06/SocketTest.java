package ite.jp.ak.lab06;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketTest {
    private static final int PORT = 4444;
    private static final String SERVER = "localhost";

    public static void main(String[] args) {

        try {

            var clientSocket = new Socket(SERVER, PORT);

            var outputStream = clientSocket.getOutputStream();
            var wrtr = new PrintWriter(outputStream, true);

            var scanner = new Scanner(System.in);

            System.out.print("> ");
            String message = scanner.nextLine();

            wrtr.println(message);

            clientSocket.close();
        } catch (UnknownHostException ex) {
            System.out.println("Unable to connect" + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }
}
