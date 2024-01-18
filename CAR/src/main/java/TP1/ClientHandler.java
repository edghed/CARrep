package TP1;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Scanner scanner;
    private HashMap<String, String> users;

    public ClientHandler(Socket socket, HashMap<String, String> users) {
        this.socket = socket;
        this.users = users;
        try {
            InputStream in = socket.getInputStream();
            scanner = new Scanner(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            OutputStream out = socket.getOutputStream();
            String reponse = "220 Service ready\r\n";
            String userName="" ;
            while (true) {
                out.write(reponse.getBytes());
                out.flush();
                reponse = "";  

                String command = receiveCommand();
                if(!(command.startsWith("USER") || command.startsWith("PASS")) ){
                System.out.println("Commande: " + command);}
                if (command.startsWith("USER")) {
                    userName = command.substring(5).trim();
                    reponse = "331 User name ok, need password\r\n" ;
                } else if (command.startsWith("PASS")) {
                    String pass = command.substring(5).trim();
                    System.out.println("User: " + userName);
                    System.out.println("Pass: " + pass);
                    reponse = checkCredentials(userName, pass) ? "230 User logged in\r\n" : "530 User not logged in\r\n" ;
                } else if (command.startsWith("QUIT")) {
                    reponse = "221 Service closing control connection\r\n";
                    break;
                } 
                else {
                    reponse = "502 Command not implemented\r\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    

    private String receiveCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "";
    }

    private boolean checkCredentials(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    
}


