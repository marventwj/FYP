package com.marven.fyp.memorytraining;

import java.util.ArrayList;
import java.util.Arrays;

public class DataHolder {
//    private static String data = "no";
//    public static String getData() {return data;}
//    public static void setData(String data) {DataHolder.data = data;}

    private static boolean naturalReaderSoundStatus = true;
    public static boolean getNaturalReaderSoundStatus(){return naturalReaderSoundStatus;}
    public static void setNaturalReaderSoundStatus (boolean naturalReaderSoundStatus ) {DataHolder.naturalReaderSoundStatus = naturalReaderSoundStatus;}

    private static boolean backgroundMusicStatus = true;
    public static boolean getBackgroundMusicStatus(){return backgroundMusicStatus;}
    public static void setBackgroundMusicStatus (boolean backgroundMusicStatus ) {DataHolder.backgroundMusicStatus = backgroundMusicStatus;}


    public enum Mode {
        EASY,
        MEDIUM,
        HARD,
    }

    private static PointXY[][] boardMatrix = new PointXY[8][8];
    public static void initBoardMatrix () {
        for (int i = 0; i < 8; i++) {
            Arrays.fill(boardMatrix[i], null);
        }
    }
    public static PointXY[][] getBoardMatrix(){return boardMatrix;}
    public static void setBoardMatrix (PointXY[][] boardMatrix ) {
        for(int i=0; i<boardMatrix.length; i++)
             for(int j=0; j<boardMatrix[i].length; j++)
                DataHolder.boardMatrix[i][j]=boardMatrix[i][j];
    }

    //set these to filter away unwanted small contours
    private static int xMinChipLength = 20;
    private static int xMaxChipLength = 130;
    private static int yMinChipLength = 20;
    private static int yMaxChipLength = 170;

    public static int getxMinChipLength(){return xMinChipLength;}
    public static int getxMaxChipLength(){return xMaxChipLength;}
    public static int getyMinChipLength(){return yMinChipLength;}
    public static int getyMaxChipLength(){return yMaxChipLength;}

    private static int gameSelected, level;

    public static int getGameSelected(){return gameSelected;}
    public static void setGameSelected (int gameSelected ) {DataHolder.gameSelected = gameSelected;}

    public static int getLevel(){return level;}
    public static void setLevel (int level ) {DataHolder.level = level;}

    private static boolean bluetoothConnected = false;
    public static boolean getBluetoothConnected() {return bluetoothConnected;}
    public static void setBluetoothConnected(boolean bluetoothConnected) {DataHolder.bluetoothConnected = bluetoothConnected;}

    private static int score = 0;
    public static int getScore() {return score;}
    public static void setScore(int score) {DataHolder.score = score;}

    private static int boardSize = 8;
    public static int getBoardSize() {return boardSize;}
    public static void setBoardSize(int boardSize) {DataHolder.boardSize = boardSize;}


    private static Mode mode = Mode.EASY;
    public static Mode getMode() {return mode;}
    public static void setMode(Mode mode) {DataHolder.mode = mode;}

    //easy mode
    private static int easyLevel1Time = 6;  //in seconds
    private static int easyLevel2Time = 6;
    private static int easyLevel3Time = 6;

    private static int easyLevel1NumLED = 3;
    private static int easyLevel2NumLED = 4;
    private static int easyLevel3NumLED = 5;

    private static int easyLevel1NumColour = 2;
    private static int easyLevel2NumColour = 2;
    private static int easyLevel3NumColour = 2;

    //medium mode
    private static int mediumLevel1Time = 5;
    private static int mediumLevel2Time = 5;
    private static int mediumLevel3Time = 5;

    private static int mediumLevel1NumLED = 5;
    private static int mediumLevel2NumLED = 6;
    private static int mediumLevel3NumLED = 7;

    private static int mediumLevel1NumColour = 3;
    private static int mediumLevel2NumColour = 3;
    private static int mediumLevel3NumColour = 3;

    //hard mode
    private static int hardLevel1Time = 4;
    private static int hardLevel2Time = 4;
    private static int hardLevel3Time = 4;

    private static int hardLevel1NumLED = 6;
    private static int hardLevel2NumLED = 7;
    private static int hardLevel3NumLED = 8;

    private static int hardLevel1NumColour = 4;
    private static int hardLevel2NumColour = 4;
    private static int hardLevel3NumColour = 4;

    //easy mode get set methods
    public static int getEasyLevel1Time() {return easyLevel1Time;}
    public static void setEasyLevel1Time(int easyLevel1Time) {DataHolder.easyLevel1Time = easyLevel1Time;}
    public static int getEasyLevel2Time() {return easyLevel2Time;}
    public static void setEasyLevel2Time(int easyLevel2Time) {DataHolder.easyLevel2Time = easyLevel2Time;}
    public static int getEasyLevel3Time() {return easyLevel3Time;}
    public static void setEasyLevel3Time(int easyLevel3Time) {DataHolder.easyLevel3Time = easyLevel3Time;}

