import javax.swing.*;

public class Inky extends Ghost{

    public Inky(short speed) {
        ghostSpeed = speed;
        ghostDX = 1;
        image = new ImageIcon("src/Sprites/Inky0.gif").getImage();
    }
}
