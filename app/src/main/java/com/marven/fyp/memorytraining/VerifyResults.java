package com.marven.fyp.memorytraining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
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
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.imgproc.Imgproc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceView;

import static com.marven.fyp.memorytraining.CameraCheckChipsRemoved.fingerBufferLength;

public class VerifyResults extends BaseActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";
    public static final int chipBufferLengthFromCentre = 35;

    public enum Colours {
        RED,
        GREEN,
        BLUE,
        YELLOW;
    }

    public static final int xMinChipLength = 20;
    public static final int xMaxChipLength = 130; //130
    public static final int yMinChipLength = 20;
    public static final int yMaxChipLength = 170; //130

    Intent i;
    //int level, gameSelected;
    boolean win;

    PointXY[][] boardMatrix = new PointXY[8][8];
    char [][] chipPlacement = new char[8][8];
    char[][] generatedPlacement = new char[8][8];
    ArrayList<String> lightedLEDStringList = new ArrayList<String>();
    ArrayList<String> stringBuffer = new ArrayList<String>();
    ArrayList<Integer> generatedSequence = new ArrayList<Integer>();

    MediaPlayer placeTheChipsMP3;
    MediaPlayer soundMP3;
    MediaPlayer LEDMP3;

    private boolean              pressedStartButton = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar               mBlobColorHsv ,redBlobColorHsv, greenBlobColorHsv, blueBlobColorHsv, yellowBlobColorHsv , fingerBlobColorHsv , pinkButtonBlobColorHsv;
    private ColorBlobDetector    mDetector, redDetector, greenDetector, blueDetector, yellowDetector,  fingerDetector , pinkButtonDetector;
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
                    mOpenCvCameraView.setOnTouchListener(VerifyResults.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public VerifyResults() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        i = new Intent(this, Score.class);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //no need?
        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);   //no need?
        mOpenCvCameraView.setCvCameraViewListener(this);


        if (DataHolder.getGameSelected() == 1) {
            placeTheChipsMP3 = MediaPlayer.create(this, R.raw.you_can_place_the_chips_now);
            soundMP3 = MediaPlayer.create(this, R.raw.press_start_when_you_are_ready);
            placeTheChipsMP3.start();       //press start when you are ready
            placeTheChipsMP3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    soundMP3.start();
                    mp.release();
                }
            }); //press start when you are ready
        }

        generatedPlacement = (char [][])getIntent().getSerializableExtra("generatedPlacement");    //get array XY of board from previous activity
        //see generated placement of chips
//        for (int i=0; i<8; i++)
//            for (int j=0 ;j<8;j++)
//                Log.e(TAG, "generatedPlacement is " + generatedPlacement[i][j] + i + "" + j);
        boardMatrix = (PointXY [][])getIntent().getSerializableExtra("boardMatrix");    //get array XY of board from previous activity
