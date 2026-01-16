package de.hhn.it.devtools.javafx.powerpong.view;

import javax.sound.sampled.*;

/**
 * Sound manager for PowerPong with programmatically generated retro sounds.
 * Uses javax.sound.sampled to create synth-style sound effects.
 */
public class SoundManager {

    public enum SoundType {
        PADDLE_HIT,
        WALL_HIT,
        SCORE,
        POWERUP,
        WIN,
        LOSE
    }

    public enum MusicState {
        OFF,
        MENU,
        GAME
    }

    private double volume = 0.7;
    private double musicVolume = 0.4;
    private boolean muted = false;

    private static final float SAMPLE_RATE = 44100f;

    private volatile MusicState currentMusicState = MusicState.OFF;
    private Thread musicThread;
    private volatile boolean musicRunning = false;

    public SoundManager() {
        // Pre-warm the audio system
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            if (AudioSystem.isLineSupported(info)) {
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.close();
            }
        } catch (Exception e) {
            // Audio not available
        }
    }

    public void setVolume(double vol) {
        this.volume = Math.max(0, Math.min(1, vol));
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isMuted() {
        return muted;
    }

    public void playSound(SoundType type) {
        if (muted)
            return;

        // Play sound in background thread to avoid blocking
        new Thread(() -> {
            try {
                byte[] sound = generateSound(type);
                if (sound != null) {
                    playBytes(sound);
                }
            } catch (Exception e) {
                // Silently ignore audio errors
            }
        }).start();
    }

    public void startMusic(MusicState state) {
        if (currentMusicState == state)
            return;

        currentMusicState = state;

        if (state == MusicState.OFF) {
            musicRunning = false;
        } else {
            if (!musicRunning) {
                musicRunning = true;
                musicThread = new Thread(this::musicLoop);
                musicThread.setDaemon(true);
                musicThread.start();
            }
        }
    }

    private void musicLoop() {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (!AudioSystem.isLineSupported(info))
                return;

            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, 4096); // Small buffer for low latency
            line.start();

            int step = 0;

            while (musicRunning) {
                if (muted || currentMusicState == MusicState.OFF) {
                    Thread.sleep(100);
                    continue;
                }

                int bpm = (currentMusicState == MusicState.GAME) ? 120 : 95;
                int samplesPer16th = (int) (SAMPLE_RATE * 60 / bpm / 4);

                byte[] buffer = new byte[samplesPer16th];

                // Generate synthesis for this step
                double bassFreq = getBassFreq(step, currentMusicState);
                double melodyFreq = getMelodyFreq(step, currentMusicState);

                for (int i = 0; i < samplesPer16th; i++) {
                    double t = (double) i / SAMPLE_RATE;

                    // Bass (Squareish wave)
                    double bass = 0;
                    if (bassFreq > 0) {
                        double bassEnv = Math.exp(-t * 10); // Pluck decay
                        bass = Math.signum(Math.sin(2 * Math.PI * bassFreq * t)) * 0.3 * bassEnv;
                    }

                    // Melody (Soft Sine/Triangle)
                    double melody = 0;
                    if (melodyFreq > 0) {
                        double melEnv = Math.exp(-t * 5);
                        melody = (Math.sin(2 * Math.PI * melodyFreq * t) +
                                0.5 * Math.sin(2 * Math.PI * melodyFreq * 2 * t)) * 0.2 * melEnv;
                    }

                    double mix = (bass + melody) * musicVolume * volume;
                    buffer[i] = (byte) (Math.max(-1, Math.min(1, mix)) * 127);
                }

                line.write(buffer, 0, buffer.length);
                step++;
            }

            line.drain();
            line.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getBassFreq(int step, MusicState state) {
        int patternLength = 16;
        int s = step % patternLength;

        // Define frequencies
        double C2 = 65.41;
        double Eb2 = 77.78;
        double F2 = 87.31;
        double G2 = 98.00;
        double Ab2 = 103.83;
        double Bb2 = 116.54;

        if (state == MusicState.MENU) {
            // Relaxed funk bass for menu
            if (s == 0)
                return C2;
            if (s == 3)
                return C2;
            if (s == 6)
                return F2;
            if (s == 8)
                return G2;
            if (s == 11)
                return Bb2;
            if (s == 14)
                return G2;
        } else {
            // TRON STYLE: 4-Bar Chord Progression Bass
            // Bar 1: C Minor
            // Bar 2: Ab Major
            // Bar 3: Eb Major
            // Bar 4: Bb Major

            int bar = (step / 16) % 4; // Current bar (0-3)

            double root = switch (bar) {
                case 0 -> C2;
                case 1 -> Ab2;
                case 2 -> Eb2;
                case 3 -> Bb2;
                default -> C2;
            };

            // Driving bass pattern (Eights)
            if (s % 2 == 0)
                return root;
            if (s % 4 == 3)
                return root * 2; // Octave jump for energy
        }
        return 0;
    }

    private double getMelodyFreq(int step, MusicState state) {
        int patternLength = 32;
        int s = step % patternLength;

        double C4 = 261.63;
        double Eb4 = 311.13;
        double F4 = 349.23;
        double G4 = 392.00;
        double Bb4 = 466.16;

        if (state == MusicState.MENU) {
            // Relaxed melody
            if (s == 4)
                return G4;
            if (s == 12)
                return F4;
            if (s == 20)
                return Eb4;
            if (s == 28)
                return C4;
        } else {
            // TRON STYLE: Rolling Arpeggiator (16th notes)
            // Pattern changes with the chords

            int bar = (step / 16) % 4;
            double root4;
            double third4;
            double fifth4;

            switch (bar) {
                case 0: // Cm
                    root4 = 261.63; // C4
                    third4 = 311.13; // Eb4
                    fifth4 = 392.00; // G4
                    break;
                case 1: // Ab (G#)
                    root4 = 207.65; // Ab3 (lowered for range)
                    third4 = 261.63; // C4
                    fifth4 = 311.13; // Eb4
                    break;
                case 2: // Eb
                    root4 = 311.13; // Eb4
                    third4 = 392.00; // G4
                    fifth4 = 466.16; // Bb4
                    break;
                case 3: // Bb
                    root4 = 233.08; // Bb3
                    third4 = 293.66; // D4
                    fifth4 = 349.23; // F4
                    break;
                default:
                    root4 = 261.63;
                    third4 = 311.13;
                    fifth4 = 392.00;
            }

            // Arp Pattern: Root - Third - Fifth - Octave
            int arpStep = s % 4;

            return switch (arpStep) {
                case 0 -> root4;
                case 1 -> third4;
                case 2 -> fifth4;
                case 3 -> root4 * 2; // Octave
                default -> 0;
            };
        }
        return 0;
    }

    private byte[] generateSound(SoundType type) {
        return switch (type) {
            case PADDLE_HIT -> generateTone(800, 50, 0.8); // High short blip
            case WALL_HIT -> generateTone(400, 40, 0.5); // Lower thump
            case SCORE -> generateSweep(400, 800, 150); // Rising sweep
            case POWERUP -> generateChime(600, 100); // Magical chime
            case WIN -> generateFanfare(); // Victory fanfare
            case LOSE -> generateSweep(600, 200, 200); // Falling tone
        };
    }

    private byte[] generateTone(double freq, int durationMs, double amp) {
        int numSamples = (int) (SAMPLE_RATE * durationMs / 1000);
        byte[] data = new byte[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double time = i / SAMPLE_RATE;
            double envelope = Math.min(1, Math.min(i / 100.0, (numSamples - i) / 100.0));
            double sample = Math.sin(2 * Math.PI * freq * time) * amp * envelope * volume;
            data[i] = (byte) (sample * 127);
        }
        return data;
    }

    private byte[] generateSweep(double startFreq, double endFreq, int durationMs) {
        int numSamples = (int) (SAMPLE_RATE * durationMs / 1000);
        byte[] data = new byte[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double t = (double) i / numSamples;
            double freq = startFreq + (endFreq - startFreq) * t;
            double time = i / SAMPLE_RATE;
            double envelope = Math.min(1, (numSamples - i) / (numSamples * 0.3));
            double sample = Math.sin(2 * Math.PI * freq * time) * 0.7 * envelope * volume;
            data[i] = (byte) (sample * 127);
        }
        return data;
    }

    private byte[] generateChime(double baseFreq, int durationMs) {
        int numSamples = (int) (SAMPLE_RATE * durationMs / 1000);
        byte[] data = new byte[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double time = i / SAMPLE_RATE;
            double envelope = Math.exp(-time * 20); // Quick decay
            double sample = (Math.sin(2 * Math.PI * baseFreq * time) * 0.5 +
                    Math.sin(2 * Math.PI * baseFreq * 1.5 * time) * 0.3 +
                    Math.sin(2 * Math.PI * baseFreq * 2 * time) * 0.2) * envelope * volume;
            data[i] = (byte) (sample * 127);
        }
        return data;
    }

    private byte[] generateFanfare() {
        // Three ascending notes
        byte[] note1 = generateTone(523, 100, 0.8); // C5
        byte[] note2 = generateTone(659, 100, 0.8); // E5
        byte[] note3 = generateTone(784, 200, 0.8); // G5

        byte[] result = new byte[note1.length + note2.length + note3.length];
        System.arraycopy(note1, 0, result, 0, note1.length);
        System.arraycopy(note2, 0, result, note1.length, note2.length);
        System.arraycopy(note3, 0, result, note1.length + note2.length, note3.length);
        return result;
    }

    private void playBytes(byte[] data) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 8, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (!AudioSystem.isLineSupported(info))
                return;

            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            line.write(data, 0, data.length);
            line.drain();
            line.close();
        } catch (Exception e) {
            // Ignore audio errors
        }
    }
}
