package TP1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client3 {

    public static void main(String[] args) {
        try (Socket commandSocket = new Socket("localhost", 2121);
             Scanner scanner = new Scanner(commandSocket.getInputStream());
             OutputStream commandOut = commandSocket.getOutputStream()) {

           
            System.out.println("reponse serveur: " + scanner.nextLine());

       
            sendCommand("USER miage\r\n", commandOut, scanner);
            sendCommand("PASS car\r\n", commandOut, scanner);

            sendCommand("PING\r\n", commandOut, scanner);
            System.out.println("reponse serveur: " + scanner.nextLine());  

          
            sendCommand("EPSV\r\n", commandOut, scanner);
            String epsvResponse = scanner.nextLine();
            System.out.println("reponse EPSV: " + epsvResponse);

            int port = extractPort(epsvResponse);


            try (Socket dataSocket = new Socket("localhost", port);
                 BufferedReader dataIn = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()))) {

              
                sendCommand("LINE test.txt 1\r\n", commandOut, scanner);

                String lineResponse = dataIn.readLine();
                System.out.println("reponse serveur (ligne spécifique): " + lineResponse);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendCommand(String command, OutputStream out, Scanner scanner) throws IOException {
        out.write(command.getBytes());
        System.out.println("Commande envoyée: " + command.trim());
        if (!command.startsWith("EPSV")) {
            System.out.println("reponse serveur: " + scanner.nextLine());
        }
    }

    private static int extractPort(String response) {
        int start = response.lastIndexOf('(');
        int end = response.lastIndexOf(')');
        String portSection = response.substring(start + 4, end - 1);
        return Integer.parseInt(portSection.split("\\|")[3]);
    }
}
