package com.marven.fyp.memorytraining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
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
import org.opencv.imgproc.Imgproc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceView;

public class VerifyResults extends BaseActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String  TAG              = "OCVSample::Activity";
    public static final int chipBufferLengthFromCentre = 25;

    public enum Colours {
        RED,
        GREEN,
        BLUE,
        YELLOW;
    }

    Intent i;
    int level, gameSelected;
    boolean win;

    PointXY[][] boardMatrix = new PointXY[8][8];
    char [][] chipPlacement = new char[8][8];
    char[][] generatedPlacement = new char[8][8];
    ArrayList<String> lightedLEDStringList = new ArrayList<String>();
    ArrayList<String> stringBuffer = new ArrayList<String>();

    MediaPlayer placeTheChipsMP3;
    MediaPlayer soundMP3;

    private boolean              mIsColorSelected = false;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar               mBlobColorHsv ,redBlobColorHsv, greenBlobColorHsv, blueBlobColorHsv, yellowBlobColorHsv;
    private ColorBlobDetector    mDetector, redDetector, greenDetector, blueDetector, yellowDetector;
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //no need?
        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);   //no need?
        mOpenCvCameraView.setCvCameraViewListener(this);

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

        level =  getIntent().getIntExtra("Level",0);
        gameSelected =  getIntent().getIntExtra("GameSelected",0);

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
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        redBlobColorHsv = new Scalar(255);
        greenBlobColorHsv = new Scalar(255);
        blueBlobColorHsv = new Scalar(255);
        yellowBlobColorHsv = new Scalar(255);

        SPECTRUM_SIZE = new Size(200, 64);
        CONTOUR_COLOR = new Scalar(255,0,0,255);



        //hsv : x, x, x, 0 - red chip
        redBlobColorHsv.val[0] = 254;  //H  //250
        redBlobColorHsv.val[1] = 224; //S   //136
        redBlobColorHsv.val[2] = 109; //V

        //hsv : 95, 126, 164, 0 - green chip
        greenBlobColorHsv.val[0] = 95;  //H
        greenBlobColorHsv.val[1] = 126; //S
        greenBlobColorHsv.val[2] = 164; //V

        //hsv : 148, 218, 104, 0 - blue chip
        blueBlobColorHsv.val[0] = 143;  //H
        blueBlobColorHsv.val[1] = 143; //S
        blueBlobColorHsv.val[2] = 98; //V

        //hsv : 41, 143, 145, 0 - yellow chip
        yellowBlobColorHsv.val[0] = 50;  //H
        yellowBlobColorHsv.val[1] = 143; //S
        yellowBlobColorHsv.val[2] = 145; //V

        //converts hsv scalar value to rgba scalar value
        mBlobColorRgba = converScalarHsv2Rgba(mBlobColorHsv);
        //values are capped at (255,255,255,255[this value is always 255 in most case, on websites, 255 may refer to '1'])
        Log.i(TAG, "Touched rgba color: (" + mBlobColorRgba.val[0] + ", " + mBlobColorRgba.val[1] +
                ", " + mBlobColorRgba.val[2] + ", " + mBlobColorRgba.val[3] + ")");


        redDetector.setColorRadius(new Scalar(15,50,255,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        greenDetector.setColorRadius(new Scalar(15,50,255,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        blueDetector.setColorRadius(new Scalar(15,50,255,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance
        yellowDetector.setColorRadius(new Scalar(15,50,255,0));   //default is 25,50,50,0. set 2nd and 3rd parameter to adjust greyness / brightness acceptance

        //mDetector.setHsvColor(mBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        redDetector.setHsvColor(redBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        greenDetector.setHsvColor(greenBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        blueDetector.setHsvColor(blueBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        yellowDetector.setHsvColor(yellowBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?

        //Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(redDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(greenDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);      //resize the image to specture size
        Imgproc.resize(blueDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);
        Imgproc.resize(yellowDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public boolean onTouch(View v, MotionEvent event) {     //when press on camera screen

        Log.e(TAG, "TOUCHED");

        int cols = mRgba.cols();    //get resolution of display ( cols value is 1280 )
        int rows = mRgba.rows();    //get resolution of display  ( rows value is 720 )

        int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;    //get resolution of display (mOpenCvCameraView.getWidth() value is 1920 , xOffset value is 320 )
        int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;   //get resolution of display (mOpenCvCameraView.getHeight() value is 1005 , yOffset value is 142 )

        int x = (int)event.getX() - xOffset;    //get resolution of display (event.getX is y axis, most top lesser value, more btm higher value ~?)
        int y = (int)event.getY() - yOffset;    //get resolution of display (event.getY is x axis, most right lesser value, most left higher value ~?)

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
        for (int i = 0; i < mBlobColorHsv.val.length; i++) {
            mBlobColorHsv.val[i] /= pointCount;
            Log.e(TAG, "touched HSV color: " + mBlobColorHsv.val[i]);
            //System.out.println("touched HSV color: " + mBlobColorHsv.val[i]);
        }

//////////colour detection starts from here.-----------------------------------------------------

        //with red LED on for green chip
//        mBlobColorHsv.val[0] = 67;  //H
//        mBlobColorHsv.val[1] = 204; //S
//        mBlobColorHsv.val[2] = 131; //V

        //mDetector.setHsvColor(mBlobColorHsv);              //mDetector is ColorBlobDetector object, tell the detector to detect this colour?
        //Imgproc.resize(mDetector.getSpectrum(), mSpectrum, SPECTRUM_SIZE);

        mIsColorSelected = true;    //simulate button pressed
        //release all mats
        touchedRegionRgba.release();
        touchedRegionHsv.release();

        return false; // don't need subsequent touch events
    }

    Point[] points;
    double minValueX, minValueY, maxValueX, maxValueY, centreX, centreY;
    List<MatOfPoint> mContours = new ArrayList<MatOfPoint>();
    List<MatOfPoint> redContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> greenContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> blueContours;// = new ArrayList<MatOfPoint>();
    List<MatOfPoint> yellowContours;// = new ArrayList<MatOfPoint>();

    ArrayList<PointXY> redCentrePointList = new ArrayList<PointXY>();
    ArrayList<PointXY> greenCentrePointList = new ArrayList<PointXY>();
    ArrayList<PointXY> blueCentrePointList = new ArrayList<PointXY>();
    ArrayList<PointXY> yellowCentrePointList = new ArrayList<PointXY>();

    double redCentrePointX, redCentrePointY, greenCentrePointX, greenCentrePointY, blueCentrePointX , blueCentrePointY, yellowCentrePointX, yellowCentrePointY;


    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mRgba = inputFrame.rgba();

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

        Imgproc.drawContours(mRgba, redContours, -1, CONTOUR_COLOR);   //draw red contours on the screen
        Imgproc.drawContours(mRgba, greenContours, -1,  new Scalar(0,255,0,255));   //draw green contours on the screen
        Imgproc.drawContours(mRgba, blueContours, -1,  new Scalar(0,0,255,255));   //draw blue contours on the screen
        Imgproc.drawContours(mRgba, yellowContours, -1,  new Scalar(255,255,0,255));   //yellow red contours on the screen

        //if screen is touched (simulate start button), verify results
        if (mIsColorSelected) {

//            mDetector.process(mRgba);
//            mContours = mDetector.getContours();
//            getChipsCentre(mContours);
//            Log.e(TAG, "Contours count: " + mContours.size());
//            Imgproc.drawContours(mRgba, mContours, -1, CONTOUR_COLOR);   //draw red contours on the screen

            addChipsCentreToList(redContours, Colours.RED);
            addChipsCentreToList(greenContours, Colours.GREEN);
            addChipsCentreToList(blueContours, Colours.BLUE);
            addChipsCentreToList(yellowContours, Colours.YELLOW);

            Log.e(TAG, "red Contours count: " + redContours.size());
            Log.e(TAG, "green Contours count: " + greenContours.size());
            Log.e(TAG, "blue Contours count: " + blueContours.size());
            Log.e(TAG, "yellowContours count: " + yellowContours.size());

            //empty chipPlacementArray first
            for (int i = 0; i < 8; i++) {
                Arrays.fill(chipPlacement[i], '0');
            }
            //identifying chips position on the board
            for (int i = 0; i < redCentrePointList.size(); i++) {   //check all red chips
                redCentrePointX = redCentrePointList.get(i).x();
                redCentrePointY = redCentrePointList.get(i).y();
                for (int j = 0; j < 8; j++) {       //iterate through first column
                    if ((boardMatrix[j][0].y() > (redCentrePointY - chipBufferLengthFromCentre)) && (boardMatrix[j][0].y() < (redCentrePointY + chipBufferLengthFromCentre))) { //use first column as reference to check which column the chip lies on
                        //check X
                        for (int k = 0; k < 8; k++) {    //iterate through the rows
                            if ((boardMatrix[j][k].x() > (redCentrePointX - chipBufferLengthFromCentre)) && (boardMatrix[j][k].x() < (redCentrePointX + chipBufferLengthFromCentre))) {
                                chipPlacement[j][k] = 'r';
                                break;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < greenCentrePointList.size(); i++) {   //check all green chips
                greenCentrePointX = greenCentrePointList.get(i).x();
                greenCentrePointY = greenCentrePointList.get(i).y();
                for (int j = 0; j < 8; j++) {       //iterate through first column
                    if ((boardMatrix[j][0].y() > (greenCentrePointY - chipBufferLengthFromCentre)) && (boardMatrix[j][0].y() < (greenCentrePointY + chipBufferLengthFromCentre))) { //use first column as reference to check which column the chip lies on
                        //check X
                        for (int k = 0; k < 8; k++) {    //iterate through the rows
                            if ((boardMatrix[j][k].x() > (greenCentrePointX - chipBufferLengthFromCentre)) && (boardMatrix[j][k].x() < (greenCentrePointX + chipBufferLengthFromCentre))) {
                                chipPlacement[j][k] = 'g';
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
                                break;
                            }
                        }
                    }
                }
            }

//            //verify chip placement
//            for (int j=0; j<8; j++)
//                for (int k = 0; k<8; k++) {
//                    if (chipPlacement[j][k] != '0')
//                     Log.e(TAG, "Position " + j + "" + k + " colour: " + chipPlacement[j][k]);
//                }

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
                level += 1; //next level
            }
            else {
                Log.e(TAG, "user lost!");//user loses
                level = 1; //restart level
                win = false;
            }

            //put extra to next activity
            i.putExtra("Level",level);
            i.putExtra("win", win);
            i.putExtra("GameSelected", gameSelected);
            startActivity(i);
        }

        mIsColorSelected = false;
        return mRgba;
    }


    private void lightUpGeneratedPlacement(){
        for (int i=0; i<lightedLEDStringList.size(); i++) {
            if (i == (lightedLEDStringList.size()-1) ){ //last in lightedLEDStringlist
                stringBuffer.add(lightedLEDStringList.get(i)); //show LED since is last, no need to append
                send();
            }
            else{
                String stringToAppend = lightedLEDStringList.get(i);
                String appendedString = stringToAppend.substring(0, 1) + '0' + stringToAppend.substring(2);  //append first byte into "00" //don't show LED
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
            if (stringBuffer.get(0).charAt(1) == '1' ) {            //if stringBuffer.length == 1 && opcode == show LED
                Log.e(TAG, "enter charAt(1) = 1");
                soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
                soundMP3.start();                                     //play sound
                soundMP3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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


}
