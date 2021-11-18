import javax.swing.*;

public class Pinky extends Ghost{

    public Pinky(short speed) {
        ghostSpeed = speed;
        ghostDX = -1;
        image = new ImageIcon("src/Sprites/Pinky0.gif").getImage();
    }
}
