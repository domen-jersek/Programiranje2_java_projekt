import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JumpKingPlatformLayout {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Jump King Platform Layout");
        frame.setSize(new Dimension(800, 600));
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setResizable(true);
        
        GamePanel panel = new GamePanel();
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        
        JPanel north = new JPanel();
        frame.add(north, BorderLayout.NORTH);
        
        JPanel south = new JPanel();
        frame.add(south, BorderLayout.SOUTH);
        south.setLayout(new GridLayout(3, 1));
        for (int i = 0; i < 3; i++) {
            float rgb = (i + 1) / 4.0f;
            Color color = new Color(rgb, rgb, rgb);
            JPanel subsouth = new JPanel();
            subsouth.setBackground(color);
            south.add(subsouth);
        }
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        while (true) {
            frame.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

@SuppressWarnings("serial")
class GamePanel extends JPanel {
    private List<Platform> platforms;
    
    public GamePanel() {
        super();
        setBackground(Color.BLACK);
        initializePlatforms();
    }
    
    private void initializePlatforms() {
        platforms = new ArrayList<>();
        
        // Ground platform
        platforms.add(new Vodoravna(100, 550, 0, 1.0));
        platforms.add(new Vodoravna(300, 550, 0, 1.0));
        platforms.add(new Vodoravna(500, 550, 0, 1.0));
        
        // First level platforms
        platforms.add(new Vodoravna(200, 480, 0, 1.0));
        platforms.add(new Vodoravna(400, 480, 0, 1.0));
        
        // Second level platforms
        platforms.add(new Vodoravna(100, 410, 0, 1.0));
        platforms.add(new slopeUp(300, 410, 0, 1.0));
        platforms.add(new Vodoravna(500, 410, 0, 1.0));
        
        // Third level platforms
        platforms.add(new Vodoravna(200, 340, 0, 1.0));
        platforms.add(new slopeDown(400, 340, 0, 1.0));
        platforms.add(new Navpicna(600, 340, 0, 1.0));
        
        // Fourth level platforms
        platforms.add(new slopeDown(100, 270, 0, 1.0));
        platforms.add(new Vodoravna(300, 270, 0, 1.0));
        platforms.add(new slopeUp(500, 270, 0, 1.0));
        
        // Fifth level platforms
        platforms.add(new Vodoravna(400, 200, 0, 1.0));
        
        // Sixth level platforms
        platforms.add(new Vodoravna(200, 130, 0, 1.0));
        platforms.add(new Vodoravna(500, 130, 0, 1.0));
        
        // Top level platform (goal)
        platforms.add(new Vodoravna(350, 60, 0, 1.0));
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Set rendering quality
        g2d.setStroke(new BasicStroke(3));
        
        // Draw all platforms
        for (Platform platform : platforms) {
            drawPlatform(g2d, platform);
        }
        
        // Draw "goal" text at the top platform
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("GOAL", 350, 50);
        
        // Draw "start" text at the bottom platform
        g2d.setColor(Color.GREEN);
        g2d.drawString("START", 100, 545);
    }
    
    private void drawPlatform(Graphics2D g2d, Platform platform) {
        int platformLength = 100;
        double x = platform.getX();
        double y = platform.getY();
        float angle = platform.getKot();
        
        if (platform instanceof Vodoravna) {
            g2d.setColor(Color.WHITE);
            g2d.draw(new Line2D.Double(x, y, x + platformLength, y));
        } else if (platform instanceof Navpicna) {
            g2d.setColor(Color.CYAN);
            g2d.draw(new Line2D.Double(x, y - platformLength, x, y));
        } else if (platform instanceof slopeDown) {
            g2d.setColor(Color.ORANGE);
            g2d.draw(new Line2D.Double(x, y, x + platformLength, y + platformLength));
        } else if (platform instanceof slopeUp) {
            g2d.setColor(Color.MAGENTA);
            g2d.draw(new Line2D.Double(x, y, x + platformLength, y - platformLength));
        }
    }
}

class Platform {
    private double x;
    private double y;
    private float kot;
    private double trdota;
    
    public Platform(double x, double y, float kot, double trdota) {
        super();
        this.x = x;
        this.y = y;
        this.kot = kot;
        this.trdota = trdota;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public float getKot() {
        return kot;
    }
    
    public double getTrdota() {
        return trdota;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public void setKot(float kot) {
        this.kot = kot;
    }
    
    public void setTrdota(double trdota) {
        this.trdota = trdota;
    }
}

class Vodoravna extends Platform {
    public Vodoravna(double x, double y, float kot, double trdota) {
        super(x, y, 0.0f, 1.0);
    }
}

class Navpicna extends Platform {
    public Navpicna(double x, double y, float kot, double trdota) {
        super(x, y, (float) Math.toRadians(90), 1.0);
    }
}

class slopeDown extends Platform {
    public slopeDown(double x, double y, float kot, double trdota) {
        super(x, y, (float) Math.toRadians(45), 1.0);
    }
}

class slopeUp extends Platform {
    public slopeUp(double x, double y, float kot, double trdota) {
        super(x, y, (float) Math.toRadians(135), 1.0);
    }
}