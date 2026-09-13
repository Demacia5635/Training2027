import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 * GCodeProcessor
 * ---------------
 * Reads a G-code (.nc / .tap / .txt) file and applies four transformations:
 *
 *   1. Removes G28 
 *   2. Where a line contains "G80 ... Z<value>", splits it into two lines:
 *        G80
 *        G00 Z<value>
 *   3. After each tool change add M00 with tool name
 *   4. After every tool change from drill to mill - add g92
 *
 */
public class GCodeProcessor1 {

    record CMD(char c, double value) {};
    ArrayList<Line> lines = new ArrayList<>();
    class Line {
        List<CMD> cmds = new ArrayList<>();
        String line = null;
        boolean isComment = false;
        String comment = null;
        int lineNumber = -1;

        Line(String line) {
            this.line = line;
            parseLine();
            lines.add(this);
            System.out.println("Line: " + this);
        }

        private void parseLine() {
            for(String s : line.trim().split(" ")) {
                if(s.isEmpty()) continue;
                char c = s.charAt(0);
                if(c == '(') {
                    isComment = true;
                    comment = s.substring(1, s.length() - 1).trim();
                    return;
                }
                if(c == 'N') {
                    lineNumber = Integer.parseInt(s.substring(1));
                    continue; // ignore line numbers
                }
                if(c == 'G' || c == 'M' || c == 'T') {
                    try {
                        int value = Integer.parseInt(s.substring(1));
                        cmds.add(new CMD(c, value));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format in line: " + line + "\n s " + s);
                        System.exit(1);
                    }
                    continue;
                }
                try {
                    double value = Double.parseDouble(s.substring(1));
                    cmds.add(new CMD(c, value));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in line: " + line + "\n s " + s);
                    System.exit(1);
                }
            }
        }
        @Override
        public String toString() {
            if(isComment) {
                return String.format("Line{line='%s', isComment=%s, comment='%s'}", line, isComment, comment);
            }
            return String.format("cmds=%s", cmds);
        }
    } 


    public static final double DRILL_OFFSET = 30.0; // Z offset for drill tools
    public static final double MILL_OFFSET = 50.0; // Z offset for drill tools

    record Tool(int number, double offset, String description) {}

    // ===================== CONFIGURATION: EDIT THESE =====================
    // Per-tool Z offset (absolute value from your tool table), in whatever
    // units your G-code file uses (mm/inch). Keys are tool numbers as they
    // appear after "T" in the file (T1..T6). The program only ever uses the
    // DIFFERENCE between two tools' values, so what matters is that these
    // are all measured from the same reference.
    private static final Map<Integer, Tool> TOOLS = new HashMap<>();
    static {
        TOOLS.put(1, new Tool(1, DRILL_OFFSET, "Drill 4.2mm"));
        TOOLS.put(2, new Tool(2, DRILL_OFFSET, "Drill 5.0mm"));
        TOOLS.put(3, new Tool(3, DRILL_OFFSET, "Drill 6.0mm"));
        TOOLS.put(4, new Tool(4, DRILL_OFFSET, "Drill 8.0mm"));
        TOOLS.put(5, new Tool(5, MILL_OFFSET, "MILL 4.0mm 2F"));
        TOOLS.put(6, new Tool(6, MILL_OFFSET, "MILL 6mm 2F"));
    }
    public static void main(String[] args) throws IOException {
        File inputFile = getInputPath();
        if(inputFile == null) {
            System.out.println("No input file selected. Exiting.");
            return;
        }
        String inputPath = inputFile.getAbsolutePath();
        String outputPath = inputPath.replaceAll("\\.txt$", "_processed.txt");
        var gc = new GCodeProcessor1();
        gc.process(inputPath, outputPath);
        for(Line l : gc.lines) {
            if(l.lineNumber == -1 || l.isComment) 
                System.out.println(l.line);
        }

    }

    static File getInputPath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("txt files", "txt"));
        Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads");
        fileChooser.setCurrentDirectory(downloadsPath.toFile());

        // Show the standard "Open" dialog window
        int response = fileChooser.showOpenDialog(null);

        // Check if the user selected a file and clicked "Open"
        if (response == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            return selectedFile;
        } else {
            System.out.println("File selection canceled.");
            return null;
        }
    }

    public void process(String inputPath, String outputPath) throws IOException {
        for(String s : Files.readAllLines(Paths.get(inputPath)))  { 
            new Line(s);
        }
        /*
        Files.write(Paths.get(outputPath), output);
        String msg = "Processed " + inputLines.size() + " lines -> "
                + output.size() + " lines. Wrote: " + outputPath;
        JOptionPane.showMessageDialog(null, msg, "GCode Process", JOptionPane.INFORMATION_MESSAGE);
        */
    }


    /** Formats a double as a clean decimal string, e.g. 0.1500 -> "0.15". */
    public static String formatNumber(double value) {
        BigDecimal bd = BigDecimal.valueOf(value)
                .setScale(4, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        String s = bd.toPlainString();
        if (!s.contains(".")) {
            s = s + ".0";
        }
        return s;
    }
}