//        //see centrepoints of x y board.
//        for (int a=0; a<8; a++)
//            for (int b=0; b<8; b++) {
//                Log.e(TAG, "x" + a + ": " + boardMatrix[a][b].x() + "y" + b + ": " + boardMatrix[a][b].y());
//            }
        lightedLEDStringList = getIntent().getStringArrayListExtra("lightedLEDStringList");
        generatedSequence = getIntent().getIntegerArrayListExtra("generatedSequence");

       // level =  getIntent().getIntExtra("Level",0);
       // gameSelected =  getIntent().getIntExtra("GameSelected",0);


        //print generated Sequence
        for (int i = 0; i < generatedSequence.size(); i++) {
            Log.e(TAG, "generated sequence: " + generatedSequence.get(i));
        }
        Log.e(TAG, " ");



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

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

    public void onCameraViewStarted(int width, int height) {

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mDetector = new ColorBlobDetector();
        redDetector = new ColorBlobDetector();
        greenDetector = new ColorBlobDetector();
        blueDetector = new ColorBlobDetector();
        yellowDetector = new ColorBlobDetector();
        fingerDetector = new ColorBlobDetector();
        pinkButtonDetector = new ColorBlobDetector();

        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        redBlobColorHsv = new Scalar(255);
        greenBlobColorHsv = new Scalar(255);
        blueBlobColorHsv = new Scalar(255);
        yellowBlobColorHsv = new Scalar(255);
        fingerBlobColorHsv = new Scalar ( 255);
        pinkButtonBlobColorHsv = new Scalar (255);

        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);

        redBlobColorHsv.val[0] = 2;   //H  //250
        redBlobColorHsv.val[1] = 188;   //S   //136
        redBlobColorHsv.val[2] = 190;   //V

        //hsv : 95, 126, 134, 0 - old green chip
        greenBlobColorHsv.val[0] = 82;  //H
        greenBlobColorHsv.val[1] = 180; //S
        greenBlobColorHsv.val[2] = 140; //V
        //69 183 234 brighter
        //76 193 116 darker
        //82 180 180 average good

        //hsv : 143, 143, 138, 0 -  old blue chip
        blueBlobColorHsv.val[0] = 177;  //H
        blueBlobColorHsv.val[1] = 117;  //S
        blueBlobColorHsv.val[2] = 130;   //V
        //165 157 108 brighter
        //172 89 48 darker, but sensing black also
        //177 117 42 darker, but abit never sense black

        //hsv : 41, 163, 125, 0 - old yellow chip
        yellowBlobColorHsv.val[0] = 20;  //H        //50 previously when chip is bright        //30 for dark
        yellowBlobColorHsv.val[1] = 177; //S
        yellowBlobColorHsv.val[2] = 248; //V

        fingerBlobColorHsv.val[0] = 14;   //H
        fingerBlobColorHsv.val[1] = 143;   //S
        fingerBlobColorHsv.val[2] = 137;   //V   //239

        pinkButtonBlobColorHsv.val[0] = 246;   //H
        pinkButtonBlobColorHsv.val[1] = 86;   //S
        pinkButtonBlobColorHsv.val[2] = 168;   //V

        //converts hsv scalar value to rgba scalar value
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        //values are capped at (255,255,255,255[this value is always 255 in most case, on websites, 255 may refer to '1'])
        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        redDetector.setColorRadius(new Scalar(5,50,80,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        greenDetector.setColorRadius(new Scalar(15,50,85,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        blueDetector.setColorRadius(new Scalar(30,50,105,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        yellowDetector.setColorRadius(new Scalar(15,50,90,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        fingerDetector.setColorRadius(new Scalar(10,70,90,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        pinkButtonDetector.setColorRadius(new Scalar(25,30,90,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance

        //mDetector.setHsvColor(mBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        redDetector.setHsvColor(redBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        greenDetector.setHsvColor(greenBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        blueDetector.setHsvColor(blueBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        yellowDetector.setHsvColor(yellowBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        fingerDetector.setHsvColor(fingerBlobColorHsv);
        pinkButtonDetector.setHsvColor(pinkButtonBlobColorHsv);

        //Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(redDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(greenDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(blueDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(yellowDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(fingerDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(pinkButtonDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);


    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {     //when press on camera screen

        pressedStartButton = true;    //simulate button pressed
        return false; // don't need subsequent touch events
    }

    Point[] points;
    double minValueX, minValueY, maxValueX, maxValueY, centreX, centreY, valueX , valueY;
    List<MatOfPoint> redContours;
    List<MatOfPoint> greenContours;
    List<MatOfPoint> blueContours;
    List<MatOfPoint> yellowContours;
    List<MatOfPoint> pinkButtonContours;
    List<MatOfPoint> fingerContours;

    ArrayList<PointXY> redCentrePointList = new ArrayList<PointXY>();
    ArrayList<PointXY> greenCentrePointList = new ArrayList<PointXY>();
    ArrayList<PointXY> blueCentrePointList = new ArrayList<PointXY>();
    ArrayList<PointXY> yellowCentrePointList = new ArrayList<PointXY>();

    double redCentrePointX, redCentrePointY, greenCentrePointX, greenCentrePointY, blueCentrePointX , blueCentrePointY, yellowCentrePointX, yellowCentrePointY;
    RotatedRect rect;

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();

        if (DataHolder.getGameSelected() == 1) {
            determineIfUserPressedStart();
            verifyGame1();
        }
        if (DataHolder.getGameSelected() == 2)
            verifyGame2();

        return mRgba;
    }

    private void determineIfUserPressedStart(){
        //detect pink button colour
        pinkButtonDetector.process(mRgba);
        pinkButtonContours = pinkButtonDetector.getContours();
        filteredPinkButtonContours = filterColourContours(pinkButtonContours);
        Imgproc.drawContours(mRgba, filteredPinkButtonContours, -1, new Scalar (255, 0 , 255 , 255));   //draw pink contours on the screen
        //--

        //detect finger colour
        Imgproc.GaussianBlur(mRgba, mRgba, new org.opencv.core.Size(3, 3), 1, 1);
        fingerDetector.processForFinger(mRgba);
        fingerContours = fingerDetector.getContours();
        //--

        if (fingerContours.size() != 0 ) {  //filter out wrong finger detections
            PointXY fingerTipXY = detectFinger();
            checkIfUserPressPinkButton(fingerTipXY);
        }
    }

    //PointXY fingerTipXY;

    private void checkIfUserPressPinkButton(PointXY fingerTipXY){
        PointXY pinkButtonXY = getMaxPointXY(filteredPinkButtonContours);   //get point of max Y of pink button
        if (filteredPinkButtonContours.size() != 0) {  //if pink button is found on camera
            if ((pinkButtonXY.y() > (fingerTipXY.y() - fingerBufferLength)) && (pinkButtonXY.y() < (fingerTipXY.y() + fingerBufferLength))) { //use first column as reference to check which column the chip lies on
                if ((pinkButtonXY.x() > (fingerTipXY.x() - fingerBufferLength)) && (pinkButtonXY.x() < (fingerTipXY.x() + fingerBufferLength))) {
                    pressedStartButton = true;
                    Log.e(TAG, "START!");
                }
            }
        }
    }

    private PointXY detectFinger(){
        rect = Imgproc.minAreaRect(new MatOfPoint2f(fingerContours.get(0).toArray()));
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
        Imgproc.rectangle(mRgba, boundRect.tl(), boundRect.br(), new Scalar(255, 255, 255, 255), 2, 8, 0);   // white
        double a = boundRect.br().y - boundRect.tl().y;
        a = a * 0.7;
        a = boundRect.tl().y + a;
        Imgproc.rectangle(mRgba, boundRect.tl(), new Point(boundRect.br().x, a), CONTOUR_COLOR, 2, 8, 0);
        MatOfPoint2f pointMat = new MatOfPoint2f();
        Imgproc.approxPolyDP(new MatOfPoint2f(fingerContours.get(boundPos).toArray()), pointMat, 3, true);
        fingerContours.set(boundPos, new MatOfPoint(pointMat.toArray()));
        MatOfInt hull = new MatOfInt();
        MatOfInt4 convexDefect = new MatOfInt4();
        Imgproc.convexHull(new MatOfPoint(fingerContours.get(boundPos).toArray()), hull);
        Imgproc.convexityDefects(new MatOfPoint(fingerContours.get(boundPos).toArray()), hull, convexDefect);
        List<MatOfPoint> hullPoints = new LinkedList<MatOfPoint>();
        List<Point> listPo = new LinkedList<Point>();
        for (int j = 0; j < hull.toList().size(); j++) {
            listPo.add(fingerContours.get(boundPos).toList().get(hull.toList().get(j)));
        }
        MatOfPoint e = new MatOfPoint();
        e.fromList(listPo);
        hullPoints.add(e);
        Imgproc.drawContours(mRgba, hullPoints, -1, new Scalar(255, 0, 255, 255), 3);   //pink
        PointXY fingerTipXY = getMaxPointXY(hullPoints);   //get point of max Y of the finger tip
        return fingerTipXY;
    }


    private void verifyGame1(){
        //detecting chips
        redDetector.process(mRgba);
        greenDetector.process(mRgba);
        blueDetector.process(mRgba);
        yellowDetector.process(mRgba);

        //get the contours after detector.process
        redContours = redDetector.getContours();
        greenContours = greenDetector.getContours();
        blueContours = blueDetector.getContours();
        yellowContours = yellowDetector.getContours();

//        Imgproc.drawContours(mRgba, redContours, -1, CONTOUR_COLOR);   //draw red contours on the screen
//        Imgproc.drawContours(mRgba, greenContours, -1,  new Scalar(0,255,0,255));   //draw green contours on the screen
//        Imgproc.drawContours(mRgba, blueContours, -1,  new Scalar(0,0,255,255));   //draw blue contours on the screen
//        Imgproc.drawContours(mRgba, yellowContours, -1,  new Scalar(255,255,0,255));   //yellow red contours on the screen

        filteredRedContours = filterColourContours(redContours);
        filteredGreenContours = filterColourContours(greenContours);
        filteredBlueContours = filterColourContours(blueContours);
        filteredYellowContours = filterColourContours(yellowContours);


        Imgproc.drawContours(mRgba, filteredRedContours, -1, CONTOUR_COLOR);   //draw red contours on the screen
        Imgproc.drawContours(mRgba, filteredGreenContours, -1,  new Scalar(0,255,0,255));   //draw red contours on the screen
        Imgproc.drawContours(mRgba, filteredBlueContours, -1,  new Scalar(0,0,255,255));   //draw red contours on the screen
        Imgproc.drawContours(mRgba, filteredYellowContours, -1,  new Scalar(255,255,0,255));   //draw red contours on the screen


        //if screen is touched (simulate start button), verify results
        if (pressedStartButton) {

//            Log.e(TAG, "red Contours count: " + redContours.size());
//            addChipsCentreToList(redContours, Colours.RED);
//            Log.e(TAG, "green Contours count: " + greenContours.size());
//            addChipsCentreToList(greenContours, Colours.GREEN);
//            Log.e(TAG, "blue Contours count: " + blueContours.size());
//            addChipsCentreToList(blueContours, Colours.BLUE);
//            Log.e(TAG, "yellowContours count: " + yellowContours.size());
//            addChipsCentreToList(yellowContours, Colours.YELLOW);

            Log.e(TAG, "filtered red Contours count: " + filteredRedContours.size());
            addChipsCentreToList(filteredRedContours, Colours.RED);
            Log.e(TAG, "filtered green Contours count: " + filteredGreenContours.size());
            addChipsCentreToList(filteredGreenContours, Colours.GREEN);
            Log.e(TAG, "filtered blue Contours count: " + filteredBlueContours.size());
            addChipsCentreToList(filteredBlueContours, Colours.BLUE);
            Log.e(TAG, "filtered yellow Contours count: " + filteredYellowContours.size());
            addChipsCentreToList(filteredYellowContours, Colours.YELLOW);

            //empty chipPlacementArray first
            for (int i = 0; i < 8; i++) {
                Arrays.fill(chipPlacement[i], '0');
            }
            //identifying chips position on the board
            for (int i = 0; i < redCentrePointList.size(); i++) {   //check all red chips
                redCentrePointX = redCentrePointList.get(i).x();
                redCentrePointY = redCentrePointList.get(i).y();

                double min = redCentrePointX - chipBufferLengthFromCentre;
                double max = redCentrePointX + chipBufferLengthFromCentre;

                double minY = redCentrePointY - chipBufferLengthFromCentre;
                double maxY = redCentrePointY + chipBufferLengthFromCentre;
                Log.e(TAG, "red min range y based on chip: " + minY);
                Log.e(TAG, "red max range y based on chip: " + maxY);


                for (int j = 0; j < 8; j++) {       //iterate through first column
                    Log.e(TAG, "y of board: " + boardMatrix[j][0].y());

                    if ((boardMatrix[j][0].y() > (redCentrePointY - chipBufferLengthFromCentre)) && (boardMatrix[j][0].y() < (redCentrePointY + chipBufferLengthFromCentre))) { //use first column as reference to check which column the chip lies on
                        //check X

                        Log.e(TAG, "red min range x based on chip: " + min);
                        Log.e(TAG, "red max range x based on chip: " + max);


                        for (int k = 0; k < 8; k++) {    //iterate through the rows
                            if ((boardMatrix[j][k].x() > (redCentrePointX - chipBufferLengthFromCentre)) && (boardMatrix[j][k].x() < (redCentrePointX + chipBufferLengthFromCentre))) {
                                chipPlacement[j][k] = 'r';

                                Log.e(TAG, "x of board: " + boardMatrix[j][k].x());
                                Log.e(TAG, "register chip placement r");
                                Log.e(TAG, " ");

                                break;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < greenCentrePointList.size(); i++) {   //check all green chips
                greenCentrePointX = greenCentrePointList.get(i).x();
                greenCentrePointY = greenCentrePointList.get(i).y();

                double min = greenCentrePointX - chipBufferLengthFromCentre;
                double max = greenCentrePointX + chipBufferLengthFromCentre;

                double minY = greenCentrePointY - chipBufferLengthFromCentre;
                double maxY = greenCentrePointY + chipBufferLengthFromCentre;
                Log.e(TAG, "green min range y based on chip: " + minY);
                Log.e(TAG, "green max range y based on chip: " + maxY);



                for (int j = 0; j < 8; j++) {       //iterate through first column
                    Log.e(TAG, "y of board: " + boardMatrix[j][0].y());

                    if ((boardMatrix[j][0].y() > (greenCentrePointY - chipBufferLengthFromCentre)) && (boardMatrix[j][0].y() < (greenCentrePointY + chipBufferLengthFromCentre))) { //use first column as reference to check which column the chip lies on
                        //check X

                        Log.e(TAG, "green min range x based on chip: " + min);
                        Log.e(TAG, "green max range x based on chip: " + max);

                        for (int k = 0; k < 8; k++) {    //iterate through the rows
                            if ((boardMatrix[j][k].x() > (greenCentrePointX - chipBufferLengthFromCentre)) && (boardMatrix[j][k].x() < (greenCentrePointX + chipBufferLengthFromCentre))) {
                                chipPlacement[j][k] = 'g';

                                Log.e(TAG, "x of board: " + boardMatrix[j][k].x());
                                Log.e(TAG, "register chip placement g");
                                Log.e(TAG, " ");

                                break;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < blueCentrePointList.size(); i++) {   //check all blue chips
                blueCentrePointX = blueCentrePointList.get(i).x();
                blueCentrePointY = blueCentrePointList.get(i).y();
                for (int j = 0; j < 8; j++) {       //iterate through first column
                    if ((boardMatrix[j][0].y() > (blueCentrePointY - chipBufferLengthFromCentre)) && (boardMatrix[j][0].y() < (blueCentrePointY + chipBufferLengthFromCentre))) { //use first column as reference to check which column the chip lies on
                        //check X
                        for (int k = 0; k < 8; k++) {    //iterate through the rows
                            if ((boardMatrix[j][k].x() > (blueCentrePointX - chipBufferLengthFromCentre)) && (boardMatrix[j][k].x() < (blueCentrePointX + chipBufferLengthFromCentre))) {
                                chipPlacement[j][k] = 'b';
                                Log.e(TAG, "register chip placement b");
                                break;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < yellowCentrePointList.size(); i++) {   //check all yellow chips
                yellowCentrePointX = yellowCentrePointList.get(i).x();
                yellowCentrePointY = yellowCentrePointList.get(i).y();
                for (int j = 0; j < 8; j++) {       //iterate through first column
                    if ((boardMatrix[j][0].y() > (yellowCentrePointY - chipBufferLengthFromCentre)) && (boardMatrix[j][0].y() < (yellowCentrePointY + chipBufferLengthFromCentre))) { //use first column as reference to check which column the chip lies on
                        //check X
                        for (int k = 0; k < 8; k++) {    //iterate through the rows
                            if ((boardMatrix[j][k].x() > (yellowCentrePointX - chipBufferLengthFromCentre)) && (boardMatrix[j][k].x() < (yellowCentrePointX + chipBufferLengthFromCentre))) {
                                chipPlacement[j][k] = 'y';
                                Log.e(TAG, "register chip placement y");
                                break;
                            }
                        }
                    }
                }
            }

//            //verify chip placement
            for (int j=0; j<8; j++)
                for (int k = 0; k<8; k++) {
                    if (chipPlacement[j][k] != '0')
                        Log.e(TAG, "Chip placement is: Position Row " + j + ", Column " + k + " colour: " + chipPlacement[j][k]);
                }

            lightUpGeneratedPlacement();

            //put a bit of delay so can show the user the lighted up LED
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            stringBuffer.add("0300000000CCFFFFFFFFFFFFFFFF");       //turn off all LED
            send();

            //verify win or lose
            if (Arrays.deepEquals(chipPlacement, generatedPlacement)) {
                Log.e(TAG, "user won!");//user wins
                win = true;
                //DataHolder.setLevel(DataHolder.getLevel() + 1); //next level
                //level += 1; //next level
            }
            else {
                Log.e(TAG, "user lost!");//user loses
                //DataHolder.setLevel(1); //restart from level 1
                //level = 1; //restart level
                win = false;
            }

            //put extra to next activity
            //i.putExtra("Level",level);
            i.putExtra("win", win);
            //i.putExtra("GameSelected", gameSelected);
            startActivity(i);
        }
        pressedStartButton = false;

    }

    PointXY fingerTipXY = null;
    int prevFingerPositionOnBoard= 0, fingerPositionOnBoard = 0;
    boolean lose = false;
    int generatedSequenceIndex = 0;
    long startTime;

    private void verifyGame2(){
        boolean fingerInsideBoard = false;
        //detect finger colour
        Imgproc.GaussianBlur(mRgba, mRgba, new org.opencv.core.Size(3, 3), 1, 1);
        fingerDetector.processForFinger(mRgba);
        fingerContours = fingerDetector.getContours();
        //--

        if (fingerContours.size() != 0 ) {  //filter out wrong finger detections
            fingerTipXY = detectFinger();

//if (fingerTipXY in area of board)
//if (fingerTipXY almost same for 1 second)
            //check finger current position
            for (int j = 0; j < 8; j++) {       //iterate through first column to determine the row(j) it is at
                if ((boardMatrix[j][0].y() > (fingerTipXY.y() - chipBufferLengthFromCentre)) && (boardMatrix[j][0].y() < (fingerTipXY.y() + chipBufferLengthFromCentre))) { //use first column as reference to check which column the chip lies on
                    //check X
                    for (int k = 0; k < 8; k++) {    //iterate through the rows
                        if ((boardMatrix[j][k].x() > (fingerTipXY.x() - chipBufferLengthFromCentre)) && (boardMatrix[j][k].x() < (fingerTipXY.x() + chipBufferLengthFromCentre))) {

                            fingerInsideBoard = true;
                            fingerPositionOnBoard= Integer.valueOf(String.valueOf(j) + String.valueOf(k));   //combine row and column into an integer number
                            //Log.e(TAG, "finger within board");


                            //if current finger position not same position as previous finger position
                            if (prevFingerPositionOnBoard != fingerPositionOnBoard) {
                                startTime = SystemClock.elapsedRealtime(); //reset timer
                            }

                            long endTime = SystemClock.elapsedRealtime();
                            long elapsedMilliSeconds = endTime - startTime;
                            //double elapsedSeconds = elapsedMilliSeconds / 1000.0;

                            //means finger at same position for 1 second
                            if (elapsedMilliSeconds > 1000) {
                                Log.e(TAG, "finger placement is: " + j + k);
                                //verify if user lost by comparing finger current position with generated sequence
                                if (fingerPositionOnBoard != generatedSequence.get(generatedSequenceIndex)) {
                                    //user lost, proceed to score page
                                    Log.e(TAG, "user lost!");
                                    win = false;
                                    i.putExtra("win", win);
                                    startActivity(i);

                                }

                                //user for correct for that particular sequence
                                else {
                                    Log.e(TAG, "correct for this sequence");
                                    LEDMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
                                    LEDMP3.start();                                     //play sound
                                    LEDMP3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                        public void onCompletion(MediaPlayer mp) {
                                            mp.release();

                                        };
                                    });

                                    generatedSequenceIndex++;
                                    startTime = SystemClock.elapsedRealtime(); //reset timer
                                }

                                //if all sequence correct
                                if (generatedSequenceIndex == generatedSequence.size()) {
                                    //user won, proceed to score page
                                    Log.e(TAG, "user won!");
                                    win = true;
                                    i.putExtra("win", win);
                                    startActivity(i);
                                }
                            }

                            //haven't reach 1 second, continue observing
                            else {
                                prevFingerPositionOnBoard = fingerPositionOnBoard;
                            }


                            break;
                        }
                    }
                }
            }

            //reset timer if finger not inside board
            if (!fingerInsideBoard) {
                startTime = SystemClock.elapsedRealtime(); //reset timer
            }


        }



    }



    private void lightUpGeneratedPlacement(){
        for (int i=0; i<lightedLEDStringList.size(); i++) {
            if (i == (lightedLEDStringList.size()-1) ){ //last in lightedLEDStringlist
                stringBuffer.add(lightedLEDStringList.get(i)); //show LED since is last, no need to append
                send();
            }
            else{
                String stringToAppend = lightedLEDStringList.get(i);
                String appendedString = stringToAppend.substring(0, 1) + '2' + stringToAppend.substring(2);  //append first byte into "02" //don't show LED (this uses row string)
//                Log.e(TAG, "original string " + stringToAppend);
//                Log.e(TAG, "appendedString: " + appendedString);
                stringBuffer.add(appendedString);
            }
        }


    }

    public void send() {        //this function invoked when ack received
        setConnectionStateConnected();
        if (!stringBuffer.isEmpty()) {    //ensures no message sent if no more string.
            Log.e(TAG, "SENT");
            Log.e(TAG, "charAt(1) = " + stringBuffer.get(0).charAt(1) );
            if (stringBuffer.get(0).charAt(1) == '1' || stringBuffer.get(0).charAt(1) == '3' ) {            //if stringBuffer.length == 1 && opcode == show LED
                Log.e(TAG, "enter charAt(1) = 1");
                LEDMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
                LEDMP3.start();                                     //play sound
                LEDMP3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();

                    };
                });
            }

            serialSend(stringBuffer.remove(0));
        }
    }

    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
        // TODO Auto-generated method stub
        send();
    }



    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    ArrayList<PointXY> temporaryCentrePointList = new ArrayList<PointXY>();

    //get centre XY point of detected colour chips
    private void addChipsCentreToList(List<MatOfPoint> contours , Colours colours) {
        //show which chips are not removed from the board
        temporaryCentrePointList.clear();

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



            centreX = (maxValueX - minValueX) / 2 + minValueX;
            centreY = (maxValueY - minValueY) / 2 + minValueY;
            PointXY centrePoint = new PointXY(centreX, centreY);

//            Log.e(TAG, "max value Y: " + maxValueY);
//            Log.e(TAG, "min value Y: " + minValueY);
//            Log.e(TAG, "difference value Y: " + (maxValueY - minValueY));
//            Log.e(TAG, "");

            temporaryCentrePointList.add(centrePoint);
            //Imgproc.circle(mRgba, centrePoint ,10 , CONTOUR_COLOR);
            //Imgproc.putText(mRgba, String.valueOf(k) , centrePoint, Core.FONT_HERSHEY_PLAIN, 3 , CONTOUR_COLOR);      //show number of chip (specified colour) detected
            //Imgproc.putText(mRgba, String.valueOf(centreX) + ", " + String.valueOf(centreY), centrePoint, Core.FONT_HERSHEY_PLAIN, 3, CONTOUR_COLOR); //show centre x,y of chip (specified colour)
        }

        if (colours == Colours.RED)
            redCentrePointList = (ArrayList<PointXY>) temporaryCentrePointList.clone();
        if (colours == Colours.GREEN)
            greenCentrePointList = (ArrayList<PointXY>) temporaryCentrePointList.clone();
        if (colours == Colours.BLUE)
            blueCentrePointList = (ArrayList<PointXY>) temporaryCentrePointList.clone();
        if (colours == Colours.YELLOW)
            yellowCentrePointList = (ArrayList<PointXY>) temporaryCentrePointList.clone();
    }



    List<MatOfPoint> filteredRedContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> filteredGreenContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> filteredBlueContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> filteredYellowContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> filteredPinkButtonContours = new ArrayList<MatOfPoint>();


    private List<MatOfPoint> filterColourContours(List<MatOfPoint> colouredContourList) {

        ArrayList<MatOfPoint> temporaryFilteredColour = new ArrayList<MatOfPoint>();

        for (int k = 0; k < colouredContourList.size(); k++) {
            points = colouredContourList.get(k).toArray();
            //find minimum value of x and y
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

            //find maximum value of x and y
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

            //filter identified contours to detect properties of circle/square
            if (((maxValueX - minValueX) > xMinChipLength) && ((maxValueX - minValueX) < xMaxChipLength))       //each contour must be within this x length range
                if (((maxValueY - minValueY) > yMinChipLength) && ((maxValueY - minValueY) < yMaxChipLength)) {       //each contour must be within this y length range
                    temporaryFilteredColour.add(colouredContourList.get(k));
                }
        }

        return temporaryFilteredColour;
    }


    protected PointXY getMaxPointXY(List<MatOfPoint> contours) {
        PointXY maxPoint = null;
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
                    valueX = points[i].x;
                }
            }

            maxPoint = new PointXY(valueX, maxValueY);
            //get min cause mirror reflect
            //Log.e(TAG, "min finger value Y: " + minValueY);
            //Log.e(TAG, "min finger value X: " + minValueX);
        }
        return maxPoint;
    }


}
