package com.marven.fyp.memorytraining;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfInt4;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import android.app.Activity;
import android.graphics.SumPathEffect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.Size;

public class FingerActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {

    static {
        System.loadLibrary("opencv_java3");
    }
    private static final String    TAG                 = "HandPose::MainActivity";
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    private Mat                    mRgba;
    private Mat 					mIntermediateMat;

    private int                    mDetectorType       = JAVA_DETECTOR;

    private CustomSufaceView   mOpenCvCameraView;

    private SeekBar minTresholdSeekbar = null;
    private SeekBar maxTresholdSeekbar = null;
    private TextView minTresholdSeekbarText = null;
    private TextView numberOfFingersText = null;

    double iThreshold = 0;

    private Scalar               	mBlobColorHsv;
    private Scalar               	mBlobColorRgba;
    private ColorBlobDetector    	fingerDetector;
    private Mat                  	mSpectrum;
    private boolean				mIsColorSelected = false;

    private Size                 	SPECTRUM_SIZE;
    private Scalar               	CONTOUR_COLOR;
    private Scalar               	CONTOUR_COLOR_WHITE;

    int numberOfFingers = 0;

    final Runnable mUpdateFingerCountResults = new Runnable() {
        public void run() {
            updateNumberOfFingers();
        }
    };

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(FingerActivity.this);
                    // 640x480
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public FingerActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");

        super.onCreate(savedInstanceState);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.finger_surface_view);
        if (!OpenCVLoader.initDebug()) {
            Log.e("Test","man");
        }else{
        }

        mOpenCvCameraView = (CustomSufaceView) findViewById(R.id.main_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        minTresholdSeekbarText = (TextView) findViewById(R.id.textView3);


        numberOfFingersText = (TextView) findViewById(R.id.numberOfFingers);

        minTresholdSeekbar = (SeekBar)findViewById(R.id.seekBar1);
        minTresholdSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                progressChanged = progress;
                minTresholdSeekbarText.setText(String.valueOf(progressChanged));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                minTresholdSeekbarText.setText(String.valueOf(progressChanged));
            }
        });
        minTresholdSeekbar.setProgress(8700);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        //mGray = new Mat();
        //mRgba = new Mat();
        mIntermediateMat = new Mat();
        mRgba = new Mat(height, width, CvType.CV_8UC4);

        fingerDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);

        SPECTRUM_SIZE = new Size(200, 64);

        CONTOUR_COLOR = new Scalar(255,0,0,255);
        CONTOUR_COLOR_WHITE = new Scalar(255,255,255,255);

//        //colour to detect fingers
        mBlobColorHsv.val[0] = 14;   //H
        mBlobColorHsv.val[1] = 143;   //S
        mBlobColorHsv.val[2] = 157;   //V   //239

        //12 110 134 previous best radius 10 40 130
        //12 140 134 ? radius 10 40 90
        //14 143 157 , radius 10 70 90

