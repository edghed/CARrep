package TP1;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private String currentDirectory;
    private Socket socket;
    private Scanner scanner;
    private HashMap<String, String> users;
    private ServerSocket dataServerSocket;

    public ClientHandler(Socket socket, HashMap<String, String> users) {
        this.socket = socket;
        this.users = users;
        this.currentDirectory = System.getProperty("user.dir");}

    @Override
    public void run() {
        try {
            InputStream in = socket.getInputStream();
            scanner = new Scanner(in);
            OutputStream out = socket.getOutputStream();
            String reponse = "220 Service ready\r\n";
            String userName="" ;
             Boolean isVoid=false;
            while (true) {
                if(!isVoid)
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
                else if (command.startsWith("SIZE")) {
                    
                    reponse = sizeCommand(command);
                }
                
                else if (command.startsWith("EPSV")) {
                    
                    reponse = EpsvCommand();
                }

                else if (command.startsWith("RETR")) {
                    isVoid=true;

                  retrCommand(command,out);
                }
                else if (command.startsWith("LIST")) {
                    isVoid=true;
                    
                     dirCommand(command,out);
                }
                else if (command.startsWith("CWD")) {
                    isVoid=true;

                  cwdCommand(command, out);
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
    
    private String sizeCommand(String command) {
        String fileName = command.substring(5).trim();
        File file = new File(fileName);
        String reponse="";
        if (file.exists()) {
            reponse="213 " + file.length() + "\r\n";  
        } else {
            reponse="550 File not found\r\n";
        }
        return reponse;
    }

       public String EpsvCommand() {
        String reponse="";
        try {
            if (dataServerSocket != null && !dataServerSocket.isClosed()) {
                dataServerSocket.close();
            }
            dataServerSocket = new ServerSocket(0);
            int port = dataServerSocket.getLocalPort();

           reponse= "229 Entering Extended Passive Mode (|||" + port + "|)\r\n";
        } catch (IOException e) {
            reponse= "500 Internal Server Error\r\n";
        }
        return reponse;
    }

 
    private void retrCommand(String command, OutputStream out) throws IOException {
        String fileName = command.substring(5).trim();
        File file = new File(currentDirectory+fileName);
        if (!file.exists()) {
            out.write("550 File not found\r\n".getBytes());
        }
    
        try (FileInputStream fin = new FileInputStream(file);
             Socket dataSocket = dataServerSocket.accept(); 
             BufferedOutputStream dOut = new BufferedOutputStream(dataSocket.getOutputStream())) {
            
  
            out.write(("150 Opening data connection for " + fileName + "\r\n").getBytes());

    
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fin.read(buffer)) != -1) {
                dOut.write(buffer, 0, bytesRead);
            }
            
        

            out.write("226 Transfer complete\r\n".getBytes());
            

            
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
            out.write("552 Requested file action aborted\r\n".getBytes());
        }
    }
    private void dirCommand(String command, OutputStream out) throws IOException {
        String directoryPath;
    
        try {
            if (command.length() > 4) {
                String requestedPath = command.substring(4).trim();
                directoryPath = Paths.get(currentDirectory).resolve(requestedPath).normalize().toString();
            } else {
                directoryPath = currentDirectory;
            }

    
            File dir = new File(directoryPath);
            out.write("150 Opening ASCII mode data connection for file list\r\n".getBytes());
            out.flush();
    
            if (dir.exists() && dir.isDirectory()) {
                try (Socket dataSocket = dataServerSocket.accept();
                     OutputStream dOut = dataSocket.getOutputStream()) {
                    File[] files = dir.listFiles();
                    if (files != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        for (File file : files) {
                            String fileInfo = String.format("%-20s %12d %s\r\n",
                                    file.getName(),
                                    file.length(),
                                    dateFormat.format(new Date(file.lastModified())));
                            dOut.write(fileInfo.getBytes());
                        }
                        dOut.flush();
                    }
                }
                out.write("226 Transfer complete.\r\n".getBytes());
            } else {
                out.write("550 Directory not found.\r\n".getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace(); 
            out.write("550 Error processing request\r\n".getBytes());
        }
    }
    
    
    private void cwdCommand(String command, OutputStream out) throws IOException {
        String directoryName = command.substring(4).trim();
    
     
        Path newPath = Paths.get(currentDirectory).resolve(directoryName).normalize();
    
        File newDirectory = newPath.toFile();
    
        if (newDirectory.exists() && newDirectory.isDirectory()) {
            currentDirectory = newDirectory.getAbsolutePath();
    
            out.write("250 Directory changed successfully\r\n".getBytes());
        } else {
            out.write("550 Directory not found\r\n".getBytes());
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