    public static int getEasyLevel1NumLED() {return easyLevel1NumLED;}
    public static void setEasyLevel1NumLED(int easyLevel1NumLED) {DataHolder.easyLevel1NumLED = easyLevel1NumLED;}
    public static int getEasyLevel2NumLED() {return easyLevel2NumLED;}
    public static void setEasyLevel2NumLED(int easyLevel2NumLED) {DataHolder.easyLevel2NumLED = easyLevel2NumLED;}
    public static int getEasyLevel3NumLED() {return easyLevel3NumLED;}
    public static void setEasyLevel3NumLED(int easyLevel3NumLED) {DataHolder.easyLevel3NumLED = easyLevel3NumLED;}

    public static int getEasyLevel1NumColour() {return easyLevel1NumColour;}
    public static void setEasyLevel1NumColour(int easyLevel1NumColour) {DataHolder.easyLevel1NumColour = easyLevel1NumColour;}
    public static int getEasyLevel2NumColour() {return easyLevel2NumColour;}
    public static void setEasyLevel2NumColour(int easyLevel2NumColour) {DataHolder.easyLevel2NumColour = easyLevel2NumColour;}
    public static int getEasyLevel3NumColour() {return easyLevel3NumColour;}
    public static void setEasyLevel3NumColour(int easyLevel3NumColour) {DataHolder.easyLevel3NumColour = easyLevel3NumColour;}

    //medium mode get set methods
    public static int getMediumLevel1Time() {return mediumLevel1Time;}
    public static void setMediumLevel1Time(int mediumLevel1Time) {DataHolder.mediumLevel1Time = mediumLevel1Time;}
    public static int getMediumLevel2Time() {return mediumLevel2Time;}
    public static void setMediumLevel2Time(int mediumLevel2Time) {DataHolder.mediumLevel2Time = mediumLevel2Time;}
    public static int getMediumLevel3Time() {return mediumLevel3Time;}
    public static void setMediumLevel3Time(int mediumLevel3Time) {DataHolder.mediumLevel3Time = mediumLevel3Time;}

    public static int getMediumLevel1NumLED() {return mediumLevel1NumLED;}
    public static void setMediumLevel1NumLED(int mediumLevel1NumLED) {DataHolder.mediumLevel1NumLED = mediumLevel1NumLED;}
    public static int getMediumLevel2NumLED() {return mediumLevel2NumLED;}
    public static void setMediumLevel2NumLED(int mediumLevel2NumLED) {DataHolder.mediumLevel2NumLED = mediumLevel2NumLED;}
    public static int getMediumLevel3NumLED() {return mediumLevel3NumLED;}
    public static void setMediumLevel3NumLED(int mediumLevel3NumLED) {DataHolder.mediumLevel3NumLED = mediumLevel3NumLED;}

    public static int getMediumLevel1NumColour() {return mediumLevel1NumColour;}
    public static void setMediumLevel1NumColour(int mediumLevel1NumColour) {DataHolder.mediumLevel1NumColour = mediumLevel1NumColour;}
    public static int getMediumLevel2NumColour() {return mediumLevel2NumColour;}
    public static void setMediumLevel2NumColour(int mediumLevel2NumColour) {DataHolder.mediumLevel2NumColour = mediumLevel2NumColour;}
    public static int getMediumLevel3NumColour() {return mediumLevel3NumColour;}
    public static void setMediumLevel3NumColour(int mediumLevel3NumColour) {DataHolder.mediumLevel3NumColour = mediumLevel3NumColour;}

    //hard mode get set methods
    public static int getHardLevel1Time() {return hardLevel1Time;}
    public static void setHardLevel1Time(int hardLevel1Time) {DataHolder.hardLevel1Time = hardLevel1Time;}
    public static int getHardLevel2Time() {return hardLevel2Time;}
    public static void setHardLevel2Time(int hardLevel2Time) {DataHolder.hardLevel2Time = hardLevel2Time;}
    public static int getHardLevel3Time() {return hardLevel3Time;}
    public static void setHardLevel3Time(int hardLevel3Time) {DataHolder.hardLevel3Time = hardLevel3Time;}

    public static int getHardLevel1NumLED() {return hardLevel1NumLED;}
    public static void setHardLevel1NumLED(int hardLevel1NumLED) {DataHolder.hardLevel1NumLED = hardLevel1NumLED;}
    public static int getHardLevel2NumLED() {return hardLevel2NumLED;}
    public static void setHardLevel2NumLED(int hardLevel2NumLED) {DataHolder.hardLevel2NumLED = hardLevel2NumLED;}
    public static int getHardLevel3NumLED() {return hardLevel3NumLED;}
    public static void setHardLevel3NumLED(int hardLevel3NumLED) {DataHolder.hardLevel3NumLED = hardLevel3NumLED;}

    public static int getHardLevel1NumColour() {return hardLevel1NumColour;}
    public static void setHardLevel1NumColour(int hardLevel1NumColour) {DataHolder.hardLevel1NumColour = hardLevel1NumColour;}
    public static int getHardLevel2NumColour() {return hardLevel2NumColour;}
    public static void setHardLevel2NumColour(int hardLevel2NumColour) {DataHolder.hardLevel2NumColour = hardLevel2NumColour;}
    public static int getHardLevel3NumColour() {return hardLevel3NumColour;}
    public static void setHardLevel3NumColour(int hardLevel3NumColour) {DataHolder.hardLevel3NumColour = hardLevel3NumColour;}


}