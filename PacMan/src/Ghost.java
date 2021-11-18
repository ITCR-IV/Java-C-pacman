import javax.swing.*;
import java.awt.*;

public abstract class Ghost {
    protected int ghostX = 13 * 24;
    protected int ghostY = 10 * 24;
    protected int ghostDY = 0;
    protected int ghostDX;
    protected int ghostSpeed;
    protected String ghostName;
    protected Image image;
    protected Image vulnerableImage = new ImageIcon("src/Sprites/VulnerableGhost.png").getImage();
    protected boolean vulnerableFlag;

    public void moveGhost(PlayingField pf, Graphics2D g2d){
        int pos;
        int count;
        if ( (ghostX % pf.GRID_SIZE == 0) && (ghostY % pf.GRID_SIZE == 0) ){
            pos = ghostX/pf.GRID_SIZE + pf.N_HORIZONTAL * (ghostY/pf.GRID_SIZE);
            count = 0;
            short ch = pf.getScreenData()[pos];
            if ((ch & 32) != 0){
                if (ghostX < 100){
                    ghostX = ghostX + pf.GRID_SIZE*25;
                } else{
                    ghostX = ghostX - pf.GRID_SIZE*25;
                }
            }
            if ((pf.getScreenData()[pos]&1) == 0 && ghostDX != 1){
                pf.getDx()[count] = -1;
                pf.getDy()[count] = 0;
                count++;
            }
            if ((pf.getScreenData()[pos]&2) == 0 && ghostDY != 1){
                pf.getDx()[count] = 0;
                pf.getDy()[count] = -1;
                count++;
            }
            if ((pf.getScreenData()[pos]&4) == 0 && ghostDX != -1){
                pf.getDx()[count] = 1;
                pf.getDy()[count] = 0;
                count++;
            }
            if ((pf.getScreenData()[pos]&8) == 0 && ghostDY != -1){
                pf.getDx()[count] = 0;
                pf.getDy()[count] = 1;
                count++;
            }
            if (count==0){
                if ((pf.getScreenData()[pos] & 15) == 15){
                    ghostDX = 0;
                    ghostDY = 0;
                } else {
                    ghostDX = -ghostDX;
                    ghostDY = -ghostDX;
                }
            } else {
                count = (int) (Math.random() * count);
                if (count > 3){
                    count = 3;
                }
                ghostDX = pf.getDx()[count];
                ghostDY = pf.getDy()[count];
            }
        }
        ghostX = ghostX + (ghostDX * ghostSpeed);
        ghostY = ghostY + (ghostDY * ghostSpeed);
        drawGhost(pf,g2d);

        if (((PacMan.getInstance().getPacManX() > (ghostX - 12)) && (PacMan.getInstance().getPacManX() < (ghostX + 12)))
                && ((PacMan.getInstance().getPacManY() > (ghostY - 12)) && (PacMan.getInstance().getPacManY() < (ghostY + 12)))
                && pf.isRunningGame()) {
            if (vulnerableFlag){
                pf.setScore(pf.getScore()+100);
                vulnerableFlag = false;
                ghostX = 13 * 24;
                ghostY = 10 * 24;
            } else{
                pf.setDying(true);
            }
        }
    }

    public void drawGhost(PlayingField pf,Graphics2D g2d){
        if (vulnerableFlag){
            g2d.drawImage(vulnerableImage,ghostX,ghostY,pf);
        } else{
            g2d.drawImage(image,ghostX,ghostY,pf);
        }
    }

    public void setVulnerableFlag(boolean vulnerableFlag) {
        this.vulnerableFlag = vulnerableFlag;
    }

    public void setGhostSpeed(int ghostSpeed) {
        this.ghostSpeed = ghostSpeed;
    }


}
