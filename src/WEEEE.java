import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class WEEEE extends Canvas implements Runnable {

    private final Rectangle enemy;
    private int numHits = 0;

    private int powerUps = 3;
    private int powerUpTimer = 0;
    private int powerUpRefresh = 0;

    private int coolDownAlt = 3;

    private int coolDown = 0; //Functions as a cooldown between shots.
    private int clearTimer = 0;

    private final int width = 400; //Dimensions for window
    private final int height = 600;

    private int numShots = 0; //Keeps track of the total number of shots fired

    private boolean isFiring = false; //True if you are shooting.

    private int enemyVX; //Positioning and speed of enemy

    private int playerX, playerY; //Positioning and speed of player
    private int playerVX, playerVY;
    private int focus; //Allows you to focus by pressing shift

    public ArrayList<laser> pewpew = new ArrayList<>(); //Array to keep spawning bullets

    //OBS: Det här gjordes innan du nämnde saken om update() inuti draw(), kan vara så att det inte behövs men vågar inte ändra på det för mycket.
    private boolean isGoingLeft, isGoingRight, isGoingUp, isGoingDown; //Necessary to make sure the player doesn't freeze up when changing directions

    private Thread thread;

    int fps = 120;

    private  boolean isRunning;

    private BufferStrategy bs;
    //private BufferedImage img;

    public WEEEE() {

        JFrame frame = new JFrame("Not touhou I swear");
        this.setSize(width+200,height);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KL());
        frame.setVisible(true);

        isRunning = false;

        playerX = 175;
        playerY = 400;
        playerVX = 0;
        playerVY = 0;
        focus = 1;
        isGoingRight = false;
        isGoingLeft = false;
        isGoingDown = false;
        isGoingUp = false;

        enemyVX = 2;

        enemy = new Rectangle(0,50,50,20);

    }

    public void update() { //Various updates that happen every frame

        powerUpRefresh++;
        if (powerUpRefresh == 15000) {
            powerUps++;
            powerUpRefresh = 0;
        }

        if (coolDown > 10) {
            coolDown = 0;
        }

        if (powerUpTimer > 0) {
            coolDownAlt = 1;
        } else coolDownAlt = 3;

        if (powerUpTimer > -1) {
            powerUpTimer--;
        }

        for (int i = 0;i < numShots; i++) {
            if (pewpew.get(i).collide(enemy)) {
                numHits++;
                pewpew.remove(i);
                numShots--;
            }
        }

        if (clearTimer == 60) {
            clearNumShots();
        }

        //Handles spawning the bullets
        if (isFiring) {
            spawnLaser();
        }

        //Handles movement
        playerX += (playerVX / focus);
        playerY += (playerVY / focus);

        //Keeps the player inside the playing-area
        positionCheck();
    }

    public void draw() { //Draws various things every frame
        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(new Color(213, 213, 213));
        g.fillRect(width,0,200,height);

        g.setColor(Color.white);
        g.fillRect(0,0,width,height);

        shootingMahLaser(g);

        drawPlayer(g, playerX, playerY);

        if (numHits < 1000) {
            g.setColor(Color.BLACK);
            g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
        }

        g.setFont(new Font("Serif", Font.BOLD, 24));
        g.drawString("Boosts(e): " + powerUps, 400, 50);
        g.drawString("Refresh: " + powerUpRefresh, 400, 100);

        g.dispose();
        bs.show();
    }

    private void positionCheck() {
        if (playerX > width - 10) {
            playerX = width - 10;
        }
        if (playerX < 0) {
            playerX = 0;
        }
        if (playerY < 0) {
            playerY = 0;
        }
        if (playerY > height - 15) {
            playerY = height - 15;
        }

        //Moves the enemy
        enemy.x += enemyVX;
        if (enemy.x > width - 50) {
            enemyVX = -2;
        } else if(enemy.x < 0) {
            enemyVX = 2;
        }
    }

    private void spawnLaser() {
        if (coolDown == coolDownAlt) {
            numShots += 2;
            //Spawns in different positions depending on if you are focusing or not
            if (focus == 1) {
                pewpew.add(new laser(new Rectangle (playerX-6, playerY, 5, 20)));
                pewpew.add(new laser(new Rectangle (playerX+11, playerY, 5, 20)));
            } else {
                pewpew.add(new laser(new Rectangle (playerX, playerY-5, 5, 20)));
                pewpew.add(new laser(new Rectangle (playerX+5, playerY-5, 5, 20)));
            }
            coolDown = 0;
        }
    }

    private void clearNumShots() { //Clears the total number of shots if you pause for a second
        pewpew.clear();
        numShots = 0;
    }

    private void shootingMahLaser(Graphics g) { //Moves the lasers forward by changing y-cordinate, does not spawn laser but does draw it
        for (int i = 0; i < numShots; i++) {
            g.setColor(Color.GREEN);
            g.fillRect(pewpew.get(i).getPosX(), pewpew.get(i).getPosY(), 5, 10);
            pewpew.get(i).shoot();
        }
    }

    private void drawPlayer(Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.fillRect(x,y,10,15);
    }

    public static void main(String[] args) {
        WEEEE painting = new WEEEE();
        painting.start();
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double deltaT = 1000.0/fps;
        long lastTime = System.currentTimeMillis();

        while (isRunning) {
            long now = System.currentTimeMillis();
            if (now-lastTime > deltaT) {
                update();
                draw();
                if (isFiring) {
                    coolDown++;
                    clearTimer = 0;
                }
                if (!isFiring) {
                    clearTimer++;
                }
                lastTime = now;
            }
        }
        stop();
    }

    private class KL implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'e' || keyEvent.getKeyChar() == 'E') {
                if (powerUps > 0) {
                    powerUps--;
                    powerUpTimer = 300;
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
                focus = 3;
            }
            if (keyEvent.getKeyChar() == 'w' || keyEvent.getKeyChar() == 'W') {
                playerVY = -3;

                isGoingUp = true;
            }
            if (keyEvent.getKeyChar() == 's' || keyEvent.getKeyChar() == 'S') {
                playerVY = 3;

                isGoingDown = true;
            }
            if (keyEvent.getKeyChar() == 'a' || keyEvent.getKeyChar() == 'A') {
                playerVX = -3;

                isGoingLeft = true;
            }
            if (keyEvent.getKeyChar() == 'd' || keyEvent.getKeyChar() == 'D') {
                playerVX = 3;

                isGoingRight = true;
            }
            if (keyEvent.getKeyChar() == 'z' || keyEvent.getKeyChar() == 'Z' || keyEvent.getKeyChar() == ' ') {
                isFiring = true;
            }

        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
                focus = 1;
            }
            if (keyEvent.getKeyChar() == 'w' || keyEvent.getKeyChar() == 'W') {
                if (!isGoingDown) {
                    playerVY = 0;
                }

                isGoingUp = false;
            }
            if (keyEvent.getKeyChar() == 's' || keyEvent.getKeyChar() == 'S') {
                if (!isGoingUp) {
                    playerVY = 0;
                }

                isGoingDown = false;
            }
            if (keyEvent.getKeyChar() == 'a' || keyEvent.getKeyChar() == 'A') {
                if (!isGoingRight) {
                    playerVX = 0;
                }

                isGoingLeft = false;
            }
            if (keyEvent.getKeyChar() == 'd' || keyEvent.getKeyChar() == 'D') {
                if (!isGoingLeft) {
                    playerVX = 0;
                }

                isGoingRight = false;
            }
            if (keyEvent.getKeyChar() == 'z' || keyEvent.getKeyChar() == 'Z' || keyEvent.getKeyChar() == ' ') {
                isFiring = false;
            }

        }
    }
}
