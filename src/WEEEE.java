import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

public class WEEEE extends Canvas implements Runnable {

    private BufferedImage luigi;

    private final Rectangle enemy;
    private int enemyHP = 10000;
    private int fasterShooting = 1;
    private boolean growingBullets = false;
    private int enemyVX; //Positioning and speed of enemy

    public ArrayList<enemyLaser> pewpewButDontTouch = new ArrayList<>(); //Enemys bullets
    private int enemyBulletCooldown = 0; //Cooldown between each bullet
    private int numBullets = 0;

    private int powerUps = 3;
    private int powerUpTimer = 0;
    private int powerUpRefresh = 0;

    private int coolDownAlt = 3;

    private int coolDown = 0; //Functions as a cooldown between shots.
    private int clearTimer = 0;

    private final int width = 400; //Dimensions for playing area
    private final int height = 600;

    private int numShots = 0; //Keeps track of the total number of shots fired

    private boolean isFiring = false; //True if you are shooting.


    private final Rectangle player = new Rectangle();
    private int lives = 3;

    //private int playerX, playerY; //Positioning and speed of player
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

        try {
            luigi = ImageIO.read(new File("Lugi.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Not touhou I swear");
        this.setSize(width+200,height);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KL());
        frame.setVisible(true);

        isRunning = false;

        player.x = 175;
        player.y = 400;
        player.width = 10;
        player.height = 15;

        /*playerX = 175;
        playerY = 400;*/
        playerVX = 0;
        playerVY = 0;
        focus = 1;
        isGoingRight = false;
        isGoingLeft = false;
        isGoingDown = false;
        isGoingUp = false;

        enemyVX = 2;

        enemy = new Rectangle(0,50,50,70);

    }

    public void update() { //Various updates that happen every frame

        if (enemyHP < 6000) {
            fasterShooting = 2;
            growingBullets = true;
        }else if (enemyHP < 8000) {
            fasterShooting = 2;
        }

        enemyBulletCooldown++;
        if (enemyBulletCooldown > 60/fasterShooting) {
            spawnBullet();
            enemyBulletCooldown = 0;
        }

        powerUpRefresh++;
        if (powerUpRefresh == 5000) {
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
                enemyHP -= 10;
                pewpew.remove(i);
                numShots--;
            }
        }

        for (int i = 0;i < numBullets; i++) {
            if (pewpewButDontTouch.get(i).collide(player)) {
                lives--;
                pewpewButDontTouch.remove(i);
                numBullets--;
                System.out.println("HIT");
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
        /*playerX += (playerVX / focus);
        playerY += (playerVY / focus);*/

        player.x += (playerVX / focus);
        player.y += (playerVY / focus);

        //Keeps the player inside the playing-area
        positionCheck();
    }

    private void spawnBullet() {
        System.out.println("YEP");
        pewpewButDontTouch.add(new enemyLaser(new Rectangle(enemy.x+25, enemy.y+50, 5, 5)));
        numBullets++;
    }

    private void moveBullets(Graphics g) {
        for (int i = 0; i < numBullets; i++) {
            g.setColor(Color.RED);
            g.fillRect(pewpewButDontTouch.get(i).getPosX(), pewpewButDontTouch.get(i).getPosY(), pewpewButDontTouch.get(i).getWidth(), 10);
            pewpewButDontTouch.get(i).shoot();
            if (growingBullets && enemyBulletCooldown == 60/fasterShooting) {
                pewpewButDontTouch.get(i).grow();
            }
        }
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
        moveBullets(g);

        drawPlayer(g, player.x, player.y);

        if (enemyHP > 0) {
            //g.setColor(Color.BLACK);
            //g.fillRect(enemy.x, enemy.y, enemy.width, enemy.height);
            g.drawImage(luigi, enemy.x, enemy.y,enemy.width, enemy.height, null);
        } else {

        }

        g.setFont(new Font("Serif", Font.BOLD, 24));
        g.drawString("Boosts(e): " + powerUps, 410, 50);
        g.drawString("Refresh: " + powerUpRefresh, 410, 100);
        g.drawString("Enemy HP: " + enemyHP, 410, 150);
        g.drawString("Lives: " + lives, 410, 200);
        g.drawString("numBullets: " + numBullets, 410, 250);
        g.drawString("eBC: " + enemyBulletCooldown, 410, 300);
        g.drawString("fasterShooting: " + fasterShooting, 410, 350);

        g.dispose();
        bs.show();
    }

    private void positionCheck() {
        if (player.x > width - 10) {
            player.x = width - 10;
        }
        if (player.x < 0) {
            player.x = 0;
        }
        if (player.y < 0) {
            player.y = 0;
        }
        if (player.y > height - 15) {
            player.y = height - 15;
        }

        /*if (playerX > width - 10) {
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
        }*/

        //Moves the enemy
        if (enemyHP > 0) {
            enemy.x += enemyVX;
            if (enemy.x > width - 50) {
                enemyVX = -2;
            } else if(enemy.x < 0) {
                enemyVX = 2;
            }
        } else {
            enemy.x = -100;
            enemy.y = -100;
        }
    }

    private void spawnLaser() {
        if (coolDown == coolDownAlt) {
            numShots += 2;
            //Spawns in different positions depending on if you are focusing or not
            /*if (focus == 1) {
                pewpew.add(new laser(new Rectangle (playerX-6, playerY, 5, 20)));
                pewpew.add(new laser(new Rectangle (playerX+11, playerY, 5, 20)));
            } else {
                pewpew.add(new laser(new Rectangle (playerX, playerY-5, 5, 20)));
                pewpew.add(new laser(new Rectangle (playerX+5, playerY-5, 5, 20)));
            }*/
            if (focus == 1) {
                pewpew.add(new laser(new Rectangle (player.x-6, player.y, 5, 20)));
                pewpew.add(new laser(new Rectangle (player.x+11, player.y, 5, 20)));
            } else {
                pewpew.add(new laser(new Rectangle (player.x, player.y-5, 5, 20)));
                pewpew.add(new laser(new Rectangle (player.x+5, player.y-5, 5, 20)));
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
        //g.fillRect(x,y,10,15);

        g.fillRect(player.x, player.y, 10, 15);
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
