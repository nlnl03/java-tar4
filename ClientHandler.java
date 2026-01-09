import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

  private final Socket socket;

  ClientHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    try (
      BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream())
      );
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
    ) {
      String line;
      while ((line = in.readLine()) != null) {
        System.out.println(
          "Received from " + socket.getInetAddress() + ": " + line
        );
        out.println("Echo: " + line);
      }
    } catch (IOException e) {
      System.err.println("Client handler exception: " + e.getMessage());
    } finally {
      try {
        socket.close();
      } catch (IOException ignored) {}
      System.out.println(
        "Connection with " + socket.getInetAddress() + " closed."
      );
    }
  }
}
