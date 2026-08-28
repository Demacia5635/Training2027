package frc.robot.vision;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class GrayCanny {
// This class detects a yellow ball in an image using OpenCV
// The file is in the GIT folder
// using openCV 4.14.0 - with the dll in GIT folder

public static void main(String[] args) {
        String fileName = "detectBallExercise.jpg";
        // Load the OpenCV native library
        System.load(System.getProperty("user.dir") + "\\opencv_java4140.dll");

        // Read the image and resize it to a smaller size for faster processing
        Mat orig = Imgcodecs.imread(fileName);
        Mat img = new Mat();
        Imgproc.resize(orig, img,new Size(640,480));

        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        Mat canny = new Mat();
        Imgproc.Canny(gray, canny, 100, 200);
        Mat blur = new Mat();
        Imgproc.GaussianBlur(img, blur, new Size(15,15),3);
        Mat median = new Mat();
        Imgproc.medianBlur(img, median,7);

        HighGui.imshow("original", img);
        HighGui.imshow("gray", gray);
        HighGui.imshow("canny", canny);
        HighGui.imshow("blur", blur);
        HighGui.imshow("median", median);
        // Wait for a key press and exit
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
        System.exit(1);
    }
}
