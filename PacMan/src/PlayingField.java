import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class PlayingField extends JPanel implements ActionListener {

    private Dimension d;
    private final Font font = new Font("Arial",Font.BOLD,14);
    private boolean runningGame = false;
    private boolean dying = false;

    public final int GRID_SIZE = 24;
    public final int N_HORIZONTAL= 26;
    public final int N_VERTICAL= 29;
    public final int SCREEN_SIZE_X = GRID_SIZE*N_HORIZONTAL;
    public final int SCREEN_SIZE_Y = GRID_SIZE*N_VERTICAL;
    public final int MAX_GHOSTS = 4;

    private int numGhosts = 0;

    private int score;
    private int[] dx, dy;

    private int reqDX, reqDY;

    private short[] validSpeeds = {1,2,3,4,6,8};
    private short currentSpeed = 2;
    private short[] screenData;
    private Timer timer;

    private Ghost[] ghosts = new Ghost[MAX_GHOSTS];

    private Fruit currentFruit;

    private final short levelInfo[] = {
            19, 26, 26, 26, 26, 18, 26, 26, 26, 26, 26, 22,  0,  0, 19, 26, 26, 26, 26, 26, 18, 26, 26, 26, 26, 22,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            69,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 69,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            17, 26, 26, 26, 26, 16, 26, 26, 18, 26, 26, 24, 26, 26, 24, 26, 26, 18, 26, 26, 16, 26, 26, 26, 26, 20,
            21,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0, 21,
            25, 26, 26, 26, 26, 20,  0,  0, 25, 26, 26, 22,  0,  0, 19, 26, 26, 28,  0,  0, 17, 26, 26, 26, 26, 28,
             0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,  5,  0,  0,  5,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,  5,  0,  0,  5,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  3, 10, 10,  8, 10, 10,  8, 10, 10,  6,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  5,  0,  0,  0,  0,  0,  0,  0,  0,  5,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  5,  0,  0,  0,  0,  0,  0,  0,  0,  5,  0,  0, 21,  0,  0,  0,  0,  0,
            42, 10, 10, 10, 10, 16, 10, 10,  4,  0,  0,  0,  0,  0,  0,  0,  0,  1, 10, 10, 16, 10, 10, 10, 10, 42,
             0,  0,  0,  0,  0, 21,  0,  0,  5,  0,  0,  0,  0,  0,  0,  0,  0,  5,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  5,  0,  0,  0,  0,  0,  0,  0,  0,  5,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  1, 10, 10, 10, 10, 10, 10, 10, 10,  4,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  5,  0,  0,  0,  0,  0,  0,  0,  0,  5,  0,  0, 21,  0,  0,  0,  0,  0,
             0,  0,  0,  0,  0, 21,  0,  0,  5,  0,  0,  0,  0,  0,  0,  0,  0,  5,  0,  0, 21,  0,  0,  0,  0,  0,
            19, 26, 26, 26, 26, 16, 26, 26, 24, 26, 26, 22,  0,  0, 19, 26, 26, 24, 26, 26, 16, 26, 26, 26, 26, 22,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0, 21,  0,  0,  0,  0, 21,
            73, 26, 22,  0,  0, 17, 26, 26, 18, 26, 26, 24, 10, 10, 24, 26, 26, 18, 26, 26, 20,  0,  0, 19, 26, 76,
             0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,
             0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0, 21,  0,  0,
            19, 26, 24, 26, 26, 28,  0,  0, 25, 26, 26, 22,  0,  0, 19, 26, 26, 28,  0,  0, 25, 26, 26, 24, 26, 22,
            21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,
            21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,  0,  0, 21,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 21,
            25, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 24, 26, 26, 24, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 28
    };

    public PlayingField(){
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        startGame();
    }

    private void initVariables(){
        screenData = new short[N_HORIZONTAL * N_VERTICAL];
        d = new Dimension(630,750);
        numGhosts = 0;
        ghosts = new Ghost[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        timer = new Timer(40, this);
        timer.restart();
    }

    private void startGame(){
        PacMan.getInstance().setLives(3);
        score = 0;
        startLevel();
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
            PacMan.getInstance().movePacman(this);
            PacMan.getInstance().drawPacman(this,g2d);
            moveGhosts(g2d);
            if (currentFruit != null){
                currentFruit.checkFruit(this,g2d);
                if (currentFruit.isRemoveFruit()){
                    currentFruit = null;
                }
            }
            checkMaze();
        }
    }

    private void moveGhosts(Graphics2D g2d){
       for (int i = 0; i < numGhosts; i++){
           ghosts[i].moveGhost(this,g2d);
       }
    }

    private void showIntroScreen(Graphics2D g2d){
        String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, SCREEN_SIZE_Y/4, 150);
    }

    private void drawScore(Graphics2D g2d){
        g2d.setFont(font);
        g2d.setColor(new Color(5,151,79));
        String s = "Score:" + score;
        g2d.drawString(s,SCREEN_SIZE_X/2 + 200, SCREEN_SIZE_Y+16);
        for (int i = 0; i<PacMan.getInstance().getLives();i++){
            g2d.drawImage(PacMan.getInstance().getPacRight(),i*28+8,SCREEN_SIZE_Y+1,this);
        }
    }

    public void resetPowerPellets(){
        screenData[52] = levelInfo[52];
        screenData[77] = levelInfo[77];
        screenData[572] = levelInfo[572];
        screenData[597] = levelInfo[597];
    }

    public void resetPellets(){
        for (int i = 0; i < N_VERTICAL * N_HORIZONTAL; i++) {
            if (i == 52 || i == 77 || i == 572 || i == 597){
                continue;
            } else{
                screenData[i] = levelInfo[i];
            }
        }
    }

    public void resetGhosts(){
        numGhosts = 0;
        ghosts = new Ghost[MAX_GHOSTS];
    }

    public void changeSpeedGhosts(short speedAdded){
        for (int i = 0; i < numGhosts; i++) {
            if (currentSpeed+speedAdded > 5){
                currentSpeed = 5;
            }else if (currentSpeed+speedAdded < 0){
                currentSpeed = 0;
            }else {
                currentSpeed = (short) (currentSpeed+speedAdded);
            }
            ghosts[i].setGhostSpeed(validSpeeds[currentSpeed]);
        }
    }

    private void checkMaze(){
        int i = 0;
        boolean finished = true;

        while (i < N_HORIZONTAL*N_VERTICAL && finished){
            if ((screenData[i] & 80) != 0){
                finished = false;
            }
            i++;
        }
        /*
        if(finished){
            resetPellets();
            resetPowerPellets();
        }
        */
    }

    private void death(){
        PacMan.getInstance().setLives(PacMan.getInstance().getLives()-1);
        if (PacMan.getInstance().getLives() == 0){
            runningGame = false;
        }
        continueLevel();
    }

    public void activatePowerUp(){
        for (int i = 0; i < numGhosts; i++){
            ghosts[i].setVulnerableFlag(true);
        }
        Thread thread = new Thread(){
            public void run(){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < numGhosts; i++){
                    ghosts[i].setVulnerableFlag(false);
                }
            }
        };
        thread.start();
    }


    private void drawMaze(Graphics2D g2d){
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
                    g2d.setColor(new Color(255, 195, 112));
                    g2d.fillOval(x+10,y+10,4,4);
                }
                if ((screenData[i] & 64) != 0){
                    g2d.setColor(new Color(255, 195, 112));
                    g2d.fillOval(x+6,y+6,12,12);
                }
                i++;
            }

        }

    }

    public void addGhost(String ghostName){
        if (numGhosts == 4){
            return;
        } else if (ghostName == "Clyde"){
            ghosts[numGhosts] = new Clyde(validSpeeds[currentSpeed]);
        } else if (ghostName == "Inky"){
            ghosts[numGhosts] = new Inky(validSpeeds[currentSpeed]);
        } else if (ghostName == "Pinky"){
            ghosts[numGhosts] = new Pinky(validSpeeds[currentSpeed]);
        } else{
            ghosts[numGhosts] = new Blinky(validSpeeds[currentSpeed]);
        }
        numGhosts++;
    }

    public void addFruit(String fruitName, int points){
        currentFruit = new Fruit(13 * GRID_SIZE,16 * GRID_SIZE, points, fruitName);
    }

    private void continueLevel(){
        numGhosts = 0;
        ghosts = new Ghost[MAX_GHOSTS];

        addGhost("Clyde");
        addGhost("Inky");
        addGhost("Blinky");
        addGhost("Pinky");

        addFruit("Galaga",1000);
        
        PacMan.getInstance().setPacManX(13 * GRID_SIZE);
        PacMan.getInstance().setPacManY(22 * GRID_SIZE);
        PacMan.getInstance().setPacManDX(0);
        PacMan.getInstance().setPacManDY(0) ;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int[] getDx() {
        return dx;
    }

    public int[] getDy() {
        return dy;
    }

    public int getReqDX() {
        return reqDX;
    }

    public int getReqDY() {
        return reqDY;
    }

    public short[] getScreenData() {
        return screenData;
    }

    public boolean isRunningGame() {
        return runningGame;
    }

    public void setRunningGame(boolean runningGame) {
        this.runningGame = runningGame;
    }

    public boolean isDying() {
        return dying;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }
}
