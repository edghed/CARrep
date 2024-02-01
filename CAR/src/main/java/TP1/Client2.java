package TP1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client2 {

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

           
            String pingCommand = "PING\r\n";
            out.write(pingCommand.getBytes());
            System.out.println("message client: " + pingCommand.trim());
            System.out.println("reponse serveur: " + scanner.nextLine()); 
             
            String reponse2=scanner.nextLine();
            System.out.println("reponse serveur: " + reponse2);

          if (reponse2.equals("PONG")){

            out.write("200 PONG command ok \r\n" .getBytes());
          }
          else {
            System.out.println("502 Unknown command");
          }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
