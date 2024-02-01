package TP1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client1 {

    public static void main(String[] args) {
       

        try (Socket socket = new Socket("localhost", 2121);
             Scanner scanner = new Scanner(socket.getInputStream());
             OutputStream out = socket.getOutputStream()) {

            System.out.println("reponse serveur: " + scanner.nextLine());

            String userCommand = "USER miage\r\n";
            out.write(userCommand.getBytes());
            System.out.println("message client: " + userCommand.trim());
            System.out.println("reponse serveur: " + scanner.nextLine());

            String passCommand = "PASS car\r\n";
            out.write(passCommand.getBytes());
            System.out.println("message client: " + passCommand.trim());
            System.out.println("reponse serveur: " + scanner.nextLine());

            String quitCommand = "QUIT\r\n";
            out.write(quitCommand.getBytes());
            System.out.println("message client: " + quitCommand.trim());
            System.out.println("reponse serveur: " + scanner.nextLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
