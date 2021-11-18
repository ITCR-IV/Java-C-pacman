import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PlayingField extends JPanel implements ActionListener {

    private Dimension d;
    private final Font font = new Font("Arial",Font.BOLD,14);
    private boolean runningGame = false;
    private boolean dying = false;

    private final int GRID_SIZE = 24;
    private final int N_HORIZONTAL= 26;
    private final int N_VERTICAL= 29;
    private final int SCREEN_SIZE_X = GRID_SIZE*N_HORIZONTAL;
    private final int SCREEN_SIZE_Y = GRID_SIZE*N_VERTICAL;
    private final int MAX_GHOSTS = 4;
    private final int PACMAN_SPEED = 6;

    private int numGhosts = 0;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghostX, ghostY, ghostDX, ghostDY, ghostSpeed;

    private Image heart, inky, blinky, pinky, clyde, pacUp, pacDown, pacLeft, pacRight;

    private int pacManX, pacManY, pacManDX, pacManDY;
    private int reqDX, reqDY;

    private final int PAC_MAX_SPEED = 6;
    private  int pacCurrentSpeed = 6;
    private short[] screenData;
    private Timer timer;

    private final short levelInfo[] = {
            19, 26, 26, 26, 26, 18, 26, 26, 26, 26, 26, 22,  0,  0, 19, 26, 26, 26, 26, 26, 18, 26, 26, 26, 26, 22,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            17, 26, 26, 26, 26, 16, 26, 26, 18, 26, 26, 24, 26, 26, 24, 26, 26, 18, 26, 26, 16, 26, 26, 26, 26, 20,
            21,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0, 21,
            25, 26, 26, 26, 26, 20,  0,  0, 25, 26, 26, 22,  0,  0, 19, 26, 26, 28,  0,  0, 17, 26, 26, 26, 26, 28,
             0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0, 19, 26, 26, 24, 26, 26, 24, 26, 26, 22,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,
            26, 26, 26, 26, 26, 16, 26, 26, 20,  0,  0,  0,  0,  0,  0,  0,  0, 17, 26, 26, 16, 26, 26, 26, 26, 26,
             0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0, 17, 26, 26, 26, 26, 26, 26, 26, 26, 20,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,
            19, 26, 26, 26, 26, 16, 26, 26, 24, 26, 26, 22,  0,  0, 19, 26, 26, 24, 26, 26, 16, 26, 26, 26, 26, 22,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            25, 26, 22,  0,  0, 17, 26, 26, 18, 26, 26, 24, 26, 26, 24, 26, 26, 18, 26, 26, 20,  0,  0, 19, 26, 28,
             0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,
             0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,
            19, 26, 24, 26, 26, 28,  0,  0, 25, 26, 26, 22,  0,  0, 19, 26, 26, 28,  0,  0, 25, 26, 26, 24, 26, 22,
            21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,
            25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 24, 26, 26, 24, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 28
    };

    public PlayingField(){
        loadSprites();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        startGame();
    }

    private void loadSprites() {
        pacDown = new ImageIcon("src/Sprites/down.gif").getImage();
        pacUp = new ImageIcon("src/Sprites/up.gif").getImage();
        pacLeft = new ImageIcon("src/Sprites/left.gif").getImage();
        pacRight = new ImageIcon("src/Sprites/right.gif").getImage();
        inky = new ImageIcon("src/Sprites/Inky0.gif").getImage();
        pinky = new ImageIcon("src/Sprites/pinky0.gif").getImage();
        blinky = new ImageIcon("src/Sprites/Blinky0.gif").getImage();
        clyde = new ImageIcon("src/Sprites/Clyde0.gif").getImage();
    }

    private void initVariables(){
        screenData = new short[N_HORIZONTAL * N_VERTICAL];
        d = new Dimension(630,750);
        ghostX = new int [MAX_GHOSTS];
        ghostDX = new int [MAX_GHOSTS];
        ghostY = new int [MAX_GHOSTS];
        ghostDY = new int [MAX_GHOSTS];
        ghostSpeed = new int [MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        timer = new Timer(80, this);
        timer.restart();
    }

    private void startGame(){
        lives = 3;
        score = 0;
        startLevel();
        numGhosts=1;
        pacCurrentSpeed = 6;
    }

    private void startLevel(){
        int i;
        for (i = 0; i < N_VERTICAL * N_HORIZONTAL; i++) {
            screenData[i] = levelInfo[i];
        }
        continueLevel();
    }

    private void playGame(Graphics2D g2d){
        if (dying){
            death();
        } else{
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void movePacman(){
        int pos;
        short ch;
        if ((pacManX % GRID_SIZE == 0) && (pacManY % GRID_SIZE == 0)){
            pos = pacManX/GRID_SIZE + (N_HORIZONTAL * (pacManY/GRID_SIZE));
            ch = screenData[pos];

            if ((ch & 16) != 0){
                screenData[pos] = (short)(ch & 15);
                score++;
            }
            if (reqDX != 0 || reqDY != 0){
                if (!((reqDX == -1 && reqDY == 0 && (ch & 1) != 0)
                        || (reqDX == 1 && reqDY == 0 && (ch & 4) != 0)
                        || (reqDX == 0 && reqDY == -1 && (ch & 2) != 0)
                        || (reqDX == 0 && reqDY == 1 && (ch & 8) != 0))){
                    pacManDX = reqDX;
                    pacManDY = reqDY;
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
        pacManX = pacManX + PACMAN_SPEED*pacManDX;
        pacManY = pacManY + PACMAN_SPEED*pacManDY;
    }

    public void drawPacman(Graphics2D g2d){
        if (reqDX == -1){
            g2d.drawImage(pacLeft,pacManX+1,pacManY+1,this);
        } else if (reqDX == 1){
            g2d.drawImage(pacRight,pacManX+1,pacManY+1,this);
        } else if (reqDY == -1){
            g2d.drawImage(pacUp,pacManX+1,pacManY+1,this);
        } else if (reqDY == 1){
            g2d.drawImage(pacDown,pacManX+1,pacManY+1,this);
        }
    }

    public void moveGhosts(Graphics2D g2d){
       int pos;
       int count;
       for (int i = 0; i < numGhosts; i++){
           if ( (ghostX[i] % GRID_SIZE == 0) && (ghostY[i] % GRID_SIZE == 0) ){
               pos = ghostX[i]/GRID_SIZE + N_HORIZONTAL * (ghostY[i]/GRID_SIZE);
               count = 0;
               if ((screenData[pos]&1) == 0 && ghostDX[i] != 1){
                   dx[count] = -1;
                   dx[count] = 0;
                   count++;
               }
               if ((screenData[pos]&2) == 0 && ghostDX[i] != 1){
                   dx[count] = 0;
                   dx[count] = -1;
                   count++;
               }
               if ((screenData[pos]&4) == 0 && ghostDX[i] != -1){
                   dx[count] = 1;
                   dx[count] = 0;
                   count++;
               }
               if ((screenData[pos]&8) == 0 && ghostDX[i] != -1){
                   dx[count] = 0;
                   dx[count] = 1;
                   count++;
               }
               if (count==0){
                   if ((screenData[pos] & 15) == 15){
                       ghostDX[i] = 0;
                       ghostDY[i] = 0;
                   } else {
                       ghostDX[i] = -ghostDX[i];
                       ghostDY[i] = -ghostDX[i];
                   }
               } else {
                   count = (int) (Math.random() * count);
                   if (count > 3){
                       count = 3;
                   }
                   ghostDX[i] = dx[count];
                   ghostDY[i] = dy[count];
               }
           }
           ghostX[i] = ghostX[i] + (ghostDX[i] * ghostSpeed[i]);
           ghostY[i] = ghostY[i] + (ghostDY[i] * ghostSpeed[i]);
           drawGhost(g2d,ghostX[i]+1,ghostY[i]+1);

           if (((pacManX > (ghostX[i] - 12)) && (pacManX < (ghostX[i] + 12)))
                   && ((pacManY > (ghostY[i] - 12)) && (pacManY < (ghostY[i] + 12)))
                   && runningGame) {
               dying = true;
           }
       }
    }

    public void showIntroScreen(Graphics2D g2d){
        String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, SCREEN_SIZE_Y/4, 150);
    }

    public void drawScore(Graphics2D g2d){
        g2d.setFont(font);
        g2d.setColor(new Color(5,151,79));
        String s = "Score:" + score;
        g2d.drawString(s,SCREEN_SIZE_X/2 + 200, SCREEN_SIZE_Y+16);
        for (int i = 0; i<lives;i++){
            g2d.drawImage(pacRight,i*28+8,SCREEN_SIZE_Y+1,this);
        }
    }

    public void drawGhost(Graphics2D g2d, int x, int y){
        g2d.drawImage(blinky,x,y,this);
    }

    public void checkMaze(){
        int i = 0;
        boolean finished = true;

        while (i < N_HORIZONTAL*N_VERTICAL && finished){
            if (screenData[i] != 0){
                finished = false;
            }
            i++;
        }
        if(finished){
            startLevel();
        }
    }

    private void death(){
        lives--;
        if (lives == 0){
            runningGame = false;
        }
        continueLevel();
    }

    public void drawMaze(Graphics2D g2d){
        int i = 0;
        int x,y;

        for (y= 0; y<SCREEN_SIZE_Y; y+= GRID_SIZE){
            for (x= 0; x<SCREEN_SIZE_X; x+= GRID_SIZE){
                g2d.setColor(new Color(0,71,251));
                g2d.setStroke(new BasicStroke(5));

                if (levelInfo[i] == 0){
                    g2d.fillRect(x,y,GRID_SIZE,GRID_SIZE);
                }
                if ((screenData[i] & 1) != 0){
                    g2d.drawLine(x,y,x,y+GRID_SIZE-1);
                }
                if ((screenData[i] & 2) != 0){
                    g2d.drawLine(x,y,x+GRID_SIZE-1,y);
                }
                if ((screenData[i] & 4) != 0){
                    g2d.drawLine(x+GRID_SIZE-1,y,x+GRID_SIZE-1,y+GRID_SIZE-1);
                }
                if ((screenData[i] & 8) != 0){
                    g2d.drawLine(x,y+GRID_SIZE-1,x+GRID_SIZE-1,y+GRID_SIZE-1);
                }
                if ((screenData[i] & 16) != 0){
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x+10,y+10,4,4);
                }
                i++;
            }

        }

    }


    private void continueLevel(){
        int dx = 1;

        for (int i = 0; i < numGhosts; i++){
            ghostY[i] = 10 * GRID_SIZE;
            ghostX[i] = 13 * GRID_SIZE;
            ghostDX[i] = dx;
            ghostDY[i] = 0;
            dx = -dx;
            ghostSpeed[i] = PAC_MAX_SPEED;
        }

        pacManX = 13 * GRID_SIZE;
        pacManY = 22 * GRID_SIZE;
        pacManDX = 0;
        pacManDY = 0;
        reqDX = 0;
        reqDY = 0;
        dying = false;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0,d.width,d.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (runningGame){
             playGame(g2d);
        } else {
             showIntroScreen(g2d);
        }
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    class TAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            int key = e.getKeyCode();

            if (runningGame){
                if (key == KeyEvent.VK_LEFT){
                    reqDX = -1;
                    reqDY = 0;
                }
                else if (key == KeyEvent.VK_RIGHT){
                    reqDX = 1;
                    reqDY = 0;
                }
                else if (key == KeyEvent.VK_UP){
                    reqDX = 0;
                    reqDY = -1;
                }
                else if (key == KeyEvent.VK_DOWN){
                    reqDX = 0;
                    reqDY = 1;
                }
                else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()){
                    runningGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    runningGame = true;
                    startGame();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
}
