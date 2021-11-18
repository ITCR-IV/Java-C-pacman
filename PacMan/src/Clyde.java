import javax.swing.*;

public class Clyde extends Ghost{

    public Clyde(short speed) {
        ghostSpeed = speed;
        ghostDX = -1;
        image = new ImageIcon("src/Sprites/Clyde0.gif").getImage();
    }
}
