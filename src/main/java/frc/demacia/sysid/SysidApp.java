package frc.demacia.sysid;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class SysidApp {
    public static boolean[] kFlags = {true, true, true, false, false, false};

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        SysidMain app = new SysidMain();
        app.show();
    }
}

class SysidMain implements Consumer<File> {
    JFrame frame = new JFrame("SysID - Pro Version");
    
    FileChooserPanel fileChooser = new FileChooserPanel(this);
    DefaultListModel<MotorData> listModel = new DefaultListModel<>();
    JList<MotorData> motorList = new JList<>(listModel);
    SysidResultPanel result = new SysidResultPanel(this);
    
    JTextArea msgArea = new JTextArea();
    JScrollPane msgPane = new JScrollPane(msgArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    
    Map<String, LogReader.BucketResult> analysisResults;
    File currentFile;

    private static SysidMain sysid = null;

    public SysidMain() {
        sysid = this;
        frame.setSize(1280, 850); 
        frame.setLocationRelativeTo(null); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        var pane = frame.getContentPane();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        motorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        motorList.setBorder(BorderFactory.createTitledBorder("Select Motor Data"));
        JScrollPane listScroll = new JScrollPane(motorList);
        
        motorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                MotorData selected = motorList.getSelectedValue();
                if (selected != null) {
                    result.updateDisplay(selected);
                }
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 5, 10);
        pane.add(fileChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 5, 5);
        pane.add(listScroll, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 0.7; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 5, 10);
        pane.add(result, gbc);

        msgArea.setEditable(false);
        msgArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        msgPane.setBorder(BorderFactory.createTitledBorder("Console"));
        msgPane.setPreferredSize(new Dimension(100, 150));

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0; gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 10, 10);
        pane.add(msgPane, gbc);
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void msg(String message) {
        if(sysid != null) {
            SwingUtilities.invokeLater(() -> {
                sysid.msgArea.append(message + "\n");
                sysid.msgArea.setCaretPosition(sysid.msgArea.getDocument().getLength());
            });
        }
    }

    @Override
    public void accept(File file) {
        msg("File selected: " + file.getName());
        this.currentFile = file;
        performFullAnalysis();
    }

    public void performFullAnalysis() {
        if (currentFile == null) return;
        
        MotorData currentlySelected = motorList.getSelectedValue();
        String selectedName = (currentlySelected != null) ? currentlySelected.fullName : null;

        try {
            msg("Starting analysis...");
            MotorData.motors.clear();
            listModel.clear();
            
            analysisResults = LogReader.getResult(currentFile.getAbsolutePath());
            
            msg("Analysis complete. Found " + analysisResults.size() + " motor groups.");
            
            List<String> sortedNames = new ArrayList<>(analysisResults.keySet());
            Collections.sort(sortedNames);

            int indexToSelect = 0;
            int i = 0;
            
            for(String groupName : sortedNames) {
                MotorData motorData = new MotorData();
                motorData.fullName = groupName;
                
                String[] parts = groupName.split(":");
                motorData.displayName = parts.length > 0 ? parts[0] : groupName;
                
                motorData.bucketResult = analysisResults.get(groupName);
                MotorData.motors.add(motorData);
                listModel.addElement(motorData);
                
                if(selectedName != null && selectedName.equals(groupName)) {
                    indexToSelect = i;
                }
                i++;
            }
            
            if (!listModel.isEmpty()) {
                motorList.setSelectedIndex(indexToSelect);
            } else {
                msg("No valid motor data found in file.");
            }
            
        } catch (Exception e) {
            msg("Error during analysis: " + e.getMessage());
            e.printStackTrace();
            fileChooser.field.setText("Error loading file");
        }
    }
}

class FileChooserPanel extends JPanel implements ActionListener {
    JButton button;
    JTextField field;
    JFileChooser chooser;
    Consumer<File> consumer;

    public FileChooserPanel(Consumer<File> consumer) {
        super(new BorderLayout(5, 0));
        this.consumer = consumer;
        
        button = new JButton("Open WPILOG");
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        field = new JTextField();
        field.setEditable(false);
        field.setBackground(Color.WHITE);
        
        button.addActionListener(this);
        chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setFileFilter(new FileNameExtensionFilter("WPILOG Files", "wpilog"));
        
        add(button, BorderLayout.WEST);
        add(field, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int res = chooser.showOpenDialog(this);
        if(res == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            field.setText(file.getAbsolutePath());
            if(consumer != null) {
                consumer.accept(file);
            }
        }
    }
}

class SysidResultPanel extends JPanel {
    JCheckBox[] checkBoxes;
    JLabel[] valueLabels;
    JButton recalcButton;
    
    JLabel countLabel = new JLabel("0 / 0");
    JLabel r2Label = new JLabel("0.0000");
    JLabel avgErrLabel = new JLabel("0.0000 V");
    JLabel maxErrLabel = new JLabel("0.0000 V");
    
