import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JumpKingClone extends JPanel implements ActionListener, KeyListener {

    // Frame properties
    private final int WIDTH = 800, HEIGHT = 600;
    private Timer timer;

    // Player properties
    private Rectangle player;
    private int playerVelY = 0;
    private boolean onGround = false;
    private boolean left, right, jumping;
    private final int gravity = 1;
    private final int jumpPower = -20;
    private final int moveSpeed = 5;

    // Simple ground platform
    private Rectangle ground = new Rectangle(0, 550, 800, 50);

    public JumpKingClone() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        player = new Rectangle(400, 400, 30, 50);
        timer = new Timer(16, this); // ~60fps
        timer.start();
        addKeyListener(this);
        setFocusable(true);
    }

    // Game loop
    public void actionPerformed(ActionEvent e) {
        updatePlayer();
        repaint();
    }

    // Player logic
    private void updatePlayer() {
        // Horizontal movement
        if (left) player.x -= moveSpeed;
        if (right) player.x += moveSpeed;

        // Gravity
        playerVelY += gravity;
        player.y += playerVelY;

        // Ground collision
        if (player.intersects(ground)) {
            player.y = ground.y - player.height;
            playerVelY = 0;
            onGround = true;
        } else {
            onGround = false;
        }

        // Jump
        if (jumping && onGround) {
            playerVelY = jumpPower;
            onGround = false;
        }
    }

    // Drawing
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.fillRect(ground.x, ground.y, ground.width, ground.height);

        g.setColor(Color.RED);
        g.fillRect(player.x, player.y, player.width, player.height);
    }

    // Key controls
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT) left = true;
        if (k == KeyEvent.VK_RIGHT) right = true;
        if (k == KeyEvent.VK_SPACE) jumping = true;
    }

    public void keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT) left = false;
        if (k == KeyEvent.VK_RIGHT) right = false;
        if (k == KeyEvent.VK_SPACE) jumping = false;
    }

    public void keyTyped(KeyEvent e) {}

    // Main method to run the game
    public static void main(String[] args) {
        JFrame frame = new JFrame("Jump King Clone");
        JumpKingClone game = new JumpKingClone();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
