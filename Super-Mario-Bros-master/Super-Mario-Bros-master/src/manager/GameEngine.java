package manager;

import model.hero.Mario;
import smbai.SMBAILauncher;
import smbai.SMBAIManager;
import smbai.SaveData;
import view.ImageLoader;
import view.StartScreenSelection;
import view.UIManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameEngine implements Runnable {

    private final static int WIDTH = 1440, HEIGHT = 900;

    private MapManager mapManager;
    private SMBAIManager aiManager;
    public static GameEngine gameEngine;
    private UIManager uiManager;
    private SoundManager soundManager;
    public InputManager inputManager;
    private GameStatus gameStatus;
    private boolean isRunning;
    private Camera camera;
    private ImageLoader imageLoader;
    private Thread thread;
    private StartScreenSelection startScreenSelection = StartScreenSelection.START_GAME;
    private int selectedMap = 0;
	private boolean skipNextTimeUpdate = false;

	
	private GameEngine() {
		init();
	}
	
	public void jumpStart() {
		startGame();
	}

    private void init() {
        imageLoader = new ImageLoader();
        inputManager = new InputManager(this);
        gameStatus = GameStatus.START_SCREEN;
        camera = new Camera();
        uiManager = new UIManager(this, WIDTH, HEIGHT);
        soundManager = new SoundManager();
        mapManager = new MapManager();
        
        aiManager = new SMBAIManager();
        
        JFrame frame = new JFrame("Super Mario Bros.");
        frame.add(uiManager);
        frame.addKeyListener(inputManager);
        frame.addMouseListener(inputManager);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
			@Override
        	public void windowClosing(WindowEvent e) {
        		System.out.println("Window closed without saving");
        		System.exit(0);
        	}
        });
        
        start();
    }

    private synchronized void start() {
        if (isRunning)
            return;
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void reset(){
    	aiManager.scoreAI();
        resetCamera();
        aiManager.nextAI();
        aiManager.startAIRun();
    }

    public void resetCamera(){
        camera = new Camera();
        soundManager.restartBackground();
    }

    public void selectMapViaMouse() {
        String path = uiManager.selectMapViaMouse(uiManager.getMousePosition());
        if (path != null) {
            createMap(path);
        }
    }

    public void selectMapViaKeyboard(){
        String path = uiManager.selectMapViaKeyboard(selectedMap);
        if (path != null) {
            createMap(path);
        }
    }

    public void changeSelectedMap(boolean up){
        selectedMap = uiManager.changeSelectedMap(selectedMap, up);
    }

    private void createMap(String path) {
        boolean loaded = mapManager.createMap(imageLoader, path);
        if(loaded){
            setGameStatus(GameStatus.RUNNING);
            soundManager.restartBackground();
        }

        else
            setGameStatus(GameStatus.START_SCREEN);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = SMBAILauncher.tickSpeed; //Between 60 and 1000
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (isRunning && !thread.isInterrupted()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            while (delta >= 1) {
                if (gameStatus == GameStatus.RUNNING) {
                	aiManager.aiLoop();
                    gameLoop();
                }
                delta--;
            }
            if (gameStatus == GameStatus.RUNNING) {
            	if (skipNextTimeUpdate) { 
            		skipNextTimeUpdate = false;
            	} else {
            		mapManager.updateTime((double)(now - lastTime) / ns / 60);
            	}
            }
            lastTime = now;
            render();
            if (SMBAIManager.autoStart) {
            	selectMapViaKeyboard();
            	SMBAIManager.autoStart = false;
            }
        }
    }

    private void render() {
        uiManager.repaint();
    }

    private void gameLoop() {
    	if (isGameOver()) {
        	reset();
        	mapManager.resetCurrentMap(gameEngine);
        }

        if(mapManager.endLevel()) {
            reset();
            mapManager.resetCurrentMap(gameEngine);
            skipNextTimeUpdate = true;
    	}
        updateLocations();
        checkCollisions();
        updateCamera();
    }

    private void updateCamera() {
        Mario mario = mapManager.getMario();
        double marioVelocityX = mario.getVelX();
        double shiftAmount = 0;

        if (marioVelocityX > 0 && mario.getX() - 600 > camera.getX()) {
            shiftAmount = marioVelocityX;
        }

        camera.moveCam(shiftAmount, 0);
    }

    private void updateLocations() {
        mapManager.updateLocations();
    }

    private void checkCollisions() {
        mapManager.checkCollisions(this);
    }

    public void receiveInput(ButtonAction input) {

        if (gameStatus == GameStatus.START_SCREEN) {
            if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.START_GAME) {
                startGame();
            } else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_ABOUT) {
                setGameStatus(GameStatus.ABOUT_SCREEN);
            } else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_HELP) {
                setGameStatus(GameStatus.HELP_SCREEN);
            } else if (input == ButtonAction.GO_UP) {
                selectOption(true);
            } else if (input == ButtonAction.GO_DOWN) {
                selectOption(false);
            }
        }
        else if(gameStatus == GameStatus.MAP_SELECTION){
            if(input == ButtonAction.SELECT){
                selectMapViaKeyboard();
            }
            else if(input == ButtonAction.GO_UP){
                changeSelectedMap(true);
            }
            else if(input == ButtonAction.GO_DOWN){
                changeSelectedMap(false);
            }
        } else if (gameStatus == GameStatus.RUNNING) {
            Mario mario = mapManager.getMario();
            if (input == ButtonAction.JUMP) {
                mario.jump(this);
            } else if (input == ButtonAction.M_RIGHT) {
                mario.move(true, camera);
            } else if (input == ButtonAction.M_LEFT) {
                mario.move(false, camera);
            } else if (input == ButtonAction.ACTION_COMPLETED_X) {
                mario.setVelX(0);
            } else if (input == ButtonAction.ACTION_COMPLETED_Y) {
            	
            } else if (input == ButtonAction.FIRE) {
                mapManager.fire(this);
            } else if (input == ButtonAction.PAUSE_RESUME) {
                SaveData.save(SMBAIManager.pushFile);
                System.exit(0);
            } else if (input == ButtonAction.SELECT) {
            	reset();
                aiManager.startAIRun();
            }
        } else if(gameStatus == GameStatus.GAME_OVER){
            reset();
            aiManager.startAIRun();
        } else if(gameStatus == GameStatus.MISSION_PASSED){
            reset();
            aiManager.startAIRun();
        }
    }

    private void selectOption(boolean selectUp) {
        startScreenSelection = startScreenSelection.select(selectUp);
    }

    private void startGame() {
        if (gameStatus != GameStatus.GAME_OVER) {
            setGameStatus(GameStatus.MAP_SELECTION);
            selectMapViaKeyboard();
            
        }
    }

    private void pauseGame() {
        if (gameStatus == GameStatus.RUNNING) {
            setGameStatus(GameStatus.PAUSED);
            soundManager.pauseBackground();
        } else if (gameStatus == GameStatus.PAUSED) {
            setGameStatus(GameStatus.RUNNING);
            soundManager.resumeBackground();
        }
    }

    public void shakeCamera(){
        camera.shakeCamera();
    }

    public boolean isGameOver() {
        return mapManager.isGameOver();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public StartScreenSelection getStartScreenSelection() {
        return startScreenSelection;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }


    public int getRemainingLives() {
        return mapManager.getRemainingLives();
    }

    public int getCoins() {
        return mapManager.getCoins();
    }

    public int getSelectedMap() {
        return selectedMap;
    }

    public void drawMap(Graphics2D g2) {
        mapManager.drawMap(g2);
    }

    public Point getCameraLocation() {
        return new Point((int)camera.getX(), (int)camera.getY());
    }

    public void playCoin() {
        soundManager.playCoin();
    }

    public void playOneUp() {
        soundManager.playOneUp();
    }

    public void playSuperMushroom() {
        soundManager.playSuperMushroom();
    }

    public void playMarioDies() {
        soundManager.playMarioDies();
    }

    public void playJump() {
        soundManager.playJump();
    }

    public void playFireFlower() {
        soundManager.playFireFlower();
    }

    public void playFireball() {
        soundManager.playFireball();
    }

    public void playStomp() {
        soundManager.playStomp();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    /*public static void main(String... args) {
        gameStart();
    }*/

    public static void gameStart() {
    	gameEngine = new GameEngine();
	}

	public int getRemainingTime() {
        return mapManager.getRemainingTime();
    }
}
