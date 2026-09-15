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
 * 1. Removes G28
 * 2. Where a line contains "G80 ... Z<value>", splits it into two lines:
 * G80
 * G00 Z<value>
 * 3. After each tool change add M00 with tool name
 * 4. After every tool change from drill to mill - add g92
 */
public class GCodeProcessor {

    public static final double DRILL_OFFSET = 0.0; // Z offset for drill tools
    public static final double MILL_OFFSET = 20.0; // Z offset for mill tools

    record Tool(int number, double offset, String description, double spindel, double feed) {
    }

    // ===================== CONFIGURATION =====================
    // Per-tool Z offset (absolute value from your tool table), in whatever
    // units your G-code file uses (mm/inch). Keys are tool numbers as they
    // appear after "T" in the file. The program only ever uses the
    // DIFFERENCE between two tools' values, so what matters is that these
    // are all measured from the same reference.
    //
    // Tool data lives in an external parameters file (see TOOLS_FILE_NAME)
    // next to where this program is run from, so it can be edited without
    // recompiling. If the file doesn't exist yet, a default one is created
    // automatically on first run.
    private static final String TOOLS_FILE_NAME = "tools.csv";
    private static Map<Integer, Tool> TOOLS;
    // ===========================================================

    // G28 anywhere in the line (word boundary, case-insensitive)
    private static final Pattern G28_PATTERN = Pattern.compile("(?i)\\bG28\\b");

    // "G80 Z" line
    private static final Pattern G80_PATTERN = Pattern.compile("(?i)\\bG80\\sZ");

    // Tool select word, e.g. "T3" or "T03"
    private static final Pattern T_WORD_PATTERN = Pattern.compile("(?i)\\bT(\\d{1,2})\\b");

    // Tool change execution word: M6 or M06
    private static final Pattern M06_PATTERN = Pattern.compile("(?i)\\bM0?6\\b");
    // X/Y/Z word, e.g. "Z25.0", "Z-3.5", or "Z.5" (no leading digit before the decimal point)
    private static final Pattern Z_WORD_PATTERN = Pattern.compile("(?i)\\bZ(-?(?:\\d*\\.\\d+|\\d+))\\b");
    private static final Pattern N_WORD_PATTERN = Pattern.compile("(?i)\\bN(-?\\d+(\\.\\d+)?)\\b");

    static int lineNumber = -1; // track line numbers for debugging

    // ================== SETUP CHANGE CONFIGURATION ==================
    // In the source file, a "Z999" move is used as a marker meaning
    // "pause here for a setup change" rather than a real position.
    private static final double SETUP_CHANGE_Z_MARKER = 999.0;
    // Clearance added above the last real Z position to compute a safe
    // retract height for the setup-change stop.
    private static final double SETUP_CHANGE_CLEARANCE = 80.0;
    // The computed retract height is rounded up to the nearest multiple
    // of this value, to give a clean, round number.
    private static final double SETUP_CHANGE_ROUNDING = 5.0;
    // ==================================================================