//        16 141 150  25 85 213
//                25 52 109

        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        //to be added in
        fingerDetector.setColorRadius(new Scalar(10,70,90,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        fingerDetector.setHsvColor(mBlobColorHsv);
        Imgproc.resize(fingerDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

    }

    public void onCameraViewStopped() {
        //mGray.release();
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

        int x = (int)event.getX() - xOffset;
        int y = (int)event.getY() - yOffset;

        Log.i(TAG, "Touch image coordinates: (" + x + ", " + y + ")");

        if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

        Rect touchedRect = new Rect();

        touchedRect.x = (x>5) ? x-5 : 0;
        touchedRect.y = (y>5) ? y-5 : 0;

        touchedRect.width = (x+5 < cols) ? x + 5 - touchedRect.x : cols - touchedRect.x;
        touchedRect.height = (y+5 < rows) ? y + 5 - touchedRect.y : rows - touchedRect.y;

        Mat touchedRegionRgba = mRgba.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        mBlobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width*touchedRect.height;
        for (int i = 0; i < mBlobColorHsv.val.length; i++) {
        //for (int i = 0; i < 2; i++) {
            mBlobColorHsv.val[i] /= pointCount;
            Log.e(TAG, "touched HSV color: " + mBlobColorHsv.val[i]);
        }



//to be commented
//          mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
//          fingerDetector.setHsvColor(mBlobColorHsv);
//           Imgproc.resize(fingerDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);



        mIsColorSelected = true;
        ////to be commented

        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events


    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    List <MatOfPoint> fingerContours;


    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        Imgproc.GaussianBlur(mRgba, mRgba, new org.opencv.core.Size(3, 3), 1, 1);
        fingerDetector.processForFinger(mRgba);
        fingerContours = fingerDetector.getContours();

        if (fingerContours.size() <= 0) {
            return mRgba;
        }

        RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(fingerContours.get(0)	.toArray()));
        double boundWidth = rect.size.width;
        double boundHeight = rect.size.height;
        int boundPos = 0;
        for (int i = 1; i < fingerContours.size(); i++) {
            rect = Imgproc.minAreaRect(new MatOfPoint2f(fingerContours.get(i).toArray()));
            if (rect.size.width * rect.size.height > boundWidth * boundHeight) {
                boundWidth = rect.size.width;
                boundHeight = rect.size.height;
                boundPos = i;
            }
        }
        Rect boundRect = Imgproc.boundingRect(new MatOfPoint(fingerContours.get(boundPos).toArray()));
        Imgproc.rectangle( mRgba, boundRect.tl(), boundRect.br(), CONTOUR_COLOR_WHITE, 2, 8, 0 );
        double a = boundRect.br().y - boundRect.tl().y;
        a = a * 0.7;
        a = boundRect.tl().y + a;
        Imgproc.rectangle( mRgba, boundRect.tl(), new Point(boundRect.br().x, a), CONTOUR_COLOR, 2, 8, 0 );
        MatOfPoint2f pointMat = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(fingerContours.get(boundPos).toArray()), pointMat, 3, true);
        fingerContours.set(boundPos, new MatOfPoint(pointMat.toArray()));
        MatOfInt hull = new MatOfInt();
        MatOfInt4 convexDefect = new MatOfInt4();
        Imgproc.convexHull(new MatOfPoint(fingerContours.get(boundPos).toArray()), hull);
        Imgproc.convexityDefects(new MatOfPoint(fingerContours.get(boundPos)	.toArray()), hull, convexDefect);
        List<MatOfPoint> hullPoints = new LinkedList<MatOfPoint>();
        List<Point> listPo = new LinkedList<Point>();
        for (int j = 0; j < hull.toList().size(); j++) {
            listPo.add(fingerContours.get(boundPos).toList().get(hull.toList().get(j)));
        }
        MatOfPoint e = new MatOfPoint();
        e.fromList(listPo);
        hullPoints.add(e);
        Imgproc.drawContours(mRgba, hullPoints, -1, CONTOUR_COLOR, 3);
     //   Log.e(TAG, "hullPoints size: " + hullPoints.size()); //1 hand = 1 hullpoint
        getFingerTipXY(hullPoints);   //get the XY of the finger tip

        return mRgba;
    }

    public void updateNumberOfFingers(){
        numberOfFingersText.setText(String.valueOf(this.numberOfFingers));
    }


    Point[] points;
    double minValueX, minValueY, maxValueX, maxValueY, centreX, centreY;


    private void getFingerTipXY(List<MatOfPoint> contours) {
        for (int k = 0; k < contours.size(); k++) {
            points = contours.get(k).toArray();
            minValueX = points[0].x;
            minValueY = points[0].y;
            for (int i = 1; i < points.length; i++) {
                if (points[i].x < minValueX) {
                    minValueX = points[i].x;
                }
                if (points[i].y < minValueY) {
                    minValueY = points[i].y;
                }
            }

            maxValueX = points[0].x;
            maxValueY = points[0].y;
            for (int i = 1; i < points.length; i++) {
                if (points[i].x > maxValueX) {
                    maxValueX = points[i].x;
                }
                if (points[i].y > maxValueY) {
                    maxValueY = points[i].y;
                }
            }

            //get min cause mirror reflect
            //Log.e(TAG, "min finger value Y: " + minValueY);
            //Log.e(TAG, "min finger value X: " + minValueX);
        }
    }






}