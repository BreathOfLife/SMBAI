package manager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundManager {

    private Clip background;
    private Clip jumpClip;
    private Clip coinClip;
    private Clip fireballClip;
    private Clip gameOverClip;
    private Clip stompClip;
    private Clip oneUpClip;
    private Clip mushroomClip;
    private Clip deathClip;
    private Clip errorClip;
    private long clipTime = 0;

    public SoundManager() {
        background = getClip(loadAudio("background"));
        jumpClip = getClip(loadAudio("jump"));
        coinClip = getClip(loadAudio("coin"));
        fireballClip = getClip(loadAudio("fireball"));
        gameOverClip = getClip(loadAudio("gameOver"));
        stompClip = getClip(loadAudio("stomp"));
        oneUpClip = getClip(loadAudio("oneUp"));
        mushroomClip = getClip(loadAudio("superMushroom"));
        deathClip = getClip(loadAudio("marioDies"));
        errorClip = getClip(loadAudio("error"));
    }

    private AudioInputStream loadAudio(String url) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/media/audio/" + url + ".wav");
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            return AudioSystem.getAudioInputStream(bufferedIn);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    private Clip getClip(AudioInputStream stream) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(stream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void resumeBackground(){
        background.setMicrosecondPosition(clipTime);
        background.start();
    }

    public void pauseBackground(){
        clipTime = background.getMicrosecondPosition();
        background.stop();
    }

    public void restartBackground() {
        clipTime = 0;
        resumeBackground();
    }

    public void playJump() {
        jumpClip.start();

    }

    public void playCoin() {
        coinClip.start();

    }

    public void playFireball() {
        fireballClip.start();

    }

    public void playGameOver() {
        gameOverClip.start();

    }

    public void playStomp() {
        stompClip.start();

    }

    public void playOneUp() {
        oneUpClip.start();

    }

    public void playSuperMushroom() {

        mushroomClip.start();

    }

    public void playMarioDies() {
        deathClip.start();

    }

    public void playFireFlower() {

    }
    
    public void playError() {
    	errorClip.start();
    }
}
