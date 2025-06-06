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
import javax.imageio.*;
import java.io.File;
import java.awt.Image;
import java.io.IOException;
import java.security.Key;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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
            panel.update(); // Add this line
            frame.repaint();
            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

@SuppressWarnings("serial")
class GamePanel extends JPanel implements KeyListener{

    private List<Platform> platforms;
    private King king;
    private boolean[] keys = new boolean[256]; // Track pressed keys
    
    
    public GamePanel() {
        super();
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();
        initializePlatforms();
        king = new King(190, 510, 0, 0, 15, 0.5f);
    }

    public void update() {
        // Handle input
        if (keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A]) {
            king.move(-1);
        }
        if (keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D]) {
            king.move(1);
        }
        if (keys[KeyEvent.VK_SPACE] || keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W]) {
            king.jump();
        }
        
        // Update physics
        king.update();
        
        // Check collisions
        checkCollisions();
    }



    
    private void initializePlatforms() {
        platforms = new ArrayList<>();
        
        // Ground platform
        platforms.add(new Navpicna(100, 550, 0, 1.0));
        platforms.add(new Vodoravna(100, 550, 0, 1.0));
        platforms.add(new Vodoravna(200, 550, 0, 1.0));
        platforms.add(new Vodoravna(300, 550, 0, 1.0));
        platforms.add(new Vodoravna(400, 550, 0, 1.0));
        platforms.add(new Vodoravna(500, 550, 0, 1.0));
        platforms.add(new Navpicna(600, 550, 0, 1.0));
        
        // First level platforms
        platforms.add(new Navpicna(100, 480, 0, 1.0));
        platforms.add(new Vodoravna(200, 480, 0, 1.0));
        platforms.add(new Vodoravna(400, 480, 0, 1.0));
        platforms.add(new Navpicna(600, 480, 0, 1.0));
        
        // Second level platforms
        platforms.add(new Navpicna(100, 410, 0, 1.0));
        platforms.add(new Vodoravna(100, 410, 0, 1.0));
        platforms.add(new slopeUp(300, 410, 0, 1.0));
        platforms.add(new Vodoravna(500, 410, 0, 1.0));
        platforms.add(new Navpicna(600, 410, 0, 1.0));
        
        // Third level platforms
        platforms.add(new Navpicna(100, 340, 0, 1.0));
        platforms.add(new Vodoravna(200, 340, 0, 1.0));
        platforms.add(new slopeDown(400, 340, 0, 1.0));
        platforms.add(new Navpicna(600, 340, 0, 1.0));
        
        // Fourth level platforms
        platforms.add(new Navpicna(100, 270, 0, 1.0));
        platforms.add(new slopeDown(100, 270, 0, 1.0));
        platforms.add(new Vodoravna(300, 270, 0, 1.0));
        platforms.add(new slopeUp(500, 270, 0, 1.0));
        platforms.add(new Navpicna(600, 270, 0, 1.0));
        
        // Fifth level platforms
        platforms.add(new Navpicna(100, 200, 0, 1.0));
        platforms.add(new Vodoravna(400, 200, 0, 1.0));
        platforms.add(new Navpicna(600, 200, 0, 1.0));
        
        // Sixth level platforms
        platforms.add(new Navpicna(100, 130, 0, 1.0));
        platforms.add(new Vodoravna(200, 130, 0, 1.0));
        platforms.add(new Vodoravna(500, 130, 0, 1.0));
        platforms.add(new Navpicna(600, 130, 0, 1.0));

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
        
        // drawing the King
        drawKing(g2d);
        //movingKing(g2d, new King(190, 510, 1, 10, 10, 10));

        // Draw "goal" text at the top platform
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("GOAL", 350, 50);
        
        // Draw "start" text at the bottom platform
        g2d.setColor(Color.GREEN);
        g2d.drawString("START", 120, 545);
    }

    private void drawKing(Graphics2D g2d) {
        float x = king.getX();
        float y = king.getY();
        Image image = king.getImage();
        
        if (image != null) {
            g2d.drawImage(image, (int)x, (int)y, (int)king.getWidth(), (int)king.getHeight(), null);
        } else {
            // Draw a simple rectangle if image is missing
            g2d.setColor(Color.RED);
            g2d.fillRect((int)x, (int)y, (int)king.getWidth(), (int)king.getHeight());
        }
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
            g2d.draw(new Line2D.Double(x, y, x + platformLength*Math.sqrt(2)/2, y + platformLength*Math.sqrt(2)/2));
        } else if (platform instanceof slopeUp) {
            g2d.setColor(Color.MAGENTA);
            g2d.draw(new Line2D.Double(x, y, x + platformLength*Math.sqrt(2)/2, y - platformLength*Math.sqrt(2)/2));
        }
    }

    // private void movingKing(Graphics2D g2d, King king) {
        
    //     float x = king.getX();
    //     float y = king.getY();
    //     float kot = king.getKot();
    //     //float hitrost = king.getHitrost();
    //     float jumpStrength = king.getJumpStrength();
    //     float weight = king.getWeight();
    //     Image image = king.getImage();

    //     g2d.drawImage(image, (int)x, (int)y, image.getWidth(null)/4, image.getHeight(null)/4, null);


    // }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

