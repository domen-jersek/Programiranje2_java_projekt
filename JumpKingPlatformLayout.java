import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JumpKingPlatformLayout {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Infinite Height Platformer");
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
            panel.update();
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
    private boolean[] keys = new boolean[256];
    private float cameraX = 0; // Camera offset for centering horizontally
    private float cameraY = 0; // Camera offset for following the player
    private int currentHeight = 0; // Track how high we've generated
    private Random random = new Random();
    private int maxHeightAchieved = 0;
    
    // Configuration for infinite generation
    private static final int LEVEL_HEIGHT = 100; // Vertical spacing between levels
    private static final int SCREEN_HEIGHT = 600;
    private static final int GENERATION_BUFFER = 1000; // Generate platforms this far ahead
    private static final int MAX_PLATFORMS_PER_LEVEL = 4;
    private static final int MIN_PLATFORMS_PER_LEVEL = 2;
    
    
    public GamePanel() {
        super();
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();
        initializePlatforms();
        king = new King(350, 510, 0, 0, 15, 0.5f);
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
        
        // Update camera to follow player
        updateCamera();
        
        // Generate more platforms if needed
        generateMorePlatforms();
    }
    
    private void updateCamera() {
        // Follow the player vertically with some smoothing
        float targetCameraY = king.getY() - getHeight() * 0.7f; // Keep player in lower 70% of screen
        cameraY += (targetCameraY - cameraY) * 0.1f; // Smooth camera movement  

        float gameWidth = 700; // The playable area width (100 to 600 + margins)
        float targetCameraX = (getWidth() - gameWidth) / 2.0f;
        cameraX += (targetCameraX - cameraX) * 0.1f; // Smooth horizontal centering
    }
    
    private void generateMorePlatforms() {
        // Check if we need to generate more platforms above current height
        if (king.getY() < currentHeight + GENERATION_BUFFER) {
            generatePlatformsToHeight(currentHeight - 2000); // Generate 2000 pixels higher
        }
    }
    
    private void initializePlatforms() {
        platforms = new ArrayList<>();
        
        // Create your custom starting levels
        createCustomStartingLevels();
        
        // Set currentHeight to start generating above your custom levels
        currentHeight = 50; // Start generating above your highest platform
        
        // Generate initial tower of platforms above the custom ones
        generatePlatformsToHeight(-3000); // Generate 3000 pixels up initially
    }
    
    private void createCustomStartingLevels() {
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

        // Top level platform (goal marker, but not the end!)
        platforms.add(new Vodoravna(350, 60, 0, 1.0));
    }
    
    private void generatePlatformsToHeight(int targetHeight) {
        int levelY = currentHeight;
        
        while (levelY > targetHeight) {
            levelY -= LEVEL_HEIGHT;
            generateLevelAt(levelY);
        }
        
        currentHeight = targetHeight;
    }
    
    private void generateLevelAt(int y) {
        // Determine how many platforms this level should have
        int numPlatforms = MIN_PLATFORMS_PER_LEVEL + random.nextInt(MAX_PLATFORMS_PER_LEVEL - MIN_PLATFORMS_PER_LEVEL + 1);
        
        // Create a list of possible X positions
        List<Integer> xPositions = new ArrayList<>();
        for (int x = 100; x <= 500; x += 100) {
            xPositions.add(x);
        }
        
        // Randomly select positions for platforms
        for (int i = 0; i < numPlatforms && !xPositions.isEmpty(); i++) {
            int index = random.nextInt(xPositions.size());
            int x = xPositions.remove(index);
            
            // Choose platform type based on probability
            Platform platform = createRandomPlatform(x, y);
            platforms.add(platform);

            // Add vertical barrier
            platforms.add(new Navpicna(100, y, 0, 1.0));
            platforms.add(new Navpicna(600, y, 0, 1.0));
        }
        
        // Occasionally add extra challenge elements
        // if (random.nextFloat() < 0.3f) { // 30% chance
        //     addChallengeElement(y);
        // }
    }
    
    private Platform createRandomPlatform(int x, int y) {
        float rand = random.nextFloat();
        
        if (rand < 0.5f) {
            return new Vodoravna(x, y, 0, 1.0); // 50% horizontal
        } else if (rand < 0.75f) {
            return new slopeUp(x, y, 0, 1.0); // 25% slope up
        } else {
            return new slopeDown(x, y, 0, 1.0); // 25% slope down
        }
    }
    
    private void addChallengeElement(int y) {
        // Add challenging combinations
        if (random.nextFloat() < 0.5f) {
            // Create a gap that requires a long jump
            int startX = 150 + random.nextInt(200);
            platforms.add(new Vodoravna(startX, y, 0, 1.0));
            platforms.add(new Vodoravna(startX + 250, y, 0, 1.0)); // Wide gap
        } else {
            // Create a wall maze
            int x = 200 + random.nextInt(300);
            platforms.add(new Navpicna(x, y + 30, 0, 1.0));
            platforms.add(new Navpicna(x + 100, y - 30, 0, 1.0));
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Apply camera transform
        g2d.translate(cameraX, -cameraY);
        
        // Set rendering quality
        g2d.setStroke(new BasicStroke(3));
        
        // Draw all platforms (only those visible on screen for performance)
        for (Platform platform : platforms) {
            float platY = (float) platform.getY();
            // Only draw platforms that might be visible
            if (platY > cameraY - 100 && platY < cameraY + SCREEN_HEIGHT + 100) {
                drawPlatform(g2d, platform);
            }
        }
        
        // Draw the King
        drawKing(g2d);
        
        // Reset transform for UI elements
        g2d.translate(-cameraX, cameraY);
        
        // Draw UI elements (height counter, etc.)
        
        drawUI(g2d);
    }
    
    private void drawUI(Graphics2D g2d) {
        // Draw current height achieved
        int heightAchieved = Math.max(0, (int)(550 - king.getY()) / 10); // Convert to "floors"
        
        if (heightAchieved > maxHeightAchieved)
            maxHeightAchieved = heightAchieved;

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Curent best: " + maxHeightAchieved + "m", 10, 30);

        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Current height: " + heightAchieved + "m", 10, 60);
        
        // Draw controls
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Controls: A/D - Move, W/Space - Jump", 10, getHeight() - 40);
        g2d.drawString("Goal: Reach as high as possible!", 10, getHeight() - 20);
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

    // Collision detection methods remain the same as original
    private void checkCollisions() {
        float kingWidth = king.getWidth();
        float kingHeight = king.getHeight();
        float kingX = king.getX();
        float kingY = king.getY();
        
        for (Platform platform : platforms) {
            // Only check collision with platforms near the player for performance
            float platY = (float) platform.getY();
            if (Math.abs(platY - kingY) > 200) continue; // Skip distant platforms
            
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
        float platHeight = 3;
        
        if (kingX < platX + platWidth && kingX + kingWidth > platX &&
            kingY < platY + platHeight && kingY + kingHeight > platY) {
            
            float overlapLeft = (kingX + kingWidth) - platX;
            float overlapRight = (platX + platWidth) - kingX;
            float overlapTop = (kingY + kingHeight) - platY;
            float overlapBottom = (platY + platHeight) - kingY;
            
            float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));
            
            if (minOverlap == overlapTop && king.getVelocityY() > 0) {
                king.setY(platY - kingHeight);
                king.land();
            } else if (minOverlap == overlapBottom && king.getVelocityY() < 0) {
                king.setY(platY + platHeight);
                king.setVelocityY(0);
            } else if (minOverlap == overlapLeft && king.getVelocityX() > 0) {
                king.setX(platX - kingWidth);
                king.setVelocityX(0);
            } else if (minOverlap == overlapRight && king.getVelocityX() < 0) {
                king.setX(platX + platWidth);
                king.setVelocityX(0);
            }
        }
    }
    
    private void checkVerticalPlatformCollision(Platform platform, float kingX, float kingY, float kingWidth, float kingHeight) {
        float platX = (float) platform.getX();
        float platY = (float) platform.getY();
        float platWidth = 3;
        float platHeight = 100;
        
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
        float slopeLength = (float)(100 * Math.sqrt(2) / 2); // Actual slope length
        
        float kingCenterX = kingX + kingWidth / 2;
        
        // Check if king is within the horizontal bounds of the slope
        if (kingCenterX < platX || kingCenterX > platX + slopeLength) {
            return;
        }
        
        float slopeHeight;
        float relativeX = kingCenterX - platX;
        
        if (platform instanceof slopeDown) {
            // For downward slope: height increases as we move right
            slopeHeight = platY + (relativeX * slopeLength / slopeLength); // 1:1 ratio for 45° slope
        } else if (platform instanceof slopeUp) {
            // For upward slope: height decreases as we move right
            slopeHeight = platY - (relativeX * slopeLength / slopeLength); // 1:1 ratio for 45° slope
        } else {
            return;
        }
        
        float kingBottom = kingY + kingHeight;
        float tolerance = 15; // Collision tolerance
        
        // Check if king is close enough to the slope surface
        if (kingBottom >= slopeHeight - tolerance && kingBottom <= slopeHeight + tolerance) {
            if (king.getVelocityY() >= -2) { // Allow landing if not moving up too fast
                king.setY(slopeHeight - kingHeight);
                king.land();
                
                // Apply slope physics
                if (platform instanceof slopeDown) {
                    king.setVelocityX(king.getVelocityX() + 1.0f); // Speed boost downhill
                } else if (platform instanceof slopeUp) {
                    king.setVelocityX(king.getVelocityX() * 0.7f); // Slow down uphill
                }
            }
        }
        // Handle ceiling collision (hitting slope from below)
        else if (kingY <= slopeHeight + tolerance && kingY >= slopeHeight - tolerance && king.getVelocityY() < 0) {
            king.setY(slopeHeight + tolerance);
            king.setVelocityY(0);
        }
    }
    
    private void checkScreenBoundaries() {
        if (king.getX() < 0) {
            king.setX(0);
            king.setVelocityX(0);
        } else if (king.getX() + king.getWidth() > getWidth()) {
            king.setX(getWidth() - king.getWidth());
            king.setVelocityX(0);
        }
        
        // Reset if king falls too far below starting position
        if (king.getY() > 700) {
            resetKingPosition();
        }
    }

    private void resetKingPosition() {
        king.setX(350);
        king.setY(510);
        king.setVelocityX(0);
        king.setVelocityY(0);
        // Reset camera too
        cameraY = 0;
        cameraX = 0;
    }
}

// Platform classes remain the same
class Platform {
    private double x, y;
    private float kot;
    private double trdota;
    
    public Platform(double x, double y, float kot, double trdota) {
        this.x = x; this.y = y; this.kot = kot; this.trdota = trdota;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public float getKot() { return kot; }
    public double getTrdota() { return trdota; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setKot(float kot) { this.kot = kot; }
    public void setTrdota(double trdota) { this.trdota = trdota; }
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

// King class remains the same
class King {
    private float x, y, kot, hitrostX, hitrostY, jumpStrength, weight;
    private Image image;
    private boolean onGround, jumping;
    private static final float GRAVITY = 0.8f;
    private static final float GROUND_FRICTION = 0.60f;
    private static final float AIR_RESISTANCE = 0.98f;
    private static final float MOVE_SPEED = 2.0f;
    private static final float MAX_FALL_SPEED = 15.0f;

    public King(float x, float y, float kot, float hitrost, float jumpStrength, float weight) {
        this.x = x; this.y = y; this.kot = kot; this.hitrostX = hitrost; this.hitrostY = 0;
        this.jumpStrength = jumpStrength; this.weight = weight; this.onGround = false; this.jumping = false;
        try { this.image = ImageIO.read(new File("elephant.png")); } 
        catch (IOException e) { this.image = null; }
    }

    public void update() {
        if (!onGround) {
            hitrostY += GRAVITY;
            if (hitrostY > MAX_FALL_SPEED) hitrostY = MAX_FALL_SPEED;
        }
        hitrostX *= onGround ? GROUND_FRICTION : AIR_RESISTANCE;
        x += hitrostX; y += hitrostY; onGround = false;
    }

    public void move(float deltaX) {
        hitrostX += deltaX * MOVE_SPEED * (onGround ? 1.0f : 0.3f);
    }

    public void jump() {
        if (onGround && !jumping) {
            hitrostY = -jumpStrength; onGround = false; jumping = true;
        }
    }

    public void land() { onGround = true; jumping = false; hitrostY = 0; }

    // Getters and setters
    public float getWidth() { return image != null ? image.getWidth(null) / 4 : 20; }
    public float getHeight() { return image != null ? image.getHeight(null) / 4 : 20; }
    public boolean isOnGround() { return onGround; }
    public float getVelocityX() { return hitrostX; }
    public float getVelocityY() { return hitrostY; }
    public void setVelocityX(float vx) { this.hitrostX = vx; }
    public void setVelocityY(float vy) { this.hitrostY = vy; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getKot() { return kot; }
    public float getJumpStrength() { return jumpStrength; }
    public float getWeight() { return weight; }
    public Image getImage() { return image; }
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setKot(float kot) { this.kot = kot; }
    public void setWeight(float weight) { this.weight = weight; }
    public void setJumpStrength(float jumpStrength) { this.jumpStrength = jumpStrength; }
}