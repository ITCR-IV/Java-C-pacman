import javax.swing.*;
import java.awt.*;

public class Fruit {
    private int fruitX = 13 * 24;
    private int fruitY = 10 * 24;
    private int points;
    private String fruitName;
    private Image image;
    private boolean removeFruit = false;


    public Fruit(int fruitX, int fruitY, int points, String fruitName) {
        this.fruitX = fruitX;
        this.fruitY = fruitY;
        this.points = points;
        this.fruitName = fruitName;
        switch (fruitName) {
            case "Apple" -> image = new ImageIcon("src/Sprites/apple.png").getImage();
            case "Cherry" -> image = new ImageIcon("src/Sprites/cherry.png").getImage();
            case "Galaga" -> image = new ImageIcon("src/Sprites/galaga.png").getImage();
            case "Strawberry" -> image = new ImageIcon("src/Sprites/strawberry.png").getImage();
            case "Melon" -> image = new ImageIcon("src/Sprites/melon.png").getImage();
            case "Orange" -> image = new ImageIcon("src/Sprites/orange.png").getImage();
            default -> image = new ImageIcon("src/Sprites/key.png").getImage();
        }
    }

    public void checkFruit(PlayingField pf, Graphics2D g2d){
        drawFruit(pf,g2d);

        if (((PacMan.getInstance().getPacManX() > (fruitX - 12)) && (PacMan.getInstance().getPacManX() < (fruitX + 12)))
                && ((PacMan.getInstance().getPacManY() > (fruitY - 12)) && (PacMan.getInstance().getPacManY() < (fruitY + 12)))
                && pf.isRunningGame()) {
            pf.setScore(pf.getScore()+points);
            removeFruit = true;
        }
    }

    public void drawFruit(PlayingField pf,Graphics2D g2d){
        g2d.drawImage(image,fruitX,fruitY,pf);
    }

    public boolean isRemoveFruit() {
        return removeFruit;
    }

}
