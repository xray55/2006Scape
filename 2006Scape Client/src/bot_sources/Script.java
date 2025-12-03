import java.awt.*;

public abstract class Script {
    public Game client; // Access to the game instance
    public volatile boolean isRunning = false;
    public volatile boolean isPaused = false;

    // Connects the script to the game
    public void init(Game c) {
        this.client = c;
        this.isRunning = true;
    }

    // --- Abstract Methods (Your bots MUST have these) ---
    public abstract void onStart();
    public abstract int onLoop(); // Returns time to sleep in ms
    public abstract void onExit();

    // Optional: Draw paint graphics
    public void onPaint(Graphics g) {}

    // Stops the current script
    public void stop() {
        isRunning = false;
    }

    // Safe sleep method
    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- API Helpers (Add more later) ---
    public void log(String msg) {
        System.out.println("[Bot] " + msg);
        if (client != null) {
            client.pushMessage(msg, 0, "");
        }
    }

}
