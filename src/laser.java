import java.awt.*;

public class laser {
    public Rectangle Hitbox;

    public laser(Rectangle Hitbox) {
        this.Hitbox = Hitbox;
    }

    public Rectangle getHitbox() {
        return Hitbox;
    }

    public int setPosX(int x) {
        Hitbox.x = x;
        return Hitbox.x;
    }

    public int setPosY(int y) {
        Hitbox.y = y;
        return Hitbox.y;
    }

    public void setHitbox(Rectangle hitbox) {
        Hitbox = hitbox;
    }

    public int getPosX() {
        return Hitbox.x;
    }

    public int getPosY() {
        return Hitbox.y;
    }

    public int shoot () {

        return Hitbox.y -= 10;
    }
    public boolean collide(Rectangle a) {
        return Hitbox.intersects(a);
    }
}
