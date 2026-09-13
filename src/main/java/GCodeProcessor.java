import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
 * TODO - For G03/G02 arcs - add Y for Xxx.xx Zxx.xx if missing, to avoid "G03/G02 with no Y" errors on some machines.
 * Also - add X/Y for full circle arcs (G02/G03 with no X/Y) to avoid "G02/G03 with no X/Y" errors on some machines.
 * Remember last X and Y values
 *
 */
public class GCodeProcessor {

    public static final double DRILL_OFFSET = 0.0; // Z offset for drill tools
    public static final double MILL_OFFSET = 20.0; // Z offset for drill tools

    record Tool(int number, double offset, String description, double spindel, double feed) {}

    // ===================== CONFIGURATION: EDIT THESE =====================
    // Per-tool Z offset (absolute value from your tool table), in whatever
    // units your G-code file uses (mm/inch). Keys are tool numbers as they
    // appear after "T" in the file (T1..T6). The program only ever uses the
    // DIFFERENCE between two tools' values, so what matters is that these
    // are all measured from the same reference.
    private static final Map<Integer, Tool> TOOLS = new HashMap<>();
    static {
        TOOLS.put(1, new Tool(1, DRILL_OFFSET, "Drill 4.2mm",220, 1500));
        TOOLS.put(2, new Tool(2, DRILL_OFFSET, "Drill 5.0mm", 180, 1500));
        TOOLS.put(3, new Tool(3, DRILL_OFFSET, "Drill 6.0mm", 140, 1500));
        TOOLS.put(4, new Tool(4, DRILL_OFFSET, "Drill 8.0mm", 120, 1500));
        TOOLS.put(5, new Tool(5, MILL_OFFSET, "MILL 4.0mm 2F", 350, 1500));
        TOOLS.put(6, new Tool(6, MILL_OFFSET, "MILL 6mm 2F", 300, 1000));
    }
    // =======================================================================

    // G28 anywhere in the line (word boundary, case-insensitive)
    private static final Pattern G28_PATTERN =
            Pattern.compile("(?i)\\bG28\\b");

    // G80 line that also carries a Z word, e.g. "G80 Z25.0"
    private static final Pattern G80_PATTERN =
            Pattern.compile("(?i)\\bG80\\sZ");
   
    // Tool select word, e.g. "T3" or "T03"
    private static final Pattern T_WORD_PATTERN =
            Pattern.compile("(?i)\\bT(\\d{1,2})\\b");

    // Tool change execution word: M6 or M06
    private static final Pattern M06_PATTERN =
            Pattern.compile("(?i)\\bM0?6\\b");
    // X/Y/Z word, e.g. "Z25.0" or "Z-3.5"
    private static final Pattern Z_WORD_PATTERN =
            Pattern.compile("(?i)\\bZ(-?\\d+(\\.\\d+)?)\\b");
    private static final Pattern X_WORD_PATTERN =
            Pattern.compile("(?i)\\bX(-?\\d+(\\.\\d+)?)\\b");
    private static final Pattern Y_WORD_PATTERN =
            Pattern.compile("(?i)\\bY(-?\\d+(\\.\\d+)?)\\b");


    public static void main(String[] args) throws IOException {
//        test1();
        File inputFile = getInputPath();
        if(inputFile == null) {
            System.out.println("No input file selected. Exiting.");
            return;
        }
        String inputPath = inputFile.getAbsolutePath();
        String outputPath = inputPath.replaceAll("\\.txt$", "_processed.txt");
        process(inputPath,outputPath);
    }

    private static void test1() {
        String line = "N6 G90 G00 X103.389 Y-91.214";
        Matcher ym = Y_WORD_PATTERN.matcher(line);
        System.out.println(" find = " + ym.find());
        System.out.println("ym = " + ym);
        System.out.println("end = " + ym.end());
        System.out.println("count = " + ym.groupCount());
        System.out.println("region start = " + ym.regionStart());
        System.out.println("regionEnd = " + ym.regionEnd());
        double y = Double.parseDouble(ym.group(1));
        System.out.println("y = " + y);
        System.out.println("formated = " + formatNumber(y));
        System.exit(0);
    }