//////////////////collision detection/////////////////////
    private void checkCollisions() {
        float kingWidth = king.getWidth();
        float kingHeight = king.getHeight();
        float kingX = king.getX();
        float kingY = king.getY();
        
        for (Platform platform : platforms) {
            if (platform instanceof Vodoravna) {
                checkHorizontalPlatformCollision(platform, kingX, kingY, kingWidth, kingHeight);
            } else if (platform instanceof Navpicna) {
                checkVerticalPlatformCollision(platform, kingX, kingY, kingWidth, kingHeight);
            } else if (platform instanceof slopeDown || platform instanceof slopeUp) {
                checkSlopePlatformCollision(platform, kingX, kingY, kingWidth, kingHeight);
            }
        }
        
        // Check screen boundaries
        checkScreenBoundaries();
    }

    private void checkHorizontalPlatformCollision(Platform platform, float kingX, float kingY, float kingWidth, float kingHeight) {
        float platX = (float) platform.getX();
        float platY = (float) platform.getY();
        float platWidth = 100;
        float platHeight = 3; // Thickness of platform
        
        // Check if king is intersecting with platform
        if (kingX < platX + platWidth && kingX + kingWidth > platX &&
            kingY < platY + platHeight && kingY + kingHeight > platY) {
            
            // Determine collision direction
            float overlapLeft = (kingX + kingWidth) - platX;
            float overlapRight = (platX + platWidth) - kingX;
            float overlapTop = (kingY + kingHeight) - platY;
            float overlapBottom = (platY + platHeight) - kingY;
            
            // Find minimum overlap
            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
            
            if (minOverlap == overlapTop && king.getVelocityY() > 0) {
                // Landing on top of platform
                king.setY(platY - kingHeight);
                king.land();
            } else if (minOverlap == overlapBottom && king.getVelocityY() < 0) {
                // Hitting platform from below
                king.setY(platY + platHeight);
                king.setVelocityY(0);
            } else if (minOverlap == overlapLeft && king.getVelocityX() > 0) {
                // Hitting platform from left
                king.setX(platX - kingWidth);
                king.setVelocityX(0);
            } else if (minOverlap == overlapRight && king.getVelocityX() < 0) {
                // Hitting platform from right
                king.setX(platX + platWidth);
                king.setVelocityX(0);
            }
        }
    }
    
    private void checkVerticalPlatformCollision(Platform platform, float kingX, float kingY, float kingWidth, float kingHeight) {
        float platX = (float) platform.getX();
        float platY = (float) platform.getY();
        float platWidth = 3; // Thickness of vertical platform
        float platHeight = 100;
        
        // Adjust platform Y to start from top
        platY -= platHeight;
        
        if (kingX < platX + platWidth && kingX + kingWidth > platX &&
            kingY < platY + platHeight && kingY + kingHeight > platY) {
            
            float overlapLeft = (kingX + kingWidth) - platX;
            float overlapRight = (platX + platWidth) - kingX;
            
            if (overlapLeft < overlapRight && king.getVelocityX() > 0) {
                king.setX(platX - kingWidth);
                king.setVelocityX(0);
            } else if (king.getVelocityX() < 0) {
                king.setX(platX + platWidth);
                king.setVelocityX(0);
            }
        }
    }
    
    private void checkSlopePlatformCollision(Platform platform, float kingX, float kingY, float kingWidth, float kingHeight) {
        float platX = (float) platform.getX();
        float platY = (float) platform.getY();
        float platWidth = 100;
        
        // Check if king is horizontally within the slope's range
        float kingCenterX = kingX + kingWidth / 2;
        if (kingCenterX < platX || kingCenterX > platX + platWidth * Math.sqrt(2) / 2) {
            return; // King is not over the slope
        }
        
        // Calculate the slope line equation
        float slopeHeight;
        float relativeX = kingCenterX - platX;
        
        if (platform instanceof slopeDown) {
            // For downward slope: y increases as x increases
            slopeHeight = platY + relativeX; // 45-degree slope
        } else if (platform instanceof slopeUp) {
            // For upward slope: y decreases as x increases
            slopeHeight = platY - relativeX; // 45-degree slope going up
        } else {
            return;
        }
        
        // Check if king is touching or below the slope line
        float kingBottom = kingY + kingHeight;
        
        if (kingBottom >= slopeHeight && kingY < slopeHeight + 10) {
            if (king.getVelocityY() >= 0) {
                // King is on the slope (falling down or stationary)
                king.setY(slopeHeight - kingHeight);
                king.land();
                
                // Optional: Add some slope physics
                if (platform instanceof slopeDown) {
                    // Add slight forward momentum on downward slopes
                    king.setVelocityX(king.getVelocityX() + 0.5f);
                } else if (platform instanceof slopeUp) {
                    // Reduce momentum on upward slopes
                    king.setVelocityX(king.getVelocityX() * 0.8f);
                }
            } else if (kingY > slopeHeight - kingHeight) {
                // King hit slope from below (his top is below the slope line) - block him
                king.setY(slopeHeight + 10);
                king.setVelocityY(0);
            }
        }
    }
    
    private void checkScreenBoundaries() {
        // Keep king within screen bounds
        if (king.getX() < 0) {
            king.setX(0);
            king.setVelocityX(0);
        } else if (king.getX() + king.getWidth() > getWidth()) {
            king.setX(getWidth() - king.getWidth());
            king.setVelocityX(0);
        }
        
        // Reset if king falls too far
        if (king.getY() > getHeight() + 100) {
            resetKingPosition();
        }
    }


    private void resetKingPosition() {
        king.setX(190);
        king.setY(510);
        king.setVelocityX(0);
        king.setVelocityY(0);
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

class King {


    private float x;
    private float y;
    private float kot;
    private float hitrostX, hitrostY;
    private float jumpStrength, weight;
    private Image image;
    private boolean onGround;
    private boolean jumping;

    private static final float GRAVITY = 0.8f;
    private static final float GROUND_FRICTION = 0.60f;
    private static final float AIR_RESISTANCE = 0.98f;
    private static final float MOVE_SPEED = 2.0f;
    private static final float MAX_FALL_SPEED = 15.0f;

    public King(float x, float y, float kot, float hitrost, float jumpStrength, float weight) {
        super();
        this.x = x;
        this.y = y;
        this.kot = kot;
        this.hitrostX = hitrost;
        this.hitrostY = 0;
        this.jumpStrength = jumpStrength;
        this.weight = weight;
        this.onGround = false;
        this.jumping = false;

        try {
            this.image = ImageIO.read(new File("elephant.png"));;
        } catch (IOException e) {
            this.image = null;
        }
        }


    public void update() {
        // Apply gravity
        if (!onGround) {
            hitrostY += GRAVITY;
            if (hitrostY > MAX_FALL_SPEED) {
                hitrostY = MAX_FALL_SPEED;
            }
        }
        
        // Apply air resistance/friction
        if (onGround) {
            hitrostX *= GROUND_FRICTION;
        } else {
            hitrostX *= AIR_RESISTANCE;
        }
        
        // Update position
        x += hitrostX;
        y += hitrostY;
        
        // Reset ground flag (will be set by collision detection)
        onGround = false;
    }

    public void move(float deltaX) {
        if (onGround) {
            hitrostX += deltaX * MOVE_SPEED;
        } else {
            hitrostX += deltaX * MOVE_SPEED * 0.3f; // Reduced air control
        }
    }

    public void jump() {
        if (onGround && !jumping) {
            hitrostY = -jumpStrength;
            onGround = false;
            jumping = true;
        }
    }

    public void land() {
        onGround = true;
        jumping = false;
        hitrostY = 0;
    }

    public float getWidth() { return image != null ? image.getWidth(null) / 4 : 20; }
    public float getHeight() { return image != null ? image.getHeight(null) / 4 : 20; }
    public boolean isOnGround() { return onGround; }
    public float getVelocityX() { return hitrostX; }
    public float getVelocityY() { return hitrostY; }
    public void setVelocityX(float vx) { this.hitrostX = vx; }
    public void setVelocityY(float vy) { this.hitrostY = vy; }
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getKot() {
        return kot;
    }
    
    public float getJumpStrength() {
        return jumpStrength;
    }

    public float getWeight() {
        return weight;
    }
    
    // public float getHitrost() {
    //     return hitrost;
    // }

    public Image getImage() {
        return image;
    }
    
    public void setX(float x) {
        this.x = x;
    }
    
    public void setY(float y) {
        this.y = y;
    }
    
    public void setKot(float kot) {
        this.kot = kot;
    }
    
    // public void setHitrost(float hitrost) {
    //     this.hitrost = hitrost;
    // }
    
    public void setWeight(float weight) {
        this.weight = weight;
    }
    
    public void setJumpStrength(float jumpStrength) {
        this.jumpStrength = jumpStrength;
    }
}