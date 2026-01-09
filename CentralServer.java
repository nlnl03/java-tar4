import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class CentralServer {

  private static final int PORT = 9999;
  private ArrayList<ClientHandler> clients = new ArrayList<>();
  public static void main(String[] args) {
    System.out.println("Starting Central Server...");
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      System.out.println("Central Server is running on port " + PORT);
      while (true) {
        try {
          Socket clientSocket = serverSocket.accept();
          System.out.println(
            "New client connected: " + clientSocket.getInetAddress()
          );
          Thread clientThread = new Thread(new ClientHandler(clientSocket));
          clientThread.start();
        } catch (IOException e) {
          System.err.println(
            "Error accepting client connection: " + e.getMessage()
          );
          if (serverSocket.isClosed()) break;
        }
      }
    } catch (IOException e) {
      System.err.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
