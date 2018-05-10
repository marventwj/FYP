package com.marven.fyp.memorytraining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.imgproc.Imgproc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.SurfaceView;
import android.widget.Button;

public class CameraCheckChipsRemoved extends BaseActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    //length of each circle light on the board
    public static final int xMinLength = 15; //70
    public static final int xMaxLength = 150; //130
    public static final int yMinLength = 15;
    public static final int yMaxLength = 150; //130

    public static final int fingerBufferLength = 60;

    ProgressDialog progress;
    boolean settingChipsRemoved = false;


    private Button buttonRecalibrateBoard;

    MediaPlayer soundMP3, soundAlignCamera;
    Intent i;

    private boolean pressedStartButton = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar               mBlobColorHsv ,redBlobColorHsv, greenBlobColorHsv, blueBlobColorHsv, yellowBlobColorHsv, fingerBlobColorHsv , pinkButtonBlobColorHsv ,anotherRedBlobColorHsv;
    private ColorBlobDetector    mDetector, redDetector, greenDetector, blueDetector, yellowDetector, fingerDetector , pinkButtonDetector, anotherRedDetector;


    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;
    MediaPlayer soundRemoveChips;
    Timer timer = new Timer();

    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CameraCheckChipsRemoved.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraCheckChipsRemoved() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //no need?
        setContentView(R.layout.activity_camera_check_chips_removed);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view1);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);   //no need?
        mOpenCvCameraView.setCvCameraViewListener(this);



        buttonRecalibrateBoard = (Button) findViewById(R.id.buttonRecalibrateBoard);
        buttonRecalibrateBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "RE-CALIBRATE BOARD PRESSED");
                DataHolder.initBoardMatrix();   //set board matrix to null
            }
        });

        if (DataHolder.getGameSelected() == 1)
            i = new Intent(this, Game1and3.class);
        if (DataHolder.getGameSelected() == 2)
            i = new Intent(this, Game2.class);//intent to game 2
        if (DataHolder.getGameSelected() == 3)
            i = new Intent(this, Game1and3.class);//intent to game 3
//        if (gameSelected == 4)
//            i = new Intent(this, Game4.class);//intent to game 4



        soundRemoveChips = MediaPlayer.create(this, R.raw.please_remove_the_chips_from_the_board);
        soundMP3 = MediaPlayer.create(this, R.raw.press_start_to_begin);
        soundAlignCamera = MediaPlayer.create(this,R.raw.please_align_the_camera_to_the_board);

        if (DataHolder.getNaturalReaderSoundStatus()) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    soundMP3.start();
