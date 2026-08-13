package frc.demacia.utils.log;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LogFileChooser {

    /**
     * Opens a native OS file dialog to select a .wpilog file.
     * Automatically sets the starting directory to where FRC tools download log files.
     * * @return Absolute path of the selected .wpilog file
     * @throws IOException If the user cancels or if running in a headless environment
     */
    public static String selectFileFromComputer() throws IOException {
        if (GraphicsEnvironment.isHeadless()) {
            throw new UnsupportedOperationException("Cannot open GUI file picker in a headless environment (e.g., on the roboRIO).");
        }

        File initialDirectory = getBestLogDirectory();
        String initialPath = initialDirectory != null ? initialDirectory.getAbsolutePath() : "";
        String escapedPath = initialPath.replace("\\", "\\\\");

        String psScript = "$showDialog = New-Object System.Windows.Forms.OpenFileDialog;" +
                          "$showDialog.InitialDirectory = '" + escapedPath + "';" +
                          "$showDialog.Filter = 'WPILib Log Files (*.wpilog)|*.wpilog';" +
                          "$showDialog.Title = 'Select WPILog File';" +
                          "$result = $showDialog.ShowDialog();" +
                          "if ($result -eq 'OK') { Write-Output $showDialog.FileName }";

        try {
            ProcessBuilder pb = new ProcessBuilder(
                "powershell", 
                "-NoProfile", 
                "-Command", 
                "[void][System.Reflection.Assembly]::LoadWithPartialName('System.Windows.Forms'); " + psScript
            );
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String selectedPath = reader.readLine();
            process.waitFor();

            if (selectedPath != null && !selectedPath.trim().isEmpty()) {
                return selectedPath.trim();
            } else {
                throw new IOException("Log file selection was cancelled by the user.");
            }
        } catch (Exception e) {
            return fallbackFileChooser(initialDirectory);
        }
    }

    private static String fallbackFileChooser(File initialDirectory) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select WPILog File");
        fileChooser.setMultiSelectionEnabled(false);

        FileNameExtensionFilter filter = new FileNameExtensionFilter("WPILib Log Files (*.wpilog)", "wpilog");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (initialDirectory != null && initialDirectory.exists()) {
            fileChooser.setCurrentDirectory(initialDirectory);
        }

        fileChooser.setPreferredSize(new Dimension(1000, 650));

        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            throw new IOException("Log file selection was cancelled by the user.");
        }
    }

    /**
     * Finds the best default folder where logs are usually downloaded.
     */
    private static File getBestLogDirectory() {
        String userHome = System.getProperty("user.home");
        String publicDir = System.getenv("PUBLIC");

        File[] potentialDirs = new File[] {
            new File(publicDir != null ? publicDir + "/Documents/FRC/Log Files" : "C:/Users/Public/Documents/FRC/Log Files"),
            new File("logs/"),
            new File(userHome + "/Documents"),
            new File(userHome + "/Downloads"),
        };

        for (File dir : potentialDirs) {
            if (dir.exists() && dir.isDirectory()) {
                File[] wpilogFiles = dir.listFiles((d, name) -> name.endsWith(".wpilog"));
                if (wpilogFiles != null && wpilogFiles.length > 0) {
                    return dir;
                }
            }
        }

        for (File dir : potentialDirs) {
            if (dir.exists() && dir.isDirectory()) {
                return dir;
            }
        }

        return null;
    }
}