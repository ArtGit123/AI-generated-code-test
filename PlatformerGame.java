package platformer_additions_extra;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PlatformerGame extends JFrame implements ActionListener {
    private Timer timer;
    private int playerX, playerY;
    private int playerSpeedX, playerSpeedY;
    private boolean isJumping;
    private boolean isMovingLeft;
    private boolean isMovingRight;
    private boolean isJumpKeyPressed;
    private boolean isOnPlatform; // New variable
    private Image buffer;
    private Graphics bufferGraphics;
    private List<Rectangle> platforms;

    public PlatformerGame() {
        setTitle("Platformer Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        playerX = 100;
        playerY = 400;
        playerSpeedX = 0;
        playerSpeedY = 0;
        isJumping = false;
        isMovingLeft = false;
        isMovingRight = false;
        isJumpKeyPressed = false;
        isOnPlatform = false; // Initialize to false

        timer = new Timer(10, this);
        timer.start();

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_LEFT) {
                    isMovingLeft = true;
                    if (!isJumping) {
                        playerSpeedX = -5;
                    }
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    isMovingRight = true;
                    if (!isJumping) {
                        playerSpeedX = 5;
                    }
                } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_SPACE) {
                    isJumpKeyPressed = true;
                }
            }

            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_LEFT) {
                    isMovingLeft = false;
                    if (!isMovingRight && !isJumping) {
                        playerSpeedX = 0;
                    } else if (isMovingRight && !isJumping) {
                        playerSpeedX = 5;
                    }
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    isMovingRight = false;
                    if (!isMovingLeft && !isJumping) {
                        playerSpeedX = 0;
                    } else if (isMovingLeft && !isJumping) {
                        playerSpeedX = -5;
                    }
                } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_SPACE) {
                    isJumpKeyPressed = false;
                }
            }
        });

        setFocusable(true);
        requestFocusInWindow();

        // Initialize platforms
        platforms = new ArrayList<>();
        platforms.add(new Rectangle(0, 450, 800, 50));
        platforms.add(new Rectangle(100, 300, 200, 20));
        platforms.add(new Rectangle(480, 120, 220, 20));
    }

    @Override
    public void addNotify() {
        super.addNotify();
        init();
    }

    public void init() {
        // Initialize double buffering
        buffer = createImage(getWidth(), getHeight());
        bufferGraphics = buffer.getGraphics();
    }

    public void actionPerformed(ActionEvent e) {
        updatePlayer();
        repaint();
    }

    private void updatePlayer() {
        if (isMovingLeft) {
            playerSpeedX = -5;
        } else if (isMovingRight) {
            playerSpeedX = 5;
        } else {
            playerSpeedX = 0;
        }

        playerX += playerSpeedX;

        if (isJumpKeyPressed && !isJumping) {
            startJump();
        }

        if (isJumping) {
            playerY += playerSpeedY;
            playerSpeedY += 1;

            if (playerY >= 400) {
                playerY = 400;
                playerSpeedY = 0;
                isJumping = false;
                isOnPlatform = true; // Player has landed on a platform
            }
        } else if (!isOnPlatform) {
            // Update y-coordinate when not jumping and not on a platform
            playerY += playerSpeedY;
            playerSpeedY += 1;
        }

     // Check for collisions with platforms
        isOnPlatform = false; // Reset isOnPlatform flag
        for (Rectangle platform : platforms) {
            int playerTop = playerY; // Top edge of the player
            int playerBottom = playerY + 50; // Bottom edge of the player
            int platformTop = platform.y; // Top edge of the platform
            int platformBottom = platform.y + platform.height; // Bottom edge of the platform

            if (playerX + 50 >= platform.x && playerX <= platform.x + platform.width) {
                if (playerBottom >= platformTop && playerTop <= platformBottom) {
                    if (playerBottom >= platformBottom) {
                        playerY = platformBottom;
                        playerSpeedY = 0;
                        isJumping = false;
                        isOnPlatform = true; // Player is on a platform
                    } else if (playerSpeedY > 0 && playerTop <= platformTop) {
                        playerY = platformTop - 50;
                        playerSpeedY = 0;
                        isJumping = false;
                        isOnPlatform = true; // Player is on a platform
                    }
                }
            }
        }

        // Prevent the player from going beyond the screen edges
        if (playerX < 0) {
            playerX = 0;
        } else if (playerX > getWidth() - 50) {
            playerX = getWidth() - 50;
        }
    }

    private void startJump() {
        playerSpeedY = -17;
        isJumping = true;
        isOnPlatform = false; // Player is not on a platform when jumping
    }

    public void paint(Graphics g) {
        // Perform rendering offscreen
        bufferGraphics.clearRect(0, 0, getWidth(), getHeight());
        bufferGraphics.setColor(Color.RED);
        bufferGraphics.fillRect(playerX, playerY, 50, 50);
        bufferGraphics.setColor(Color.BLUE);
        for (Rectangle platform : platforms) {
            bufferGraphics.fillRect(platform.x, platform.y, platform.width, platform.height);
        }

        // Draw the offscreen buffer to the screen
        g.drawImage(buffer, 0, 0, this);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PlatformerGame game = new PlatformerGame();
            game.setVisible(true);
        });
    }
}

