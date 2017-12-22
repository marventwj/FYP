package com.dfrobot.angelo.blunobasicdemo;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;

public class CameraActivity extends Activity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";

    private boolean              mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar               mBlobColorHsv;
    private ColorBlobDetector    mDetector;
    private Mat                  mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar               CONTOUR_COLOR;

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CameraActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    public CameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //no need?
        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);   //no need?
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {     //when press on camera screen
        int cols = mRgba.cols();    //get resolution of display ( cols value is 1280 )
        int rows = mRgba.rows();    //get resolution of display  ( rows value is 720 )

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;    //get resolution of display (mOpenCvCameraView.getWidth() value is 1920 , xOffset value is 320 )
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;   //get resolution of display (mOpenCvCameraView.getHeight() value is 1005 , yOffset value is 142 )

        int x = (int)event.getX() - xOffset;    //get resolution of display (event.getX is y axis, most top lesser value, more btm higher value ~?)
        int y = (int)event.getY() - yOffset;    //get resolution of display (event.getY is x axis, most right lesser value, most left higher value ~?)


        //int x = (int)event.getX();
        //int y = (int)event.getY();
        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");          // x is y axis, most top lesser value, more btm higher value ~ 1460
                                                                                    //  y is x axis, most right lesser value, most left higher value ~800

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;   //ensure it is within screen

        //make a rectangle of width and height of 8 (it's centre point is x & y i.e the touched point event.getX and event.getY, hence need to convert to rectangle's x and y (the starting corner of rectangle).
        //in the case where screen touched is 0 & 0 or 1920 & 1080,  rectangle width and height will only be 4.
        Rect touchedRect = new Rect();
        touchedRect.x = (x>4) ? x-4 : 0;
        touchedRect.y = (y>4) ? y-4 : 0;
        touchedRect.width = (x+4 < cols) ? x + 4 - touchedRect.x : cols - touchedRect.x;        //if x+4< cols, do x+4 - touchedRect.x , else do cols - touchedRect.x
        touchedRect.height = (y+4 < rows) ? y + 4 - touchedRect.y : rows - touchedRect.y;       //if y+4< rows, do y+4 - touchedRect.y, else do rows - touchedRect.y

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        //convert new Rgba mat to HSV colour space
        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region (the rectangle region)
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++)
            mBlobColorHsv.val[i] /= pointCount;


        //converts hsv scalar value to rgba scalar value
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        //values are capped at (255,255,255,255[this value is always 255 in most case, on websites, 255 may refer to '1'])
        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        mDetector.setHsvColor(mBlobColorHsv);

        Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);      //resize the image to specture size

        mIsColorSelected = true;

        //release all mats
        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    Point[] points;
    double minValueX, minValueY, maxValueX, maxValueY, centreX , centreY;

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //take the frame rgba mat object
        mRgba = inputFrame.rgba();

        //if screen is touched,based on the touched average colour make the blobs
        if (mIsColorSelected) {
            mDetector.process(mRgba);
            List<MatOfPoint> contours = mDetector.getContours();       //get the contours after mDetector.process
            Log.e(TAG, "Contours count: " + contours.size());


            for (int k=0; k<contours.size(); k++) {
                points = contours.get(k).toArray();
                System.out.println("@@@@@@@@@@@@@@@@@@@@points.length: " + points.length);

                /*
                for (int i = 0; i < points.length; i++) {
                    Imgproc.circle(mRgba, points[i] ,4 , CONTOUR_COLOR);
                    double x = points[i].x;
                    double y = points[i].y;
                    System.out.println("x: " + x);
                    System.out.println("y: " + y);
                }
*/


                minValueX = points[0].x;
                minValueY = points[0].y;
                for (int i=1; i<points.length; i++){
                    if (points[i].x < minValueX){
                        minValueX = points[i].x;
                    }
                    if (points[i].y < minValueY){
                        minValueY = points[i].y;
                    }
                }

                maxValueX = points[0].x;
                maxValueY = points[0].y;
                for (int i=1; i<points.length; i++){
                    if (points[i].x > maxValueX){
                        maxValueX = points[i].x;
                    }
                    if (points[i].y > maxValueY){
                        maxValueY = points[i].y;
                    }
                }
                centreX = (maxValueX - minValueX) / 2 + minValueX;
                centreY = (maxValueY - minValueY) / 2 + minValueY;
                Point centrePoint = new Point( centreX, centreY);
                //Imgproc.circle(mRgba, centrePoint ,10 , CONTOUR_COLOR);
                //Imgproc.putText(mRgba, String.valueOf(k) , centrePoint, Core.FONT_HERSHEY_PLAIN, 3 , CONTOUR_COLOR);      //show number of chip (specified colour) detected
                Imgproc.putText(mRgba, String.valueOf(centreX) + ", " + String.valueOf(centreY) , centrePoint, Core.FONT_HERSHEY_PLAIN, 3 , CONTOUR_COLOR); //show centre x,y of chip (specified colour)
                //System.out.println("centreX: " + centreX);
                //System.out.println("centreY: " + centreY);

            }




            Imgproc.drawContours(mRgba, contours, -1, CONTOUR_COLOR);   //draw red contours on the screen

            Mat colorLabel = mRgba.submat(4, 68, 4, 68);
            colorLabel.setTo(mBlobColorRgba);

            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);
        }

        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}
