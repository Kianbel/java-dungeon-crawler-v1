package gui;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AudioManager {
    private static final AudioManager instance = new AudioManager();
    private AudioManager() {}
    public static AudioManager getInstance() {return instance;}

    private final Map<String, AudioClip> sfxRegistry = new HashMap<>();
    private MediaPlayer bgmPlayer;
    private double sfxVolume = 1.0;
    private double bgmVolume = 0.5;
    private Random random = new Random();

    /**
     * Registers a short audio clip into the memory cache.
     * @param key          Unique identifier for the sound effect (e.g., "hurt", "coin").
     * @param resourcePath Path relative to the source root (e.g., "/audio/hit.wav").
     */
    public void registerSFX(String key, String resourcePath) {
        try {
            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.err.println("Audio Resource not found: " + resourcePath);
                return;
            }
            AudioClip clip = new AudioClip(resource.toExternalForm());
            sfxRegistry.put(key, clip);
        } catch (Exception e) {
            System.err.println("Failed to load SFX [" + key + "]: " + e.getMessage());
        }
    }

    /**
     * Plays a cached sound effect instantly. Can overlap with other sounds.
     */
    public void playSFX(String key) {
        final double PITCH_VARIANCE = 0.1;

        AudioClip clip = sfxRegistry.get(key);
        if (clip != null) {
            // Calculate variables safely outside the thread block
            double randomModifier = (random.nextDouble() * 2.0 - 1.0) * PITCH_VARIANCE;
            double randomRate = 1.0 + randomModifier;

            // FIX: Run audio dispatch asynchronously to prevent engine lag from dropping audio frames
            javafx.application.Platform.runLater(() -> {
                try {
                    clip.play(sfxVolume, 0.0, randomRate, 0.0, 0);
                } catch (Exception e) {
                    System.err.println("Audio clip failed to fire: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Streams and continuously loops a background music file.
     * @param resourcePath Path relative to the source root (e.g., "/audio/dungeon_theme.mp3").
     */
    public void playBGM(String resourcePath) {
        try {
            // FIX: Explicitly stop AND dispose of the previous player to free native audio lines
            if (bgmPlayer != null) {
                bgmPlayer.stop();
                bgmPlayer.dispose();
                bgmPlayer = null;
            }

            URL resource = getClass().getResource(resourcePath);
            if (resource == null) {
                System.err.println("BGM Resource not found: " + resourcePath);
                return;
            }

            Media media = new Media(resource.toExternalForm());
            bgmPlayer = new MediaPlayer(media);
            bgmPlayer.setVolume(bgmVolume);

            // Set up infinite loop configurations
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.play();
        } catch (Exception e) {
            System.err.println("Failed to play BGM [" + resourcePath + "]: " + e.getMessage());
        }
    }

    public void stopBGM() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
            bgmPlayer.dispose(); // FIX: Clear resources when explicitly stopping music too
            bgmPlayer = null;
        }
    }

    public void setSFXVolume(double volume) { this.sfxVolume = Math.clamp(volume, 0.0, 1.0); }
    public void setBGMVolume(double volume) {
        this.bgmVolume = Math.clamp(volume, 0.0, 1.0);
        if (bgmPlayer != null) bgmPlayer.setVolume(this.bgmVolume);
    }
}