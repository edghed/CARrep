package TP1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        // J'ai ajouté une map pour pouvoir ajouter plusieurs utilisateurs.
        HashMap<String, String> users = new HashMap<>();
        users.put("Edem", "mdp");
        users.put("miage","car");

        int port = 2121;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur FTP en attente de connexions sur le port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouvelle connexion entrante: " + socket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(socket, users);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