//                soundMP3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    public void onCompletion(MediaPlayer mp) {
//                        mp.release();
//
//                    };
//                });
                }
            }, 2000);    //in millis
        }
    }




    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
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
        System.out.println("CameraCheckChipsRemoved ON BACKPRESS  !@#!#@#!@#!#!@");
    }

    private Mat mHierarchy;
    private Mat mIntermediateMat;

    public void onCameraViewStarted(int width, int height) {
        mHierarchy = new Mat();
        mIntermediateMat = new Mat();

        mRgba = new Mat(height, width, CvType.CV_8UC4);

        mDetector = new ColorBlobDetector();    //64 circle intensity detector
        redDetector = new ColorBlobDetector();
        greenDetector = new ColorBlobDetector();
        blueDetector = new ColorBlobDetector();
    //    anotherRedDetector = new ColorBlobDetector();
//        yellowDetector = new ColorBlobDetector();
//        fingerDetector = new ColorBlobDetector();
//        pinkButtonDetector = new ColorBlobDetector();

        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        redBlobColorHsv = new Scalar(255);
        greenBlobColorHsv = new Scalar(255);
        blueBlobColorHsv = new Scalar(255);
     //   anotherRedBlobColorHsv = new Scalar(255);
//        yellowBlobColorHsv = new Scalar(255);
//        fingerBlobColorHsv = new Scalar ( 255);
//        pinkButtonBlobColorHsv = new Scalar (255);

        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(360, 0, 0, 255);


        //254,224,109 - old red chip
        redBlobColorHsv.val[0] = 245;   //H  //250
        redBlobColorHsv.val[1] = 171;   //S   //136
        redBlobColorHsv.val[2] = 185;   //V
        //3 194 150             //radius 5 40 110           -red magnet without light
        //245 171 185           //radius 25 85 70           -red magnet with light


//        anotherRedBlobColorHsv.val[0] = 0;
//        anotherRedBlobColorHsv.val[1] = 0;
//        anotherRedBlobColorHsv.val[2] = 0;


        //hsv : 95, 126, 134, 0 - old green chip
        greenBlobColorHsv.val[0] = 96;  //H
        greenBlobColorHsv.val[1] = 169; //S
        greenBlobColorHsv.val[2] = 165; //V
        //82 180 140            //radius 15 50 110          -green magnet without light
        //96 149 165            //radius 25 100 110         -green magnet with white dim light

        //hsv : 143, 143, 138, 0 -  old blue chip
        blueBlobColorHsv.val[0] = 163;  //H
        blueBlobColorHsv.val[1] = 172;  //S
        blueBlobColorHsv.val[2] = 145;   //V
        //177 157 130           //radius 15 50 110          -blue magnet without light
        //163 172 205           //radius 25 85 50           -blue magnet with white dim light

        //hsv : 41, 163, 125, 0 - old yellow chip
//        yellowBlobColorHsv.val[0] = 19;  //H        //50 previously when chip is bright        //30 for dark
//        yellowBlobColorHsv.val[1] = 182; //S
//        yellowBlobColorHsv.val[2] = 180; //V

//        fingerBlobColorHsv.val[0] = 15;   //H
//        fingerBlobColorHsv.val[1] = 143;   //S
//        fingerBlobColorHsv.val[2] = 137;   //V   //239

//        pinkButtonBlobColorHsv.val[0] = 246;   //H
//        pinkButtonBlobColorHsv.val[1] = 86;   //S
//        pinkButtonBlobColorHsv.val[2] = 168;   //V

        //converts hsv scalar value to rgba scalar value
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);

        //values are capped at (255,255,255,255[this value is always 255 in most case, on websites, 255 may refer to '1'])
        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");

        //15,50,255 currently

        redDetector.setColorRadius(new Scalar(25,120,120,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
 //       anotherRedDetector.setColorRadius(new Scalar(0,0,0,0));
        greenDetector.setColorRadius(new Scalar(25,80,110,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        blueDetector.setColorRadius(new Scalar(45,140,110,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
     //   yellowDetector.setColorRadius(new Scalar(5,50,110,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
     //   fingerDetector.setColorRadius(new Scalar(5,50,110,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
     //   pinkButtonDetector.setColorRadius(new Scalar(25,30,90,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance

        //mDetector.setHsvColor(mBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        redDetector.setHsvColor(redBlobColorHsv);            //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
   //     anotherRedDetector.setHsvColor(anotherRedBlobColorHsv);
        greenDetector.setHsvColor(greenBlobColorHsv);        //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        blueDetector.setHsvColor(blueBlobColorHsv);          //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
     //   yellowDetector.setHsvColor(yellowBlobColorHsv);      //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
     //   fingerDetector.setHsvColor(fingerBlobColorHsv);
     //   pinkButtonDetector.setHsvColor(pinkButtonBlobColorHsv);

        //Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);      //resize the image to specture size
        Imgproc.resize(redDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
//        Imgproc.resize(anotherRedDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(greenDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);      //resize the image to specture size
        Imgproc.resize(blueDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
     //   Imgproc.resize(yellowDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
     //   Imgproc.resize(fingerDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
     //   Imgproc.resize(pinkButtonDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

    }

    public void onCameraViewStopped() {
        mRgba.release();
    }


    public boolean onTouch(View v, MotionEvent event) {     //simulate touch of screen to be start button pressed.
        Log.e(TAG, "Pressed Start Button");
        pressedStartButton = true;
        return false;
    }

    Point[] points;
    double minValueX, minValueY, maxValueX, maxValueY, centreX, centreY , valueX , valueY, handMinX, handMaxX;
    List<MatOfPoint> contours;

    List<MatOfPoint> redContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> anotherRedContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> greenContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> blueContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> yellowContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> fingerContours;
    List<MatOfPoint> pinkButtonContours;

    ArrayList<PointXY> boardCentrePoints = new ArrayList<PointXY>();
    ArrayList<PointXY> rowPoints = new ArrayList<PointXY>();
    PointXY[][] boardMatrix = new PointXY[8][8];
    boolean chipsRemoved = false;

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        //take the frame rgba mat object
        mRgba = inputFrame.rgba();

        //when you change the parameters here, remember to change the parameters for verifly results also
        int rows = (int) mRgba.size().height;
        int cols = (int) mRgba.size().width;
        int left = cols / 18;
        int top = rows / 8;
        int width = cols *2 / 3;
        int height = rows *3 / 4;
        Mat rgbaInnerWindow;
        rgbaInnerWindow = mRgba.submat(0, rows , 0, width);      // 0 to row is from top to down, col is from left to right. if change this, verify result there also must change

        //draw rectangle to show regions that will be detected
        Point start = new Point(0,0);
        Point end = new Point (width, rows);
        Imgproc.rectangle(rgbaInnerWindow,start,end, new Scalar (0,0,255,255) , 5);


        //if chipsremoved, do the processing for light intensity.
        if (chipsRemoved) {

            if (DataHolder.getBoardMatrix()[0][0] != null) {     //if already check board before
                startActivity(i);
            }
            else {

                Log.e(TAG, "Chips is removed, processing to check 64 circles.");
                //detect for 64 circles
                mDetector.processForLightIntensity(rgbaInnerWindow);
                contours = mDetector.getContours();
                filterContours();
                Imgproc.drawContours(rgbaInnerWindow, contours, -1, new Scalar(255, 0, 0, 0)); //draw contours on the original image itself
                Log.e(TAG, "Processed light intensity Contour size:" + contours.size());
                Log.e(TAG, "board centre size after filtering:" + boardCentrePoints.size());
                //clear all LEDs after board checking done
                serialSend("0300000000CCFFFFFFFFFFFFFFFF");
                //check if 64 circles detected
                if (boardCentrePoints.size() == 64) {
            //    if (boardCentrePoints.size() > 1) {
                    //sort the 64 centrepoints in ascending order
                    Collections.sort(boardCentrePoints, new Comparator<PointXY>() {
                        public int compare(PointXY o1, PointXY o2) {
                            return Double.compare(o1.y(), o2.y());
                        }
                    });

                    for (int i = 0; i < 8; i++) {
                        //add 8 points (all in the same row) into arraylist rowPoints
                        for (int j = 0; j < 8; j++)
                            rowPoints.add(boardCentrePoints.remove(0));
          //                  rowPoints.add(new PointXY (2,2));

                        Collections.sort(rowPoints, new Comparator<PointXY>() {
                            public int compare(PointXY o1, PointXY o2) {
                                return Double.compare(o1.x(), o2.x());
                            }
                        });

                        //points assigned to the board accordingly
                        for (int j = 0; j < 8; j++)
                            boardMatrix[i][j] = rowPoints.remove(0);
                    }

                    //see centrepoints of x y board.
                    for (int a = 0; a < 8; a++)
                        for (int b = 0; b < 8; b++) {
                            Log.e(TAG, "x" + a + ": " + boardMatrix[a][b].x() + "y" + b + ": " + boardMatrix[a][b].y());
                        }

                    //i.putExtra("boardMatrix", boardMatrix);
                    DataHolder.setBoardMatrix(boardMatrix);
                    startActivity(i);
                } else {
                    //please align your phones camera to the board
                    if (DataHolder.getNaturalReaderSoundStatus()) {
                        if (!soundMP3.isPlaying() && !soundRemoveChips.isPlaying()) {
                            soundAlignCamera.start();
                        }
                    }

                    //set chips boolean as not removed
                    chipsRemoved = false;
                    //don't check start button for this frame that LED light up.
                    return mRgba;
                }

            }

        } //chips removed

        //detecting chips
        redDetector.process(rgbaInnerWindow);
  //      anotherRedDetector.process(rgbaInnerWindow);
        greenDetector.process(rgbaInnerWindow);
        blueDetector.process(rgbaInnerWindow);
//        yellowDetector.process(mRgba);

        //get the contours after detector.process
        redContours = redDetector.getContours();
  //      anotherRedDetector.getContours();
        greenContours = greenDetector.getContours();
        blueContours = blueDetector.getContours();
//        yellowContours = yellowDetector.getContours();

        filteredRedContours = filterColourContours(redContours, false);
//        filteredAnotherRedContours = filterColourContours(anotherRedContours,false);
        filteredGreenContours = filterColourContours(greenContours, false);
        filteredBlueContours = filterColourContours(blueContours, false);
//        filteredYellowContours = filterColourContours(yellowContours,false );

   //     Imgproc.drawContours(mRgba, redContours, -1, CONTOUR_COLOR);   //draw red contours on the screen
   //     Imgproc.drawContours(mRgba, greenContours, -1,  new Scalar(0,255,0,255));   //draw red contours on the screen
   //     Imgproc.drawContours(mRgba, blueContours, -1,  new Scalar(0,0,255,255));   //draw red contours on the screen
   //     Imgproc.drawContours(mRgba, yellowContours, -1,  new Scalar(255,255,0,255));   //draw red contours on the screen

        Imgproc.drawContours(rgbaInnerWindow, filteredRedContours, -1, CONTOUR_COLOR);   //draw red contours on the screen
  //      Imgproc.drawContours(rgbaInnerWindow, filteredAnotherRedContours, -1, CONTOUR_COLOR);   //draw red contours on the screen
        Imgproc.drawContours(rgbaInnerWindow, filteredGreenContours, -1,  new Scalar(0,255,0,255));   //draw green contours on the screen
        Imgproc.drawContours(rgbaInnerWindow, filteredBlueContours, -1,  new Scalar(0,0,255,255));   //draw blue contours on the screen
//        Imgproc.drawContours(mRgba, filteredYellowContours, -1,  new Scalar(255,255,0,255));   //draw yellow contours on the screen


        if (pressedStartButton) {      //simulate start button pressed
//            Log.e(TAG, "red Contours count: " + redContours.size());
//            Log.e(TAG, "green Contours count: " + greenContours.size());
//            Log.e(TAG, "blue Contours count: " + blueContours.size());
//            Log.e(TAG, "yellow Contours count: " + yellowContours.size());

            //print size of each colour contours detected
            Log.e(TAG, "filtered red Contours count: " + filteredRedContours.size());
            Log.e(TAG, "filtered green Contours count: " + filteredGreenContours.size());
            Log.e(TAG, "filtered blue Contours count: " + filteredBlueContours.size());
//            Log.e(TAG, "filtered yellow Contours count: " + filteredYellowContours.size());

            //game 2 no need to check if chips removed.
            //if chips removed, set a chips removed boolean. light up the LEDs,delay, then go to next frame. at next frame, if chips removed boolean set, process for light intensity
           // if ( (filteredRedContours.size() + filteredGreenContours.size() + filteredBlueContours.size() + filteredYellowContours.size()) == 0 || DataHolder.getGameSelected() == 2) {
            if ( (filteredRedContours.size() + filteredGreenContours.size() + filteredBlueContours.size()) == 0 || DataHolder.getGameSelected() == 2 && !settingChipsRemoved) {


                if (DataHolder.getBoardMatrix()[0][0] == null) {     //if board matrix not calibrated before

                    settingChipsRemoved = true;

                    //on LED
                    setConnectionStateConnected();
                    serialSend("03000000FFCCFFFFFFFFFFFFFFFF");

                    runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                    progress = new ProgressDialog(CameraCheckChipsRemoved.this);
                    progress.setMessage("Calibrating Board...");
                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress.show();
                    }
                    });

                    //start timer
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            chipsRemoved = true;
                            settingChipsRemoved = false;
                        }
                    },5000);    //in millis

                }
                else {
                    //chips removed at here, set chips boolean as removed and will go to next frame.
                      chipsRemoved = true;
                }
            }

             else {                            //if chips not removed, play sound to warn user to remove chips

                if (DataHolder.getNaturalReaderSoundStatus()) {
                    if (!soundMP3.isPlaying()) {
                        soundRemoveChips.start();
                        soundRemoveChips.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                soundMP3.start();
                            }

                            ;
                        });
                    }
                    ;
                }

                //set chips boolean as not removed
                chipsRemoved = false;
            }

        }
        pressedStartButton = false;
        return mRgba;
    }

    List<MatOfPoint> filteredContours = new ArrayList<MatOfPoint>();

    private void filterContours() {     //filter and add boardcentrepoints to list
        filteredContours.clear();
        boardCentrePoints.clear();
        for (int k = 0; k < contours.size(); k++) {
            points = contours.get(k).toArray();
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

            //find centre of x and y
            centreX = (maxValueX - minValueX) / 2 + minValueX;
            centreY = (maxValueY - minValueY) / 2 + minValueY;
            PointXY centrePoint = new PointXY(centreX, centreY);
            //filter identified contours to detect properties of circle/square
            if (((maxValueX - minValueX) > xMinLength) && ((maxValueX - minValueX) < xMaxLength)){       //each contour must be within this x length range
                if (((maxValueY - minValueY) > yMinLength) && ((maxValueY - minValueY) < yMaxLength)) {       //each contour must be within this y length range
                    boardCentrePoints.add(centrePoint);
                }
            }
        }
    }

    List<MatOfPoint> filteredRedContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> filteredAnotherRedContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> filteredGreenContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> filteredBlueContours = new ArrayList<MatOfPoint>();
