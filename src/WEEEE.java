import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;

public class WEEEE extends Canvas implements Runnable {

    private int width = 400;
    private int height = 600;

    private int playerX, playerY;
    private int playerVX, playerVY;
    private int speed;

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
        speed = 1;
    }

    public void update() {
        playerX += (playerVX / speed);
        playerY += (playerVY / speed);

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
        System.out.println(playerVY);

        g.dispose();
        bs.show();
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
                speed = 2;
                //System.out.println("WEee");
            }
            if (keyEvent.getKeyChar() == 'w' || keyEvent.getKeyChar() == 'W') {

                playerVY = -4;
            }
            if (keyEvent.getKeyChar() == 's' || keyEvent.getKeyChar() == 'S') {
                playerVY = 4;

            }
            if (keyEvent.getKeyChar() == 'a' || keyEvent.getKeyChar() == 'A') {
                playerVX = -4;

            }
            if (keyEvent.getKeyChar() == 'd' || keyEvent.getKeyChar() == 'D') {
                playerVX = 4;
            }


        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == KeyEvent.VK_SHIFT) {
                speed = 1;
                //System.out.println("WOoo");
            }
            if (keyEvent.getKeyChar() == 'w' || keyEvent.getKeyChar() == 'W') {
                playerVY = 0;
            }
            if (keyEvent.getKeyChar() == 's' || keyEvent.getKeyChar() == 'S') {
                playerVY = 0;
            }
            if (keyEvent.getKeyChar() == 'a' || keyEvent.getKeyChar() == 'A') {
                playerVX = 0;
            }
            if (keyEvent.getKeyChar() == 'd' || keyEvent.getKeyChar() == 'D') {
                playerVX = 0;
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
