package com.dfrobot.angelo.blunobasicdemo;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static android.content.ContentValues.TAG;

public class ColorBlobDetector {
    // Lower and Upper bounds for range checking in HSV color space
    private Scalar mLowerBound = new Scalar(0);
    private Scalar mUpperBound = new Scalar(0);
    // Minimum contour area in percent for contours filtering
    private static double mMinContourArea = 0.1; //0.1
    // Color radius for range checking in HSV color space
    private Scalar mColorRadius = new Scalar(25,50,50,0);   //25 50 50 0
    private Mat mSpectrum = new Mat();
    private List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();

    // Cache
    Mat mPyrDownMat = new Mat();
    Mat mHsvMat = new Mat();
    Mat mMask = new Mat();
    Mat mDilatedMask = new Mat();
    Mat mHierarchy = new Mat();

    public void setColorRadius(Scalar radius) {
        mColorRadius = radius;
    }

    //set the HsvColor that you want to detect
    public void setHsvColor(Scalar hsvColor) {

        //define upperbound to lower bound allowed range
        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0]-mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0]+mColorRadius.val[0] <= 255) ? hsvColor.val[0]+mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;

        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];

        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];

        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;
        ///////////////////////////

        Mat spectrumHsv = new Mat(1, (int)(maxH-minH), CvType.CV_8UC3);

        for (int j = 0; j < maxH-minH; j++) {
            byte[] tmp = {(byte)(minH+j), (byte)255, (byte)255};
            spectrumHsv.put(0, j, tmp);
        }

        Imgproc.cvtColor(spectrumHsv, mSpectrum, Imgproc.COLOR_HSV2RGB_FULL, 4);
    }

    public Mat getSpectrum() {
        return mSpectrum;
    }

    public void setMinContourArea(double area) {
        mMinContourArea = area;
    }


    public void process(Mat rgbaImage) {


        Imgproc.pyrDown(rgbaImage, mPyrDownMat);        //Blurs an image and downsamples it. src - rgbaImage dst - mPyrDownMat
        Imgproc.pyrDown(mPyrDownMat, mPyrDownMat);         //Blurs an image and downsamples it.
        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);     //Converts an image from one color space (RGB to HSV) to another. src- mPyrDownMat ,dst - mHsvMat
        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);     //create a binary image
        Imgproc.dilate(mMask, mDilatedMask, new Mat());     //dilate means enlarge. src- mMask, dst - mDilatedMask

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        // mDIlatedMask - an enlarged binary image. mHierarchy - not used. RETR_EXTERNAL - retrieves only the extreme outer contours
        //CV_CHAIN_APPROX_SIMPLE compresses horizontal, vertical, and diagonal segments and leaves only their end points

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(4,4), contour);
                mContours.add(contour);
            }
        }
    }


    Point[] points;
    double minValueX, minValueY, maxValueX, maxValueY, centreX , centreY , prevCentreX=0, prevCentreY=0;
    List<MatOfPoint> filteredContours = new ArrayList<MatOfPoint>();


    public void processForCanny(Mat rgbaImage) {
        Imgproc.pyrDown(rgbaImage, mPyrDownMat);        //Blurs an image and downsamples it. src - rgbaImage dst - mPyrDownMat
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mPyrDownMat, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        // mDIlatedMask - an enlarged binary image. mHierarchy - not used. RETR_EXTERNAL - retrieves only the extreme outer contours
        //CV_CHAIN_APPROX_SIMPLE compresses horizontal, vertical, and diagonal segments and leaves only their end points

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        mContours.clear();
        each = contours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea*maxArea) {
                Core.multiply(contour, new Scalar(2,2), contour);
                mContours.add(contour);
            }
        }




//        filteredContours.clear();
//        //List<MatOfPoint> contours = mDetector.getContours();       //get the contours after mDetector.process
//        Log.e(TAG, "Contours count: " + mContours.size());
//
//
//
//        for (int k=0; k<mContours.size(); k++) {
//            points = mContours.get(k).toArray();
//
//            //find minimum value of x and y
//            minValueX = points[0].x;
//            minValueY = points[0].y;
//            for (int i=1; i<points.length; i++){
//                if (points[i].x < minValueX){
//                    minValueX = points[i].x;
//                }
//                if (points[i].y < minValueY){
//                    minValueY = points[i].y;
//                }
//            }
//
//            //find maximum value of x and y
//            maxValueX = points[0].x;
//            maxValueY = points[0].y;
//            for (int i=1; i<points.length; i++){
//                if (points[i].x > maxValueX){
//                    maxValueX = points[i].x;
//                }
//                if (points[i].y > maxValueY){
//                    maxValueY = points[i].y;
//                }
//            }
//
//            //find centre of x and y
//            centreX = (maxValueX - minValueX) / 2 + minValueX;
//            centreY = (maxValueY - minValueY) / 2 + minValueY;
//            Point centrePoint = new Point( centreX, centreY);
//            //Imgproc.circle(mRgba, centrePoint ,10 , CONTOUR_COLOR);
//            //Imgproc.putText(mRgba, String.valueOf(k) , centrePoint, Core.FONT_HERSHEY_PLAIN, 3 , CONTOUR_COLOR);      //show number of chip (specified colour) detected
////                Imgproc.putText(mRgba, String.valueOf(centreX) + ", " + String.valueOf(centreY) , centrePoint, Core.FONT_HERSHEY_PLAIN, 3 , CONTOUR_COLOR); //show centre x,y of chip (specified colour)
////                System.out.println("centreX: " + centreX);
////                System.out.println("centreY: " + centreY);
//
//            //filter identified contours to detect properties of circle/square
//            if ( ((maxValueX - minValueX) > 70 ) && ((maxValueX - minValueX) < 130) )       //each contour must be within this y range
//                if ( ((maxValueY - minValueY) > 50 ) && ((maxValueY - minValueY) < 130) )       //each contour must be within this y range
//                    filteredContours.add(mContours.get(k));
//
//                /*
//                if (prevCentreX !=0) {
//                    if (((Math.abs(centreX - prevCentreX)) > 100) && ((Math.abs(centreX - prevCentreX)) < 130))
//                        filteredContours.add(mContours.get(k));
//                }
//                prevCentreX = centreX;
//                prevCentreY = centreY;
//                */
//        }

    }



    public List<MatOfPoint> getContours() {
        return mContours;
    }

    public List<MatOfPoint> getFilteredContours() {
        return filteredContours;
    }
}