//    List<MatOfPoint> filteredYellowContours = new ArrayList<MatOfPoint>();
//    List<MatOfPoint> filteredPinkButtonContours = new ArrayList<MatOfPoint>();
//    List<MatOfPoint> filteredFingerContours = new ArrayList<MatOfPoint>();


    private List<MatOfPoint> filterColourContours(List<MatOfPoint> colouredContourList, boolean isPink) {

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
            if (((maxValueX - minValueX) > DataHolder.getxMinChipLength()) && ((maxValueX - minValueX) < DataHolder.getxMaxChipLength()))       //each contour must be within this x length range
                if (((maxValueY - minValueY) > DataHolder.getyMinChipLength()) && ((maxValueY - minValueY) < DataHolder.getyMaxChipLength())) {       //each contour must be within this y length range

                    if (!isPink) {
  //                      Log.e(TAG, "hand min X " + handMinX);
//                        Log.e(TAG, "max value X" + maxValueX);

//                        if (handMinX > maxValueX) {
                            temporaryFilteredColour.add(colouredContourList.get(k));
//                        }
                    }
                    else{
                        temporaryFilteredColour.add(colouredContourList.get(k));
                    }
                }
        }

        return temporaryFilteredColour;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }


    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
        //  TODO Auto-generated method stub

        //        if message from bluno indicating pressed start button,
        if (theString.equals("BUTTON PRESSED")){
            Log.e(TAG, "button is pressed on check chips!!!!!!!!!");
            pressedStartButton = true;
        }

    }


}
