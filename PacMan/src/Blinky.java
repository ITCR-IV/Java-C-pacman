import javax.swing.*;

public class Blinky extends Ghost{

    public Blinky(short speed) {
        ghostSpeed = speed;
        ghostDX = 1;
        image = new ImageIcon("src/Sprites/Blinky0.gif").getImage();
    }
}