    static File getInputPath() {
        JFileChooser fileChooser = new JFileChooser();
        Path downloadsPath = Paths.get(System.getProperty("user.home"), "Downloads");
        fileChooser.setCurrentDirectory(downloadsPath.toFile());
        fileChooser.setFileFilter(new FileNameExtensionFilter("txt files", "txt"));

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

    public static void process(String inputPath, String outputPath) throws IOException {
        List<String> inputLines = Files.readAllLines(Paths.get(inputPath));
        List<String> output = new ArrayList<>();

        Integer pendingTool = null; // tool most recently named by a T word
        Integer activeTool = null;  // tool currently loaded; null until first M06
        double lastZ = 0.0; // last Z value seen, for G92 calculation
        double lastX = 0.0; // last X value seen, for G92 calculation
        double lastY = 0.0; // last Y value seen, for G92 calculation

        for (String rawLine : inputLines) {
            String line = rawLine;

            // --- 1. Remove G28 and everything after it to end of line ---
            Matcher g28m = G28_PATTERN.matcher(line);
            if (g28m.find()) {
                String remainder = line.substring(0, g28m.start()).trim();
                if (remainder.isEmpty()) {
                    continue; // nothing meaningful before G28 -> drop whole line
                }
                line = remainder; // keep processing what's left of the line
            }

            // --- Track the most recently selected tool number ---
            Matcher tMatch = T_WORD_PATTERN.matcher(line);
            if (tMatch.find()) {
                pendingTool = Integer.parseInt(tMatch.group(1));
            }
            // --- Track the most recent X/Y/Z value ---
            Matcher zMatch = Z_WORD_PATTERN.matcher(line);
            if (zMatch.find()) {
                lastZ = Double.parseDouble(zMatch.group(1));
            }
            Matcher xMatch = X_WORD_PATTERN.matcher(line);
            if (xMatch.find()) {
                lastX = Double.parseDouble(xMatch.group(1));
            }
            Matcher yMatch = Y_WORD_PATTERN.matcher(line);
            if (yMatch.find()) {
                try {
                    lastY = Double.parseDouble(yMatch.group(1));
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing Y value: " + line + 
                        "\n match = " + yMatch.group(1) + 
                        "\n " + yMatch.toString());
                }
                lastY = Double.parseDouble(yMatch.group(1));
            }

            // --- 2. Split "G80 ... Zxx" into "G80" + "G00 Zxx" ---
            Matcher g80 = G80_PATTERN.matcher(line);
            if (g80.find()) {
                output.add(line.substring(0, g80.start() + 3)); // G80
                output.add("G00 " + line.substring(g80.start() + 3)); // G00
                continue; // skip the rest of the loop; we've already added the split lines
            }

            // --- default: keep the line as-is ---
            output.add(line);

            // --- 3 & 4. On a tool-change execution (M06), insert G92 (if not
            //     the first change) then M00 ---
            if (M06_PATTERN.matcher(line).find()) {
                var newT = TOOLS.get(pendingTool);
                var oldT = activeTool == null ? newT : TOOLS.get(activeTool);
                double offset = 0;
                if(oldT.offset() != newT.offset()) {
                    output.add("G92 Z" + formatNumber(newT.offset() + (lastZ - oldT.offset())));
                    offset = newT.offset() - oldT.offset();
                }
                Tool tool = TOOLS.get(pendingTool);
                output.add("M00 (Toole Change - " + tool.description() + 
                        "         Spindel " + tool.spindel() + 
                        "         Feed " + tool.feed() + 
                        "         Z Offset " + offset + "mm)");
                if (pendingTool != null) {
                    activeTool = pendingTool;
                }
            }
        }

        Files.write(Paths.get(outputPath), output);
        String msg = "Processed " + inputLines.size() + " lines -> "
                + output.size() + " lines. Wrote: " + outputPath + 
                " lastX/Y/Z:"+ lastX + "/" + lastY + "/" + lastZ;
        JOptionPane.showMessageDialog(null, msg, "GCode Process", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Inserts G92 Z<diff>, where diff = offset(newTool) - offset(oldTool). */
    private static void addG92ForToolChange(List<String> output, Integer oldTool, Integer newTool, double lastZ) {
        var oldT = TOOLS.get(oldTool);
        var newT = TOOLS.get(newTool);
        if(oldT.offset() != newT.offset()) {
            output.add("G92 Z" + formatNumber(newT.offset() + (lastZ - oldT.offset())));
        }
    }

    /** Formats a double as a clean decimal string, e.g. 0.1500 -> "0.15". */
    private static String formatNumber(double value) {
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
