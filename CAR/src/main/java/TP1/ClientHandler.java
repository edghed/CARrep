package TP1;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private static final String DEFAULT_USERNAME = "Edem";
    private static final String DEFAULT_PASSWORD = "mdp";

    private Socket socket;
    private Scanner scanner;
    private OutputStream outputStream;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            this.scanner = new Scanner(socket.getInputStream());
            this.outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            sendResponse("220 Service ready");

            String userCommand = receiveCommand();
            if (!userCommand.startsWith("USER ")) {
                sendResponse("500 Syntax error, command unrecognized");
                return;
            }
            String username = userCommand.substring(5).trim();
            sendResponse("331 User name ok, need password");

            String passCommand = receiveCommand();
            if (!passCommand.startsWith("PASS ")) {
                sendResponse("500 Syntax error, command unrecognized");
                return;
            }
            String password = passCommand.substring(5).trim();
            System.out.println("UserName: " + username);
            System.out.println("Password: " + password);

            if (checkCredentials(username, password)) {
                sendResponse("230 User logged in");
                handleQuitCommand();
            } else {
                sendResponse("530 Not logged in");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void sendResponse(String response) throws IOException {
        response += "\r\n";
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String receiveCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "";
    }

    private boolean checkCredentials(String username, String password) {
        return DEFAULT_USERNAME.equals(username) && DEFAULT_PASSWORD.equals(password);
    }

    private void handleQuitCommand() throws IOException {
        while (true) {
            String command = receiveCommand();
            if (command.startsWith("QUIT")) {
                System.out.println("Commande : QUIT");
                sendResponse("221 Service closing control connection");
                break;
            } else {
                System.out.println("Commande : " + command);
                sendResponse("500 Syntax error, command unrecognized");
            }
        }
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

