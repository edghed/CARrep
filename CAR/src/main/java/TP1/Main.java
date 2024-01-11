package TP1;



import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        int port = 2126;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Serveur FTP en attente de connexions sur le port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nouvelle connexion entrante: " + socket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
