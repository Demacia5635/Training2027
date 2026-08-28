package frc.robot.vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

// This class detects a yellow ball in an image using OpenCV
// The file is in the GIT folder
// need to install OpenCV 4 - and set the path to the native library in the code
// - https://opencv.org/releases/
// - extract to a base folder, for example D:\Projects\OpenCV\opencv
// - set the path to the native library in the code below

public class DetectBall {
public static void main(String[] args) {
        String fileName = "detectBallExercise.jpg";
        // Load the OpenCV native library
        System.load("D:\\Projects\\OpenCV\\opencv\\build\\java\\x64\\opencv_java4140.dll");

        // Read the image and resize it to a smaller size for faster processing
        Mat img = Imgcodecs.imread(fileName);
        Mat resized = new Mat();
        Imgproc.resize(img, resized,new Size(640,480));
        // copy the original to show the contours on it later
        Mat out = resized.clone();
        // Show the original image
        HighGui.imshow("original", resized);
        // Apply Gaussian blur to reduce noise and improve contour detection
        Mat blur = new Mat();
        Imgproc.GaussianBlur(resized, blur, new Size(15,15), 2);
        HighGui.imshow("blur", blur);

        // Convert the image to HSV color space and threshold it to isolate the yellow color of the ball
        Mat hsv = new Mat();
        Imgproc.cvtColor(blur, hsv, Imgproc.COLOR_BGR2HSV);
        Mat mask = new Mat();
        Scalar min = new Scalar(20, 100, 100);
        Scalar max = new Scalar(30, 255, 255);
        Core.inRange(hsv, min,max, mask);
        HighGui.imshow("mask", mask);

        // Apply morphological operations to remove noise and fill gaps in the mask
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat filterred = new Mat();
        Point anchor = new Point(-1,-1);
        Imgproc.erode(mask, filterred, kernel, anchor , 2);
        Imgproc.dilate(filterred, filterred, kernel, anchor, 4);
        Imgproc.erode(filterred, filterred, kernel, anchor , 4);
        HighGui.imshow("filtter", filterred);

        // Find contours in the filtered mask and draw them on the output image
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat(1  ,1 ,CvType.CV_32SC2);
        Imgproc.findContours(filterred, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        // Find the best contour based on area and aspect ratio, and draw a bounding rectangle and center point on the output image
        MatOfPoint best = null;
        Rect bestRect = null;
        double bestRatio = 0;
	    double bestSize = 0;
        for(int i = 0; i < contours.size(); i++) {
            MatOfPoint m = contours.get(i);
	        double area = Imgproc.contourArea(m);
            Rect rect = Imgproc.boundingRect(m);
            double r = (double)rect.height / rect.width;
            if(area > 200) {
                Imgproc.drawContours(out, contours, i, new Scalar(0,0,255),2);
                if(best == null || isBetter(r,area, bestRatio, bestSize)) {
                    best = m;
                    bestRect = rect;
		            bestSize = area;
                    bestRatio = r;
	            }
            }
        }
        // Draw the best contour and its bounding rectangle and center point on the output image
        if(bestRect != null) {
            Imgproc.rectangle(out, bestRect, new Scalar(0,255,0),3);
            Moments m = Imgproc.moments(best);
            int centerX = (int) (m.get_m10() / m.get_m00());
            int centerY = (int) (m.get_m01() / m.get_m00());
            Imgproc.circle(out, new Point(centerX, centerY), 5, new Scalar(0, 255, 0),  -1);
        }
        HighGui.imshow("out",out);

        // Wait for a key press and exit
        HighGui.waitKey(0);
        HighGui.destroyAllWindows();
        System.exit(1);
    }


    private static boolean isBetter(double r, double area, double bestRatio, double bestSize) {
        if(bestSize == 0) {
            return true;
        }
        double ratioDiff = Math.abs(r - 1);
        double bestRatioDiff = Math.abs(bestRatio - 1);
        if(ratioDiff < bestRatioDiff) {
            return true;
        } else if(ratioDiff == bestRatioDiff) {
            return area > bestSize;
        } else {
            return false;
        }
    }
}
