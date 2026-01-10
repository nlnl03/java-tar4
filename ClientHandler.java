import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler extends Thread {

  private Socket socket;

  public ClientHandler(Socket socket) {
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
      String inputLine;

      while ((inputLine = in.readLine()) != null) {
        if (inputLine.equals("EXIT")) {
          break;
        }

        String[] parts = inputLine.split("#");

        if (
          parts.length < 4 || Arrays.stream(parts).anyMatch(String::isEmpty)
        ) {
          out.println("200");
          continue;
        }

        String name = parts[0];
        String id = parts[1];
        int itemType, quantity;

        try {
          itemType = Integer.parseInt(parts[2]);
          quantity = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
          out.println("200");
          continue;
        }

        if (quantity <= 0 || itemType < 1 || itemType > 3) {
          out.println("202");
          continue;
        }

        String responseCode = CentralServer.processRequest(
          name,
          id,
          itemType,
          quantity
        );

        out.println(responseCode);
      }
    } catch (IOException e) {
      System.out.println("Client disconnected.");
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
