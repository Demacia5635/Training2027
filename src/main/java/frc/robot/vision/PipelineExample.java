package frc.robot.vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.networktables.BooleanEntry;
import edu.wpi.first.networktables.DoubleEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.vision.VisionPipeline;
import edu.wpi.first.vision.VisionThread;

// Example vision pipeline that detects a yellow ball in an image using OpenCV
public class PipelineExample implements VisionPipeline {
    // Declare the camera, input and output streams, network table entries, and vision thread
    CvSink inputStream;
    CvSource outputStream;
    UsbCamera camera;
    BooleanEntry haveTarget;
    DoubleEntry targetX;
    DoubleEntry targety;
    VisionThread visionThread;

    /**
     * Constructor for the PipelineExample class 
     * initializes the camera, input and output streams, network table entries
     * and start the vision thread.
     */
    public PipelineExample() {
        camera = CameraServer.startAutomaticCapture();
        camera.setExposureAuto();
        camera.setResolution(320, 240);
        camera.setFPS(50);
        inputStream = CameraServer.getVideo();
        outputStream = CameraServer.putVideo("Processed", 320, 240);
        NetworkTable table = NetworkTableInstance.getDefault().getTable("Vision");
        haveTarget = table.getBooleanTopic("haveTarget").getEntry(false);
        targetX = table.getDoubleTopic("targetX").getEntry(0.0);
        targety = table.getDoubleTopic("targetY").getEntry(0.0);
        visionThread = new VisionThread(camera, this, p -> {});
        visionThread.setDaemon(true);
        visionThread.start();
    }
    
    /**
     * Processes the input image to detect a yellow ball and outputs the processed image and target information to the network table.
     */
    public void process(Mat image) {
        // Convert the image to HSV color space
        Mat hsv = new Mat();
        Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
        // Threshold the image to isolate a specific color range (e.g., yellow)
        Scalar min = new Scalar(20, 100, 100);
        Scalar max = new Scalar(30, 255, 255);
        Mat mask = new Mat();
        Core.inRange(hsv, min, max, mask);
        // Apply morphological operations to remove noise and fill gaps in the mask
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Point anchor = new Point(-1, -1);
        Imgproc.erode(mask, mask, kernel, anchor, 2);
        Imgproc.dilate(mask, mask, kernel, anchor, 4);
        Imgproc.erode(mask, mask, kernel, anchor, 2);
        // get the list of contours in the mask and find the largest one
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);         
        // find the largest contour and draw it on the image, also update the network table entries with the target information
        MatOfPoint best = null;
        double bestArea = 0;
        for (MatOfPoint contour : contours) {
           double area = Imgproc.contourArea(contour);
           if(area < 100) { // ignore small contours
               continue; 
           }
           // draw the contour on the image for debugging purposes
           Imgproc.drawContours(image, List.of(best), -1, new Scalar(0, 255, 0), 2);
            if (area > bestArea) {
                best = contour;
                bestArea = area;
                break;
            }
        }
        // Draw the best contour center point on the image and update the network table entries with the target information
        if(best != null) {
            haveTarget.set(true);
            Rect rect = Imgproc.boundingRect(best);
            double x = rect.x + rect.width / 2.0;
            double y = rect.y + rect.height / 2.0;  
            targetX.set(x); 
            targety.set(y);
            Imgproc.circle(image, new Point(x, y), 5, new Scalar(0, 255, 0),  -1);
        } else {
            haveTarget.set(false);
        }   
        outputStream.putFrame(image);
    }       

}