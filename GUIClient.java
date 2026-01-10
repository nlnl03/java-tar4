import java.awt.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class GUIClient extends JFrame {

  private static final String SERVER_IP = "localhost";
  private static final int SERVER_PORT = 9999;

  private JTextField tfName;
  private JTextField tfID;
  private JComboBox<String> cbItems;
  private JTextField tfQuantity;
  private JButton btnSend;
  private JButton btnDisconnect;
  private JTextArea logArea;

  private Socket socket;
  private PrintWriter out;
  private BufferedReader in;

  public GUIClient() {
    super("Order Management System");
    setupUI();
    connectToServer();
  }

  private void setupUI() {
    setLayout(new BorderLayout());

    JPanel panelForm = new JPanel(new GridLayout(5, 2, 5, 5));

    panelForm.add(new JLabel("Business Name:"));
    tfName = new JTextField();
    panelForm.add(tfName);

    panelForm.add(new JLabel("Business ID (5 digits):"));
    tfID = new JTextField();
    panelForm.add(tfID);

    panelForm.add(new JLabel("Select Item:"));
    String[] items = { "Sunglasses (1)", "Belt (2)", "Scarf (3)" };
    cbItems = new JComboBox<>(items);
    panelForm.add(cbItems);

    panelForm.add(new JLabel("Quantity:"));
    tfQuantity = new JTextField();
    panelForm.add(tfQuantity);

    btnSend = new JButton("Send Order");
    btnDisconnect = new JButton("Disconnect");
    btnDisconnect.setVisible(false);

    btnSend.addActionListener(e -> sendData());
    btnDisconnect.addActionListener(e -> disconnect());

    JPanel panelButtons = new JPanel();
    panelButtons.add(btnSend);
    panelButtons.add(btnDisconnect);

    add(panelForm, BorderLayout.NORTH);
    add(panelButtons, BorderLayout.CENTER);

    logArea = new JTextArea(5, 30);
    logArea.setEditable(false);
    add(new JScrollPane(logArea), BorderLayout.SOUTH);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(450, 350);
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void connectToServer() {
    try {
      socket = new Socket(SERVER_IP, SERVER_PORT);
      out = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      log("Connected to server on port " + SERVER_PORT);
    } catch (IOException e) {
      log("Error connecting to server: " + e.getMessage());
      JOptionPane.showMessageDialog(
        this,
        "Cannot connect to server!",
        "Connection Error",
        JOptionPane.ERROR_MESSAGE
      );
      btnSend.setEnabled(false);
    }
  }

  private void sendData() {
    String name = tfName.getText().trim();
    String id = tfID.getText().trim();
    String qtyStr = tfQuantity.getText().trim();

    int itemType = cbItems.getSelectedIndex() + 1;

    if (name.isEmpty() || id.isEmpty() || qtyStr.isEmpty()) {
      JOptionPane.showMessageDialog(
        this,
        "Please fill all fields.",
        "Error",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    if (!id.matches("\\d{5}")) {
      JOptionPane.showMessageDialog(
        this,
        "Business ID must be exactly 5 digits.",
        "Input Error",
        JOptionPane.ERROR_MESSAGE
      );
      return;
    }

    String message = name + "#" + id + "#" + itemType + "#" + qtyStr;

    if (out != null) {
      out.println(message);
      log("Sent: " + message);

      try {
        String response = in.readLine();
        handleServerResponse(response);
      } catch (IOException e) {
        log("Error reading response: " + e.getMessage());
      }
    }
  }

  private void handleServerResponse(String code) {
    if (code == null) return;

    switch (code) {
      case "100":
        log("Server: Success (Code 100)");
        JOptionPane.showMessageDialog(this, "Order processed successfully!");
        btnDisconnect.setVisible(true);
        break;
      case "200":
        log("Server Error: Missing Data (Code 200)");
        JOptionPane.showMessageDialog(
          this,
          "Error: Missing Data or Invalid format.",
          "Server Error",
          JOptionPane.ERROR_MESSAGE
        );
        break;
      case "201":
        log("Server Error: ID/Name Mismatch (Code 201)");
        JOptionPane.showMessageDialog(
          this,
          "Error: Business Name does not match existing ID.",
          "Server Error",
          JOptionPane.ERROR_MESSAGE
        );
        break;
      case "202":
        log("Server Error: Invalid Quantity/Item (Code 202)");
        JOptionPane.showMessageDialog(
          this,
          "Error: Quantity must be positive.",
          "Server Error",
          JOptionPane.ERROR_MESSAGE
        );
        break;
      default:
        log("Unknown response: " + code);
    }
  }

  private void disconnect() {
    if (out != null) {
      out.println("EXIT");
    }
    try {
      if (socket != null && !socket.isClosed()) socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    dispose();
    System.exit(0);
  }

  private void log(String msg) {
    logArea.append(msg + "\n");
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(GUIClient::new);
  }
}
