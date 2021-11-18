import javax.swing.*;
import java.awt.*;

public final class PacMan {

    public final int PAC_SPEED = 6;
    private int lives, pacManX, pacManY, pacManDX, pacManDY;

    private Image pacUp, pacDown, pacLeft, pacRight;
    private static PacMan instance;

    private PacMan() {
        pacDown = new ImageIcon("src/Sprites/down.gif").getImage();
        pacUp = new ImageIcon("src/Sprites/up.gif").getImage();
        pacLeft = new ImageIcon("src/Sprites/left.gif").getImage();
        pacRight = new ImageIcon("src/Sprites/right.gif").getImage();
        lives = 3;
    }

    public static PacMan getInstance() {
        if (instance == null){
            instance = new PacMan();
        }
        return instance;
    }

    public void movePacman(PlayingField pf){
        int pos;
        short ch;
        if ((pacManX % pf.GRID_SIZE == 0) && (pacManY % pf.GRID_SIZE == 0)){
            pos = pacManX/pf.GRID_SIZE + (pf.N_HORIZONTAL * (pacManY /pf.GRID_SIZE));
            ch = pf.getScreenData()[pos];

            if ((ch & 16) != 0){
                pf.getScreenData()[pos] = (short)(ch & 47);
                pf.setScore(pf.getScore()+10);
            }
            if ((ch & 32) != 0){
                if (pacManX < 100){
                    pacManX = pacManX + pf.GRID_SIZE*25;
                } else{
                    pacManX = pacManX - pf.GRID_SIZE*25;
                }
            }
            if ((ch & 64) != 0){
                pf.getScreenData()[pos] = (short)(ch & 47);
                pf.activatePowerUp();
            }
            if (pf.getReqDX() != 0 || pf.getReqDY() != 0){
                if (!((pf.getReqDX() == -1 && pf.getReqDY() == 0 && (ch & 1) != 0)
                        || (pf.getReqDX() == 1 && pf.getReqDY() == 0 && (ch & 4) != 0)
                        || (pf.getReqDX() == 0 && pf.getReqDY() == -1 && (ch & 2) != 0)
                        || (pf.getReqDX() == 0 && pf.getReqDY() == 1 && (ch & 8) != 0))){
                    pacManDX = pf.getReqDX();
                    pacManDY = pf.getReqDY();
                }
            }
            if ((pacManDX == -1 && pacManDY == 0 && (ch & 1) != 0)
                    || (pacManDX == 1 && pacManDY == 0 && (ch & 4) != 0)
                    || (pacManDX == 0 && pacManDY == -1 && (ch & 2) != 0)
                    || (pacManDX == 0 && pacManDY == 1 && (ch & 8) != 0)) {
                pacManDX = 0;
                pacManDY = 0;
            }
        }
        pacManX = pacManX + PAC_SPEED*pacManDX;
        pacManY = pacManY + PAC_SPEED*pacManDY;
    }

    public void drawPacman(PlayingField pf, Graphics2D g2d){
        if (pf.getReqDX() == -1){
            g2d.drawImage(pacLeft,pacManX+1,pacManY+1,pf);
        } else if (pf.getReqDX() == 1){
            g2d.drawImage(pacRight,pacManX+1,pacManY+1,pf);
        } else if (pf.getReqDY() == -1){
            g2d.drawImage(pacUp,pacManX+1,pacManY+1,pf);
        } else {
            g2d.drawImage(pacDown,pacManX+1,pacManY+1,pf);
        }
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getPacManX() {
        return pacManX;
    }

    public void setPacManX(int pacManX) {
        this.pacManX = pacManX;
    }

    public int getPacManY() {
        return pacManY;
    }

    public void setPacManY(int pacManY) {
        this.pacManY = pacManY;
    }

    public int getPacManDX() {
        return pacManDX;
    }

    public void setPacManDX(int pacManDX) {
        this.pacManDX = pacManDX;
    }

    public int getPacManDY() {
        return pacManDY;
    }

    public void setPacManDY(int pacManDY) {
        this.pacManDY = pacManDY;
    }

    public Image getPacUp() {
        return pacUp;
    }

    public Image getPacDown() {
        return pacDown;
    }

    public Image getPacLeft() {
        return pacLeft;
    }

    public Image getPacRight() {
        return pacRight;
    }
}
