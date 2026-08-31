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

public class DetectBall {

    public static void main(String[] args) {
        String imageName = "detectBallExercise.jpg";
        System.load(System.getProperty("user.dir") + "\\opencv_java4140.dll");

        // Original:
        Mat img = Imgcodecs.imread(imageName);
        Mat resizedImg = new Mat();
        Imgproc.resize(img, resizedImg, new Size(640, 480));
        Mat out = resizedImg.clone();

        // show original image
        HighGui.imshow("Original image", resizedImg);

        // Blur:
        Mat blurredImg = new Mat();
        Imgproc.GaussianBlur(resizedImg, blurredImg, new Size(15, 15), 2);
        // show the blurred image
        HighGui.imshow("Blured image", blurredImg);

        // HSV:
        Mat hsvImg = new Mat();
        Imgproc.cvtColor(blurredImg, hsvImg, Imgproc.COLOR_BGR2HSV);

        // Mask:
        Mat maskedimg = new Mat();
        Scalar min = new Scalar(20, 100, 100);
        Scalar max = new Scalar(30, 255, 255);
        Core.inRange(hsvImg, min, max, maskedimg);
        // show masked image
        HighGui.imshow("Masked image", maskedimg);

        // Filterred:
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Mat filterredImg = new Mat();
        Point anchor = new Point(-1, -1);
        Imgproc.erode(maskedimg, filterredImg, kernel, anchor, 2);
        Imgproc.dilate(maskedimg, filterredImg, kernel, anchor, 4);
        Imgproc.erode(maskedimg, filterredImg, kernel, anchor, 4);
        HighGui.imshow("Fillterred image", filterredImg);

        // Find contours:
        Mat hierarchy = new Mat(1, 1, CvType.CV_32SC2);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(filterredImg, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        MatOfPoint best = null;
        Rect bestRect = null;
        double bestRatio = 0;
        double bestSize = 0;

        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint m = contours.get(i);
            double area = Imgproc.contourArea(m);
            Rect rect = Imgproc.boundingRect(m);
            double r = (double) rect.height / rect.width;
            if (area > 200) {
                Imgproc.drawContours(out, contours, i, new Scalar(0, 0, 255), 2);
                if (best == null || isBetter(r, area, bestRatio, bestSize)) {
                    best = m;
                    bestRect = rect;
                    bestSize = area;
                    bestRatio = r;
                }

            }
        }
    }



    // Is better func:
    private static boolean isBetter(double r, double area, double bestRatio, double bestSize) {
        if (bestSize == 0) {
            return true;
        }
        double ratioDiffrence = Math.abs(r - 1);
        double bestRatioDiff = Math.abs(bestRatio - 1);
        if (ratioDiffrence < bestRatioDiff) {
            return true;
        } else if (ratioDiffrence == bestRatioDiff) {
            return area > bestSize;
        } else {
            return false;
        }

    }
    

}