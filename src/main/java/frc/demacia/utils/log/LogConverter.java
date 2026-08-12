package frc.demacia.utils.log;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class LogConverter {

    private static String lastCreatedFile = null;

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Log to Excel Converter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 450);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridBagLayout());
            
            JButton openButton = new JButton("Select .wpilog File");
            openButton.setFont(new Font("Arial", Font.BOLD, 14));
            
            JButton openExcelButton = new JButton("Open Last CSV");
            openExcelButton.setFont(new Font("Arial", Font.BOLD, 14));
            openExcelButton.setEnabled(false);
            
            JTextArea statusArea = new JTextArea(15, 50);
            statusArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(statusArea);

            openButton.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
                chooser.setFileFilter(new FileNameExtensionFilter("WPILib Log (.wpilog)", "wpilog"));
                int choice = chooser.showOpenDialog(frame);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    openExcelButton.setEnabled(false);
                    new Thread(() -> processFile(file, statusArea, openExcelButton)).start();
                }
            });

            openExcelButton.addActionListener(e -> {
                if (lastCreatedFile != null) {
                    try {
                        Desktop.getDesktop().open(new File(lastCreatedFile));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                    }
                }
            });

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(openButton, gbc);

            gbc.gridy = 1;
            panel.add(openExcelButton, gbc);

            gbc.gridy = 2;
            gbc.weightx = 1.0; gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            panel.add(scrollPane, gbc);

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    private static void processFile(File inputFile, JTextArea status, JButton excelButton) {
        SwingUtilities.invokeLater(() -> status.append("Loading: " + inputFile.getName() + "...\n"));
        LogReader.loadFile(inputFile.getAbsolutePath());
        
        SwingUtilities.invokeLater(() -> {
            status.append("Parsed " + LogReader.entries.size() + " IDs. Generating CSV...\n");
        });

        String tempPath = inputFile.getAbsolutePath().replace(".wpilog", ".csv");
        if (!tempPath.endsWith(".csv")) tempPath += ".csv";
        final String finalPath = tempPath;

        try {
            exportToCSV(finalPath);
            SwingUtilities.invokeLater(() -> {
                status.append("SUCCESS! Saved to: " + finalPath + "\n");
                lastCreatedFile = finalPath;
                excelButton.setEnabled(true);
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> status.append("Error: " + e.getMessage() + "\n"));
            e.printStackTrace();
        }
    }

    private static void exportToCSV(String filePath) throws IOException {
        TreeMap<Long, Map<String, Double>> rawRows = new TreeMap<>();
        Set<String> allHeaders = new TreeSet<>();

        for (List<LogReader.Group> groupList : LogReader.entries.values()) {
             for (LogReader.Group group : groupList) {
                 for (Map.Entry<String, List<LogReader.DataPoint>> entry : group.data.entrySet()) {
                     String colName;
                     if (group.groupName != null && !group.groupName.isEmpty()) {
                         colName = group.groupName + ": " + entry.getKey();
                     } else {
                         colName = entry.getKey();
                     }
                     allHeaders.add(colName);

                     for (LogReader.DataPoint point : entry.getValue()) {
                         rawRows.computeIfAbsent(point.timestamp, k -> new HashMap<>())
                             .put(colName, point.values);
                     }
                 }
             }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Timestamp (s)");
            for (String header : allHeaders) writer.write("," + header);
            writer.newLine();

            Map<String, Double> lastValues = new HashMap<>();

            for (Map.Entry<Long, Map<String, Double>> row : rawRows.entrySet()) {
                double timeSec = row.getKey() / 1_000_000.0;
                writer.write(String.format("%.4f", timeSec));
                
                lastValues.putAll(row.getValue());

                for (String header : allHeaders) {
                    Double val = lastValues.get(header);
                    if (val != null) {
                        writer.write("," + val);
                    } else {
                        writer.write(",");
                    }
                }
                writer.newLine();
            }
        }
    }
}