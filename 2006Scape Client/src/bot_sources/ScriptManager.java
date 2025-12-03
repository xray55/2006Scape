import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class ScriptManager {

    public static volatile Script currentScript; // Volatile ensures threads see changes instantly
    private static long nextRunTime;

    // --- SAFE START METHOD ---
    public static void startScript(Script script, Game c) {
        // 1. Stop old script first
        if (currentScript != null) {
            currentScript.onExit();
            currentScript = null; // Clear it immediately
        }

        try {
            System.out.println("[ScriptManager] Initializing " + script.getClass().getSimpleName() + "...");

            // 2. Initialize the NEW script fully before making it public
            script.init(c);
            script.onStart();

            // 3. Set the timer
            nextRunTime = System.currentTimeMillis();

            // 4. FINALLY make it live. This operation is atomic.
            // The game loop will either see null (do nothing) or the fully ready script.
            currentScript = script;

            System.out.println("[ScriptManager] Script is now live!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[ScriptManager] Failed to start script.");
        }
    }

    public static void loadScript(String filePath, Game c) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("File not found: " + filePath);
                return;
            }

            URL url = file.getParentFile().toURI().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{url});
            String className = file.getName().replace(".class", "");

            System.out.println("Loading class: " + className);
            Class<?> clazz = loader.loadClass(className);

            if (Script.class.isAssignableFrom(clazz)) {
                // Create instance but DO NOT assign to currentScript yet
                Script newScript = (Script) clazz.newInstance();

                // Pass to the safe start method
                startScript(newScript, c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void process() {
        // Local reference for thread safety
        Script active = currentScript;

        // Check if script exists AND is explicitly running
        if (active != null && active.isRunning) {
            if (!active.isPaused && System.currentTimeMillis() >= nextRunTime) {
                try {
                    int sleepTime = active.onLoop();
                    if (sleepTime < 0) {
                        stopScript();
                    } else {
                        nextRunTime = System.currentTimeMillis() + sleepTime;
                    }
                } catch (Exception e) {
                    System.out.println("[ScriptManager] Script crashed in loop:");
                    e.printStackTrace();
                    stopScript();
                }
            }
        }
    }

    public static void stopScript() {
        Script active = currentScript;
        if (active != null) {
            active.onExit();
            active.stop(); // Ensure flag is set to false
            currentScript = null;
            System.out.println("[ScriptManager] Script stopped.");
        }
    }
}