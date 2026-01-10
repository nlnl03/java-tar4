import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CentralServer {

  private static final int PORT = 9999;

  private static List<Business> businessList = new ArrayList<>();

  public static void main(String[] args) {
    System.out.println("Central Server is running on port " + PORT + "...");

    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      while (true) {
        Socket clientSocket = serverSocket.accept();

        new ClientHandler(clientSocket).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static synchronized String processRequest(
    String name,
    String id,
    int itemType,
    int quantity
  ) {
    Business existingBusiness = null;

    for (Business b : businessList) {
      if (b.getId().equals(id)) {
        existingBusiness = b;
        break;
      }
    }

    if (existingBusiness != null) {
      if (!existingBusiness.getName().equals(name)) {
        return "201";
      }
      existingBusiness.addQuantity(itemType, quantity);
      System.out.println("Updated: " + existingBusiness);
    } else {
      Business newBusiness = new Business(name, id);
      newBusiness.addQuantity(itemType, quantity);
      businessList.add(newBusiness);
      System.out.println("Created: " + newBusiness);
    }

    return "100";
  }
}
