import org.w3c.dom.css.Rect;

import java.awt.*;

public class enemyLaser {
    public Rectangle Hitbox;

    public enemyLaser(Rectangle hitbox) {
        Hitbox = hitbox;
    }
    public int getPosX() {
        return Hitbox.x;
    }

    public int getPosY() {
        return Hitbox.y;
    }
    public int shoot () {

        return Hitbox.y += 2;
    }
    public boolean collide(Rectangle a) {
        return Hitbox.intersects(a);
    }
    public int grow() {
        return Hitbox.width += 2;
    }
    public int shiftX() {
        return Hitbox.x -=1;
    }
    public int getWidth() {
        return Hitbox.width;
    }
    public int track(int x, int y) {
        if (Hitbox.y < y) {
            if (Hitbox.x < x) {
                return Hitbox.x += 1;
            } else if(Hitbox.x > x) {
                return Hitbox.x -= 1;
            }
        }
        return Hitbox.x;
    }
    public int getHeight() {
        return Hitbox.height;
    }
}