    /**
     * Entry point. Loads the tool table, then repeatedly prompts the user
     * to select an input G-code file and processes it, until the file
     * chooser is dismissed without a selection. Errors loading the tool
     * table or processing a given file are reported via a dialog rather
     * than crashing the program.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        try {
            TOOLS = loadTools();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to load tool table (" + TOOLS_FILE_NAME + "):\n" + e.getMessage(),
                    "GCode Process - Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File inputFile;
        while ((inputFile = getInputPath()) != null) {
            String inputPath = inputFile.getAbsolutePath();
            String outputPath = inputPath.replaceAll("\\.txt$", "_processed.txt");
            try {
                process(inputPath, outputPath);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error processing file:\n" + e.getMessage(),
                        "GCode Process - Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        System.out.println("No input file selected. Exiting.");
    }

    /**
     * Loads the tool table from {@link #TOOLS_FILE_NAME} (in the current
     * working directory). If the file doesn't exist yet, a default one is
     * written out first so the program works out of the box and the table
     * can then be edited by hand (e.g. in Notepad or Excel) without needing
     * to recompile.
     *
     * File format: one tool per line, comma-separated:
     * number,offset,description,spindel,feed
     * Blank lines and lines starting with '#' are ignored.
     *
     * @return the tool table, keyed by tool number
     * @throws IOException if the file can't be read or written, or a line
     *                      is malformed
     */
    private static Map<Integer, Tool> loadTools() throws IOException {
        Path toolsPath = Paths.get(TOOLS_FILE_NAME).toAbsolutePath();
        if (!Files.exists(toolsPath)) {
            writeDefaultToolsFile(toolsPath);
        }

        Map<Integer, Tool> tools = new HashMap<>();
        for (String rawLine : Files.readAllLines(toolsPath)) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; // blank line or comment
            }
            String[] parts = line.split(",", -1);
            if (parts.length != 5) {
                throw new IOException("Invalid line in " + toolsPath
                        + " (expected number,offset,description,spindel,feed): \"" + rawLine + "\"");
            }
            try {
                int number = Integer.parseInt(parts[0].trim());
                double offset = Double.parseDouble(parts[1].trim());
                String description = parts[2].trim();
                double spindel = Double.parseDouble(parts[3].trim());
                double feed = Double.parseDouble(parts[4].trim());
                tools.put(number, new Tool(number, offset, description, spindel, feed));
            } catch (NumberFormatException e) {
                throw new IOException("Invalid number in " + toolsPath + ": \"" + rawLine + "\"", e);
            }
        }
        return tools;
    }

    /**
     * Writes the built-in default tool table to disk, e.g. on first run.
     *
     * @param toolsPath path to write the default tool table file to
     * @throws IOException if the file can't be written
     */
    private static void writeDefaultToolsFile(Path toolsPath) throws IOException {
        List<String> defaultLines = List.of(
                "# GCodeProcessor tool table - edit as needed",
                "# number,offset,description,spindel,feed",
                "1," + DRILL_OFFSET + ",Drill 4.2mm,220,1500",
                "2," + DRILL_OFFSET + ",Drill 5.0mm,180,1500",
                "3," + DRILL_OFFSET + ",Drill 6.0mm,140,1500",
                "4," + DRILL_OFFSET + ",Drill 8.0mm,120,1500",
                "5," + MILL_OFFSET + ",MILL 4.0mm 2F,350,1500",
                "6," + MILL_OFFSET + ",MILL 6mm 2F,300,1000");
        Files.write(toolsPath, defaultLines);
    }

    /**
     * Opens a file chooser (starting in the user's Downloads folder) so the
     * user can pick a G-code file to process.
     *
     * @return the selected file, or {@code null} if the user canceled
     */
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

    /**
     * Reads the G-code file at {@code inputPath}, applies the
     * transformations described in the class Javadoc (removing G28,
     * splitting "G80 Z" lines, annotating tool changes with G92/M00,
     * inserting setup-change stops, etc.), and writes the result to
     * {@code outputPath}. Shows a summary dialog when done.
     *
     * @param inputPath  path to the source G-code file
     * @param outputPath path to write the processed G-code file to
     * @throws IOException           if the input can't be read or the output can't be written
     * @throws IllegalStateException if an M06 tool change is encountered with no known
     *                                current tool (missing or unrecognized T-word)
     */
    public static void process(String inputPath, String outputPath) throws IOException {
        lineNumber = -1; // reset line-number tracking for each file processed

        List<String> inputLines = Files.readAllLines(Paths.get(inputPath));
        List<String> output = new ArrayList<>();

        Tool firstTool = null;
        Tool nextTool = null;
        double lastZ = 0.0; // last Z value seen, for G92 calculation
        int lastN = -1;
        int setupNum = 1;

        for (String rawLine : inputLines) {
            String line = rawLine;

            // remove Nxxx - line number - if present, and track the last one seen
            Matcher nMatch = N_WORD_PATTERN.matcher(line);
            if (nMatch.find()) {
                lastN = Integer.parseInt(nMatch.group(1));
                if (lineNumber == -1) {
                    lineNumber = lastN;
                }
                line = line.substring(nMatch.end()).trim(); // remove N word from line
            }

            // --- Split "G80 ... Zxx" into "G80" + "G00 Zxx" ---
            Matcher g80 = G80_PATTERN.matcher(line);
            if (g80.find()) {
                output.add(str(line.substring(0, g80.start() + 3))); // G80
                line = "G00 " + line.substring(g80.start() + 4);
            }

            // Remove G28 and everything after it to end of line ---
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
                nextTool = TOOLS.get(Integer.parseInt(tMatch.group(1)));
            }

            // --- Track the most recent Z value ---
            Matcher zMatch = Z_WORD_PATTERN.matcher(line);
            if (zMatch.find()) {
                double nextZ = Double.parseDouble(zMatch.group(1));
                if (nextZ == SETUP_CHANGE_Z_MARKER) { // a setup change
                    setupNum++;
                    nextZ = Math.ceil((lastZ + SETUP_CHANGE_CLEARANCE) / SETUP_CHANGE_ROUNDING)
                            * SETUP_CHANGE_ROUNDING;
                    output.add(str("G00 Z" + formatNumber(nextZ)));
                    output.add(str("M00 (Setup Change - Setup " + setupNum + ")"));
                    lastZ = nextZ;
                    continue; // skip the rest of this line, since it's a setup change
                }
                lastZ = nextZ;
            }

            output.add(str(line));
            // Tool change execution (M6) - add M00 with tool description and G92 if needed
            if (M06_PATTERN.matcher(line).find()) {
                if (nextTool == null) {
                    throw new IllegalStateException(
                            "Unknown or missing tool at line: \"" + rawLine.trim()
                                    + "\". Check that a valid T-word (defined in " + TOOLS_FILE_NAME
                                    + ") precedes this M06.");
                }
                double offset = 0.0;
                if (firstTool == null) {
                    firstTool = nextTool;
                    output.add(str("G92.1 (reset G92 offsets)"));
                    output.add(str(
                            "M00 (  Make SURE that the tool tip is set to the correct height for the first move!)"));
                } else {
                    offset = nextTool.offset() - firstTool.offset();
                    if (offset != 0.0) {
                        output.add(str("G92 Z" + formatNumber(lastZ + offset)));
                    }
                }
                String offsetStr = (offset < 0 ? " " + offset + "mm BELOW current"
                        : offset > 0 ? " " + offset + "mm ABOVE current" : " SAME as current");
                output.add(str("M00 (Tool Change - " + nextTool.description() +
                        "      Spindel " + nextTool.spindel() +
                        "      Feed " + nextTool.feed() +
                        "      Height " + offsetStr + ")"));
            }
        }

        Files.write(Paths.get(outputPath), output);
        String msg = "Processed " + inputLines.size() + " lines -> "
                + output.size() + " lines. Wrote: " + outputPath;
        JOptionPane.showMessageDialog(null, msg, "GCode Process", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Prefixes {@code line} with the next sequential N line number, if line
     * numbering is active for this file (i.e. the source file itself used
     * N-numbers). Otherwise returns the line unchanged.
     *
     * @param line the G-code line (without an N-word) to emit
     * @return the line, prefixed with "N&lt;number&gt; " if numbering is active
     */
    private static String str(String line) {
        if (lineNumber > 0) {
            return "N" + lineNumber++ + " " + line;
        }
        return line;
    }

    /**
     * Formats a double as a clean decimal string, e.g. 0.1500 -> "0.15".
     *
     * @param value the number to format
     * @return the formatted decimal string, always containing a decimal point
     */
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
