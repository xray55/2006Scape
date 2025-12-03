import javax.swing.*;
import java.awt.*;
import java.io.File;

public class BotPanel extends JFrame {

    private final Game gameInstance;

    public BotPanel(Game game) {
        this.gameInstance = game;
        setupUI();
    }

    private void setupUI() {
        setTitle("2006Bot Controller");
        setSize(300, 100);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Keep it open
        setLocationRelativeTo(null); // Center it

        // --- Start Button ---
        JButton startButton = new JButton("Load Script");
        startButton.addActionListener(e -> {
            // Open File Chooser
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Loading: " + selectedFile.getName());

                // FIX: Pass 'gameInstance' as the second argument!
                ScriptManager.loadScript(selectedFile.getAbsolutePath(), gameInstance);
            }
        });

        // --- Stop Button ---
        JButton stopButton = new JButton("Stop Script");
        stopButton.addActionListener(e -> {
            ScriptManager.stopScript();
            System.out.println("Script stopped manually.");
        });

        add(startButton);
        add(stopButton);

        setVisible(true);
    }
}