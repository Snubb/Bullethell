import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

public class WEEEE extends Canvas implements Runnable {

    private int coolDown = 0;

    private int width = 400;
    private int height = 600;

    private int numShots = 0;

    private boolean isShooting = false;

    private boolean isFiring = false;

    private int planeX, planeY, planeVX;

    private int playerX, playerY;
    private int playerVX, playerVY;
    private int focus;

    public ArrayList<laser> pewpew = new ArrayList<laser>();

    private boolean isGoingLeft, isGoingRight, isGoingUp, isGoingDown;

    private Thread thread;

    int fps = 60;

    private  boolean isRunning;

    private BufferStrategy bs;
    //private BufferedImage img;

    public WEEEE() {

        JFrame frame = new JFrame("Title goes here");
        this.setSize(width,height);
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

        planeX = 0;
        planeY = 50;
        planeVX = 2;

    }

    public void update() {

        if (isFiring) {
            if (coolDown == 3) {
                numShots += 2;
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

        playerX += (playerVX / focus);
        playerY += (playerVY / focus);

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

        planeX += planeVX;
        if (planeX > width - 15) {
            planeVX = -2;
        } else if(planeX < 0) {
            planeVX = 2;
        }

    }

    public void draw() {
        bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        update();
        g.setColor(Color.white);
        g.fillRect(0,0,width,height);

        drawPlayer(g, playerX, playerY);

        drawPlane(g, planeX, planeY);

        g.setColor(Color.BLUE);

        shootingMahLaser(g);
        System.out.println(coolDown);

        g.dispose();
        bs.show();
    }

    private void shootingMahLaser(Graphics g) {
        /*if (isFiring) {
            System.out.println(playerX);
            pewpew.add(new laser(new Rectangle (playerX+2, playerY, 5, 20)));
        }*/
        /*if (isShooting) {
            g.setColor(Color.black);
            g.fillRect(pewpew.get(0).getPosX(), pewpew.get(0).getPosY(),5,10);
            pewpew.get(0).shoot();
        }*/
        if (isShooting) {
            for (int i = 0; i < numShots; i += 2) {
                g.setColor(Color.GREEN);
                g.fillRect(pewpew.get(i).getPosX(), pewpew.get(i).getPosY(), 5, 10);
                pewpew.get(i).shoot();
                /*g.fillRect(pewpew.get(i+1).getPosX(), pewpew.get(i+1).getPosY(), 5, 10);
                pewpew.get(i+1).shoot();*/
            }
            for (int i = 1; i < numShots; i += 2) {
                g.setColor(Color.BLUE);
                g.fillRect(pewpew.get(i).getPosX(), pewpew.get(i).getPosY(), 5, 10);
                pewpew.get(i).shoot();
                /*g.fillRect(pewpew.get(i+1).getPosX(), pewpew.get(i+1).getPosY(), 5, 10);
                pewpew.get(i+1).shoot();*/
            }
        }

    }

    public void drawPlane(Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.fillRect(x,y,15,5);
        g.fillRect(x+5,y,5,10);
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

                }
                lastTime = now;
            }
        }
        stop();

    }

    private class KL implements KeyListener {
        @Override
        public void keyTyped(KeyEvent keyEvent) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
                focus = 3;
                //System.out.println("WEee");
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
                System.out.println("pew");

                /*numShots++;
                pewpew.add(new laser(new Rectangle (playerX+2, playerY, 5, 20)));*/

                isShooting = true;
                isFiring = true;
            }

        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
                focus = 1;
                //System.out.println("WOoo");
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

    private class ML implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class MML implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }
}
