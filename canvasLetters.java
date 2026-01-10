import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import javax.swing.*;

public class canvasLetters extends JPanel implements ActionListener {

  // פוליגונים ל-4 האותיות
  private Polygon polyM, polyE, polyL, polyA;

  // --- משתני אנימציה ---

  // מיקומים התחלתיים (נפזר אותם על המסך)
  private int xM = 50,
    yM = 50;
  private int xE = 250,
    yE = 50;
  private int xL = 50,
    yL = 250;
  private int xA = 250,
    yA = 250;

  // מהירויות תזוזה לכל אות
  private int dxM = 2,
    dyM = 3;
  private int dxE = -2,
    dyE = 2;
  private int dxL = 3,
    dyL = -2;
  private int dxA = -3,
    dyA = -3;

  // זווית סיבוב (עבור M ו-A)
  private double angleM = 0;
  private double angleA = 0;

  // סקיילינג (עבור E)
  private double scaleE = 1.0;
  private boolean growingE = true;

  // צבעים
  private Color cM = Color.BLUE;
  private Color cE = Color.RED;
  private Color cL = Color.GREEN;
  private Color cA = Color.ORANGE;

  private Random random = new Random();

  public canvasLetters() {
    // טעינת 4 הקבצים
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
      p.addPoint(0, 50); // משולש חירום
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

    // === ציור M (סיבוב) ===
    drawRotated(g2d, polyM, xM, yM, cM, angleM);

    // === ציור E (שינוי גודל) ===
    drawScaled(g2d, polyE, xE, yE, cE, scaleE);

    // === ציור L (תזוזה רגילה) ===
    drawRotated(g2d, polyL, xL, yL, cL, 0); // זווית 0 = ללא סיבוב

    // === ציור A (סיבוב הפוך) ===
    drawRotated(g2d, polyA, xA, yA, cA, angleA);
  }

  // פונקציית עזר לציור עם סיבוב
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

  // פונקציית עזר לציור עם שינוי גודל
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

    // 1. עדכון מיקומים (לכל ה-4)
    xM += dxM;
    yM += dyM;
    xE += dxE;
    yE += dyE;
    xL += dxL;
    yL += dyL;
    xA += dxA;
    yA += dyA;

    // 2. בדיקת קירות ושינוי צבע (לוגיקה זהה לכולם)
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

    // 3. עדכון אנימציות פנימיות
    angleM += 2; // M מסתובבת ימינה
    angleA -= 2; // A מסתובבת שמאלה

    // E פועמת (גדלה/קטנה)
    if (growingE) {
      scaleE += 0.01;
      if (scaleE >= 1.3) growingE = false;
    } else {
      scaleE -= 0.01;
      if (scaleE <= 0.7) growingE = true;
    }

    repaint();
  }

  // עזרים לבדיקת קירות
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
    f.add(new canvasLetters());
    f.setSize(800, 600);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLocationRelativeTo(null);
    f.setVisible(true);
  }
}
