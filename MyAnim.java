import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import javax.swing.*;

public class MyAnim extends JPanel implements ActionListener {

  private Polygon polyM, polyE, polyL, polyA;

  private int xM = 50,
    yM = 50;
  private int xE = 250,
    yE = 50;
  private int xL = 50,
    yL = 250;
  private int xA = 250,
    yA = 250;

  private int dxM = -2,
    dyM = 3;
  private int dxE = -2,
    dyE = 2;
  private int dxL = 3,
    dyL = -2;
  private int dxA = -3,
    dyA = -3;

  private double angleM = 0;
  private double angleA = 0;

  private double scaleE = 1.0;
  private boolean growingE = true;

  private Color cM = Color.BLUE;
  private Color cE = Color.RED;
  private Color cL = Color.GREEN;
  private Color cA = Color.ORANGE;

  private Random random = new Random();

  public MyAnim() {
    polyM = loadPolygon("M.txt");
    polyE = loadPolygon("E.txt");
    polyL = loadPolygon("L.txt");
    polyA = loadPolygon("A.txt");

    Timer timer = new Timer(16, this);
    timer.start();
  }

  private Polygon loadPolygon(String filename) {
    Polygon p = new Polygon();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          p.addPoint(
            Integer.parseInt(parts[0].trim()),
            Integer.parseInt(parts[1].trim())
          );
        }
      }
    } catch (IOException e) {
      System.err.println("Error reading " + filename);
      p.addPoint(0, 0);
      p.addPoint(50, 50);
      p.addPoint(0, 50);
    }
    return p;
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON
    );

    drawRotated(g2d, polyM, xM, yM, cM, angleM);

    drawScaled(g2d, polyE, xE, yE, cE, scaleE);

    drawRotated(g2d, polyL, xL, yL, cL, 0);

    drawRotated(g2d, polyA, xA, yA, cA, angleA);
  }

  private void drawRotated(
    Graphics2D g2,
    Polygon p,
    int x,
    int y,
    Color c,
    double deg
  ) {
    AffineTransform old = g2.getTransform();
    g2.translate(x, y);
    if (deg != 0) {
      Rectangle b = p.getBounds();
      g2.rotate(Math.toRadians(deg), b.getCenterX(), b.getCenterY());
    }
    g2.setColor(c);
    g2.fillPolygon(p);
    g2.setTransform(old);
  }

  private void drawScaled(
    Graphics2D g2,
    Polygon p,
    int x,
    int y,
    Color c,
    double s
  ) {
    AffineTransform old = g2.getTransform();
    g2.translate(x, y);
    g2.scale(s, s);
    g2.setColor(c);
    g2.fillPolygon(p);
    g2.setTransform(old);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    int w = getWidth();
    int h = getHeight();

    xM += dxM;
    yM += dyM;
    xE += dxE;
    yE += dyE;
    xL += dxL;
    yL += dyL;
    xA += dxA;
    yA += dyA;

    if (checkWall(xM, yM, polyM, 1.0, w, h)) {
      dxM = -dxM;
      cM = rndColor();
    }
    if (checkWallY(xM, yM, polyM, 1.0, w, h)) {
      dyM = -dyM;
      cM = rndColor();
    }

    if (checkWall(xE, yE, polyE, scaleE, w, h)) {
      dxE = -dxE;
      cE = rndColor();
    }
    if (checkWallY(xE, yE, polyE, scaleE, w, h)) {
      dyE = -dyE;
      cE = rndColor();
    }

    if (checkWall(xL, yL, polyL, 1.0, w, h)) {
      dxL = -dxL;
      cL = rndColor();
    }
    if (checkWallY(xL, yL, polyL, 1.0, w, h)) {
      dyL = -dyL;
      cL = rndColor();
    }

    if (checkWall(xA, yA, polyA, 1.0, w, h)) {
      dxA = -dxA;
      cA = rndColor();
    }
    if (checkWallY(xA, yA, polyA, 1.0, w, h)) {
      dyA = -dyA;
      cA = rndColor();
    }

    angleM += 2;
    angleA -= 2;

    if (growingE) {
      scaleE += 0.01;
      if (scaleE >= 1.3) growingE = false;
    } else {
      scaleE -= 0.01;
      if (scaleE <= 0.7) growingE = true;
    }

    repaint();
  }

  private boolean checkWall(int x, int y, Polygon p, double s, int w, int h) {
    return (x < 0 || x + p.getBounds().width * s > w);
  }

  private boolean checkWallY(int x, int y, Polygon p, double s, int w, int h) {
    return (y < 0 || y + p.getBounds().height * s > h);
  }

  private Color rndColor() {
    return new Color(
      random.nextInt(256),
      random.nextInt(256),
      random.nextInt(256)
    );
  }

  public static void main(String[] args) {
    JFrame f = new JFrame("M E L A - Animation");
    f.add(new MyAnim());
    f.setSize(800, 600);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLocationRelativeTo(null);
    f.setVisible(true);
  }
}