    JLabel kpLabel = new JLabel("0.0000");
    JComboBox<String> kpTypeCombo;
    
    SysidMain app;
    String[] names = {"kS", "kV", "kA", "kG", "kCos", "kV²"};

    public SysidResultPanel(SysidMain app) {
        super(new GridBagLayout());
        this.app = app;
        setBorder(BorderFactory.createTitledBorder("Analysis Results"));

        checkBoxes = new JCheckBox[6];
        valueLabels = new JLabel[6];
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel pLabel = new JLabel("Parameter");
        pLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(pLabel, gbc);
        
        gbc.gridx = 1;
        JLabel vLabel = new JLabel("Value");
        vLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(vLabel, gbc);

        for(int i = 0; i < 6; i++) {
            gbc.gridx = 0; gbc.gridy = i + 1;
            checkBoxes[i] = new JCheckBox(names[i]);
            checkBoxes[i].setSelected(SysidApp.kFlags[i]);
            int idx = i;
            checkBoxes[i].addActionListener(e -> {
                SysidApp.kFlags[idx] = checkBoxes[idx].isSelected();
                app.performFullAnalysis();
            });
            add(checkBoxes[i], gbc);
            
            gbc.gridx = 1;
            valueLabels[i] = new JLabel("0.0000");
            valueLabels[i].setFont(new Font("Monospaced", Font.BOLD, 14));
            valueLabels[i].setForeground(new Color(0, 100, 0));
            add(valueLabels[i], gbc);
        }

        addSeparator(8, gbc);

        addStatRow("Points:", countLabel, 9, gbc);
        addStatRow("R²:", r2Label, 10, gbc);
        
        addStatRow("Avg Error (Volts):", avgErrLabel, 11, gbc);
        addStatRow("Max Error (Volts):", maxErrLabel, 12, gbc);
        
        addSeparator(13, gbc);
        
        gbc.gridx = 0; gbc.gridy = 14;
        JPanel kpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        kpPanel.add(new JLabel("Suggested kP: "));
        kpTypeCombo = new JComboBox<>(new String[]{"Position", "Velocity"});
        kpTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        kpTypeCombo.addActionListener(e -> app.performFullAnalysis());
        kpPanel.add(kpTypeCombo);
        add(kpPanel, gbc);

        gbc.gridx = 1;
        kpLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
        kpLabel.setForeground(Color.BLUE);
        add(kpLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 15; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 0, 5, 0);
        recalcButton = new JButton("Recalculate Fit");
        recalcButton.addActionListener(e -> app.performFullAnalysis());
        add(recalcButton, gbc);
        
        gbc.gridy = 16; gbc.weighty = 1.0;
        add(new JLabel(), gbc);
    }
    
    private void addSeparator(int y, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(new JSeparator(), gbc);
        gbc.gridwidth = 1; 
    }

    private void addStatRow(String name, JLabel label, int y, GridBagConstraints gbc) {
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel(name), gbc);
        gbc.gridx = 1;
        label.setFont(new Font("Monospaced", Font.PLAIN, 12));
        add(label, gbc);
    }
    
    public void updateDisplay(MotorData motorData) {
        if (motorData == null || motorData.bucketResult == null) {
            clearDisplay();
            return;
        }
        
        LogReader.BucketResult r = motorData.bucketResult;
        
        valueLabels[0].setText(String.format("%.5f", r.ks));
        valueLabels[1].setText(String.format("%.5f", r.kv));
        valueLabels[2].setText(String.format("%.5f", r.ka));
        valueLabels[3].setText(String.format("%.5f", r.kg));
        valueLabels[4].setText(String.format("%.5f", r.ksin));
        valueLabels[5].setText(String.format("%.5f", r.kv2));
        
        countLabel.setText(r.points + " / " + r.rawPoints);
        r2Label.setText(String.format("%.4f", r.rSquared));
        
        avgErrLabel.setText(String.format("%.4f V", r.avgError));
        maxErrLabel.setText(String.format("%.4f V", r.maxError));
        
        double kp = 0;
        double kaSafe = Math.max(Math.abs(r.ka), 0.0001);
        
        String type = (String) kpTypeCombo.getSelectedItem();
        
        if ("Position".equals(type)) {
            kp = (2.0 * r.kv) / kaSafe; 
        } else {
            kp = r.kv / kaSafe;
        }
        
        kpLabel.setText(String.format("%.4f", kp));
        
        SysidMain.msg("Results for: " + motorData.displayName);
    }

    private void clearDisplay() {
        for(JLabel l : valueLabels) l.setText("0.0000");
        countLabel.setText("0 / 0");
        r2Label.setText("0.0000");
        avgErrLabel.setText("0.0000 V");
        maxErrLabel.setText("0.0000 V");
        kpLabel.setText("0.0000");
    }
}

class MotorData {
    static List<MotorData> motors = new ArrayList<>();
    String fullName = "Motor";
    String displayName = "Motor";
    LogReader.BucketResult bucketResult;

    @Override
    public String toString() {
        return displayName;
    }
}