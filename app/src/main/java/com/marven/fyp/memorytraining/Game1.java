package com.marven.fyp.memorytraining;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class Game1 extends BaseActivity {

    private static final String TAG = "OCVSample::Activity";
    //int level , gameSelected = 1;
    private TextView topTextView;
    ArrayList<String> stringBuffer = new ArrayList<String>();
    ArrayList<String> lightedLEDStringList = new ArrayList<String>();
    Timer timer = new Timer();
    int delay = 0;
    Intent i;
    MediaPlayer soundMP3, soundPayAttention, soundGetReady, soundThree, soundTwo, soundOne, soundLevelOne, soundLevelTwo, soundLevelThree;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

        soundPayAttention = MediaPlayer.create(this, R.raw.please_pay_attention_to_the_board);
        soundGetReady = MediaPlayer.create(this, R.raw.get_ready);
        soundThree = MediaPlayer.create(this, R.raw.three);
        soundTwo = MediaPlayer.create(this, R.raw.two);
        soundOne = MediaPlayer.create(this, R.raw.one);
        soundLevelThree = MediaPlayer.create(this, R.raw.level_three);
        soundLevelTwo = MediaPlayer.create(this, R.raw.level_two);
        soundLevelOne = MediaPlayer.create(this, R.raw.level_one);
        topTextView = (TextView) findViewById(R.id.topTextView);

        //collect intent
        //level =  getIntent().getIntExtra("Level",0);
        i = new Intent(this, VerifyResults.class);

        //neccessary displays before start of game
        beforeStartDisplay();

        int timeLEDOn=0, numLED=0,numColour = 0;

        //easy mode
        if (DataHolder.getMode() == DataHolder.Mode.EASY) {
            //based on current level, generate the game, delay time is how long it will show on the board
            //if (level == 1) {
            if (DataHolder.getLevel() == 1){
                timeLEDOn = DataHolder.getEasyLevel1Time() * 1000;
                numLED = DataHolder.getEasyLevel1NumLED();
                numColour = DataHolder.getEasyLevel1NumColour();
            //} else if (level == 2) {
            } else if (DataHolder.getLevel() == 2) {
                timeLEDOn = DataHolder.getEasyLevel2Time() * 1000;
                numLED = DataHolder.getEasyLevel2NumLED();
                numColour = DataHolder.getEasyLevel2NumColour();
           // } else if (level == 3) {
            } else if (DataHolder.getLevel() == 3) {
                timeLEDOn = DataHolder.getEasyLevel3Time() * 1000;
                numLED = DataHolder.getEasyLevel3NumLED();
                numColour = DataHolder.getEasyLevel3NumColour();
            }
        }

        //medium mode
        else if (DataHolder.getMode() == DataHolder.Mode.MEDIUM) {
            //based on current level, generate the game, delay time is how long it will show on the board
            //if (level == 1) {
            if (DataHolder.getLevel() == 1){
                timeLEDOn = DataHolder.getMediumLevel1Time() * 1000;
                numLED = DataHolder.getMediumLevel1NumLED();
                numColour = DataHolder.getMediumLevel1NumColour();
            //} else if (level == 2) {
            } else if (DataHolder.getLevel() == 2) {
                timeLEDOn = DataHolder.getMediumLevel2Time() * 1000;
                numLED = DataHolder.getMediumLevel2NumLED();
                numColour = DataHolder.getMediumLevel2NumColour();
            //} else if (level == 3) {
            } else if (DataHolder.getLevel() == 3) {
                timeLEDOn = DataHolder.getMediumLevel3Time() * 1000;
                numLED = DataHolder.getMediumLevel3NumLED();
                numColour = DataHolder.getMediumLevel3NumColour();
            }
        }

        //hard mode
        else if (DataHolder.getMode() == DataHolder.Mode.HARD) {
            //based on current level, generate the game, delay time is how long it will show on the board
            //if (level == 1) {
            if (DataHolder.getLevel() == 1){
                timeLEDOn = DataHolder.getHardLevel1Time() * 1000;
                numLED = DataHolder.getHardLevel1NumLED();
                numColour = DataHolder.getHardLevel1NumColour();
            //} else if (level == 2) {
            } else if (DataHolder.getLevel() == 2) {
                timeLEDOn = DataHolder.getHardLevel2Time() * 1000;
                numLED = DataHolder.getHardLevel2NumLED();
                numColour = DataHolder.getHardLevel2NumColour();
            //} else if (level == 3) {
            } else if (DataHolder.getLevel() == 3) {
                timeLEDOn = DataHolder.getHardLevel3Time() * 1000;
                numLED = DataHolder.getHardLevel3NumLED();
                numColour = DataHolder.getHardLevel3NumColour();
            }
        }

        generateGame(numLED,numColour);
        countDownDisplay(timeLEDOn);    //count down, and clears LED after specified time


        //100 ms after game generated and LED all blink finish.
        delay += 100;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                for (int i=0; i<8; i++)
//                    for (int j=0 ;j<8;j++)
//                        Log.e(TAG, "generatedPlacement is " + generatedPlacement[i][j] + i + "" + j);
                Log.e(TAG, "you may place the chips now!");
                i.putExtra("boardMatrix", getIntent().getSerializableExtra("boardMatrix"));
                i.putExtra("generatedPlacement", generatedPlacement);
                i.putStringArrayListExtra("lightedLEDStringList", lightedLEDStringList);
                //i.putExtra("Level",level);
                //i.putExtra("GameSelected",gameSelected);
                //progress.dismiss();
                startActivity(i);
            }
        }, delay);    //in millis

    }


    String levelString = "Level ";

    public void beforeStartDisplay() {

        //levelString += String.valueOf(level);// "1" should be a variable called level from intent. current level

        levelString += String.valueOf(DataHolder.getLevel());

        //level X
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(levelString);
                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setTextSize(50);
                        topTextView.setText(levelString);
                    }
                });

                //if (level == 1) {
                if (DataHolder.getLevel() == 1) {
                    soundLevelOne.start();
                    soundLevelOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                }

                //else if (level == 2) {
                else if (DataHolder.getLevel() == 2) {
                    soundLevelTwo.start();
                    soundLevelTwo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                }

                //else if (level == 3) {
                else if (DataHolder.getLevel() == 3) {
                    soundLevelThree.start();
                    soundLevelThree.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                }
            }
        }, delay);    //in millis

        //Please Pay Attention To The Board
        delay +=1500;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Please Pay Attention To The Board");
                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setTextSize(35);
                        topTextView.setText("Please Pay Attention To The Board");
                    }
                });
                soundPayAttention.start();
                soundPayAttention.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();

                    };
                });
            }
        }, delay);    //in millis

        //Get Ready
        delay +=2000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Get ready");

                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setTextSize(50);
                        topTextView.setText("Get ready");
                    }
                });
                soundGetReady.start();
                soundGetReady.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();

                    };
                });
            }
        }, delay);    //in millis
    }

    boolean occupied;
    int row = 0, column = 0;
    char colour;
    char[][] generatedPlacement = new char[8][8];

    public void generateGame(final int numberOfLED, final int numColour) {

        //soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
       // for (int i = 0; i < numberOfLED; i++) {

//            if (i!=0)
//                delay += delayTime;
            delay+=1000;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    String redRow0To3HexString="", redRow4To7HexString="";
                    String greenRow0To3HexString="", greenRow4To7HexString="";
                    String blueRow0To3HexString="", blueRow4To7HexString="";
                    String yellowRow0To3HexString="", yellowRow4To7HexString="";
                    String redRow0To3HexWithoutZerosString="", redRow4To7HexWithoutZerosString="";
                    String greenRow0To3HexWithoutZerosString="", greenRow4To7HexWithoutZerosString="";
                    String blueRow0To3HexWithoutZerosString="", blueRow4To7HexWithoutZerosString="";
                    String yellowRow0To3HexWithoutZerosString="", yellowRow4To7HexWithoutZerosString="";
                    String generatedString="";

                    int bitPositionToChange;
                    long redRow0To3 = 0;
                    long redRow4To7 = 0;
                    long greenRow0To3 = 0;
                    long greenRow4To7 = 0;
                    long blueRow0To3 = 0;
                    long blueRow4To7 = 0;
                    long yellowRow0To3 = 0;
                    long yellowRow4To7 = 0;


                    //fill content of generatedPlacement array with 0's
                    for (int i = 0; i < 8; i++) {
                        Arrays.fill(generatedPlacement[i], '0');
                    }

                    Log.e(TAG, "number of LED: " + numberOfLED);
                    for (int i = 0; i < numberOfLED; i++) {

                        //soundMP3.start();
                        //random generate colour (either r g b or y)
                        Random random = new Random();

                        String colourPool = "rgby";
                        colour = colourPool.charAt(random.nextInt(numColour));
                        //colour = colourPool.charAt(random.nextInt(colourPool.length()));
                        Log.e(TAG, "chosen random colour is: " + colour);
                        occupied = true;

                        while (occupied) {       //re-generate random row and column if position occupied
                            //random generate 2 position number (each number between 0-7)
                            int min = 0;
                            int max = 7;
                            row = random.nextInt(max - min + 1);  //random generate row
                            //row = 7;
                            column = random.nextInt(max - min + 1);   //random generate column
                            //Log.e(TAG, "chosen random row is: " + row);
                            //Log.e(TAG, "chosen random column is: " + column);

                            //update generated position and colour into 2d array (to be sent to next activity), need to 7-row because of mirror
                            if (generatedPlacement[7-row][column] == '0') { //if no colour have been generated at this array position
                                generatedPlacement[7-row][column] = colour;
                                occupied = false;
                            } else {
                                occupied = true;
                            //    Log.e(TAG, "occupied slot, re-generating " + column);
                            }
                        }
//                        //generate string based on colour and position(returns a string)
//                        String generatedString = stringGenerator(colour, row, column);
//                        //stringBuffer.add("0200000000CCFFFFFFFFFFFFFFFF");       //clear all LED
//                        //stringBuffer.add(generatedString);                      //light up based on random generated position and colour
//                        lightedLEDStringList.add(generatedString);
//                        //send();

                        //set specific bits position to be lighted up, which will be converted into hex string to be sent to the bluno board based so as to light up LED based on bluetooth protocol specification.
                        int position = row*8 + column;  //row 0 column 0 is position 0 , row 0 column 1 is position 1, just a representation for the board.
                        if (position <=31){
                            bitPositionToChange = 31 - position;
                            if (colour == 'r') {
                                redRow0To3 += (long) java.lang.Math.pow(2, bitPositionToChange);    //set bit based on position
                                redRow0To3HexWithoutZerosString = (Long.toString(redRow0To3, 16)).toUpperCase();
                                //System.out.println("hex str: " + redRow0To3HexString);
                            }
                            else if (colour == 'g') {
                                greenRow0To3 += (long) java.lang.Math.pow(2, bitPositionToChange);    //set bit based on position
                                greenRow0To3HexWithoutZerosString = (Long.toString(greenRow0To3, 16)).toUpperCase();
                            }
                            else if (colour == 'b') {
                                blueRow0To3 += (long) java.lang.Math.pow(2, bitPositionToChange);    //set bit based on position
                                blueRow0To3HexWithoutZerosString = (Long.toString(blueRow0To3, 16)).toUpperCase();
                            }
                            else if (colour == 'y') {
                                yellowRow0To3 += (long) java.lang.Math.pow(2, bitPositionToChange);    //set bit based on position
                                yellowRow0To3HexWithoutZerosString = (Long.toString(yellowRow0To3, 16)).toUpperCase();
                            }
                        }
                        else{
                            bitPositionToChange = 63 - position;
                            if (colour == 'r') {
                                redRow4To7 += java.lang.Math.pow(2, bitPositionToChange);
                                redRow4To7HexWithoutZerosString = (Long.toString(redRow4To7, 16)).toUpperCase();
                                //System.out.println("hex str: " + redRow4To7HexString);
                            }
                            else if (colour == 'g') {
                                greenRow4To7 += java.lang.Math.pow(2, bitPositionToChange);
                                greenRow4To7HexWithoutZerosString = (Long.toString(greenRow4To7, 16)).toUpperCase();
                            }
                            else if (colour == 'b') {
                                blueRow4To7 += java.lang.Math.pow(2, bitPositionToChange);
                                blueRow4To7HexWithoutZerosString = (Long.toString(blueRow4To7, 16)).toUpperCase();
                            }
                            else if (colour == 'y') {
                                yellowRow4To7 += java.lang.Math.pow(2, bitPositionToChange);
                                yellowRow4To7HexWithoutZerosString = (Long.toString(yellowRow4To7, 16)).toUpperCase();
                            }
                        }


                    }

                    for (int i=0; i<8; i++)
                        for (int j=0 ;j<8;j++)
                            Log.e(TAG, "generatedPlacement is " + generatedPlacement[i][j] + i + "" + j);



                    //generate (8 - string.size) string of 0, to be concatanated with original string
                    for (int i=0; i< (8-redRow0To3HexWithoutZerosString.length()); i++ )
                       redRow0To3HexString += "0";
                    for (int i=0; i< (8-redRow4To7HexWithoutZerosString.length()); i++ )
                        redRow4To7HexString += "0";

                    redRow0To3HexString += redRow0To3HexWithoutZerosString;
                    redRow4To7HexString += redRow4To7HexWithoutZerosString;

                    Log.e(TAG, "red Row0 To Row3 after adding zeroes : " + redRow0To3HexString);
                    Log.e(TAG, "red Row4 To Row7 after adding zeroes : " + redRow4To7HexString);


                    for (int i=0; i< (8-greenRow0To3HexWithoutZerosString.length()); i++ )
                        greenRow0To3HexString += "0";
                    for (int i=0; i< (8-greenRow4To7HexWithoutZerosString.length()); i++ )
                        greenRow4To7HexString += "0";

                    greenRow0To3HexString += greenRow0To3HexWithoutZerosString;
                    greenRow4To7HexString += greenRow4To7HexWithoutZerosString;

                    Log.e(TAG, "green Row0 To Row3 after adding zeroes : " + greenRow0To3HexString);
                    Log.e(TAG, "green Row4 To Row7 after adding zeroes : " + greenRow4To7HexString);

                    for (int i=0; i< (8-blueRow0To3HexWithoutZerosString.length()); i++ )
                        blueRow0To3HexString += "0";
                    for (int i=0; i< (8-blueRow4To7HexWithoutZerosString.length()); i++ )
                        blueRow4To7HexString += "0";

                    blueRow0To3HexString += blueRow0To3HexWithoutZerosString;
                    blueRow4To7HexString += blueRow4To7HexWithoutZerosString;

                    Log.e(TAG, "blue Row0 To Row3 after adding zeroes : " + blueRow0To3HexString);
                    Log.e(TAG, "blue Row4 To Row7 after adding zeroes : " + blueRow4To7HexString);

                    for (int i=0; i< (8-yellowRow0To3HexWithoutZerosString.length()); i++ )
                        yellowRow0To3HexString += "0";
                    for (int i=0; i< (8-yellowRow4To7HexWithoutZerosString.length()); i++ )
                        yellowRow4To7HexString += "0";

                    yellowRow0To3HexString += yellowRow0To3HexWithoutZerosString;
                    yellowRow4To7HexString += yellowRow4To7HexWithoutZerosString;

                    Log.e(TAG, "yellow Row0 To Row3 after adding zeroes : " + yellowRow0To3HexString);
                    Log.e(TAG, "yellow Row4 To Row7 after adding zeroes : " + yellowRow4To7HexString);


                    //generate string for all 4 colours
                    generatedString = generateRowString('r',redRow0To3HexString, redRow4To7HexString);
                    lightedLEDStringList.add(generatedString);
                    generatedString = generateRowString('g',greenRow0To3HexString, greenRow4To7HexString);
                    lightedLEDStringList.add(generatedString);
                    generatedString = generateRowString('b',blueRow0To3HexString, blueRow4To7HexString);
                    lightedLEDStringList.add(generatedString);
                    generatedString = generateRowString('y',yellowRow0To3HexString, yellowRow4To7HexString);
                    lightedLEDStringList.add(generatedString);

                    //send strings via bluetooth to light up the LEDs
                    lightUpGeneratedPlacement();

                    runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                        @Override
                        public void run() {
                            topTextView.setText("");
                        }
                    });

//                    progress = new ProgressDialog(Game1.this);
//                    progress.setMessage("");
//                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//                    progress.show();
                }
            }, delay);    //in millis
        //}


    }


public void countDownDisplay(int timeLEDOn) {
    //3
    delay += timeLEDOn-3000;
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            System.out.println("3");
            runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                @Override
                public void run() {
                    topTextView.setText("3");
                }
            });
            soundThree.start();
            soundThree.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();

                }
            });
        }
    }, delay);    //in millis

    //2
    delay += 1000;
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            System.out.println("2");

            runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                @Override
                public void run() {
                    topTextView.setText("2");
                }
            });
            soundTwo.start();
            soundTwo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();

                }
            });
        }
    }, delay);    //in millis

    //1
    delay += 1000;
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            System.out.println("1");
            runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                @Override
                public void run() {
                    topTextView.setText("1");
                }
            });
            soundOne.start();
            soundOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();

                }
            });
        }
    }, delay);    //in millis

    //after 1, clear LED here
    delay += 1000;
    timer.schedule(new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                @Override
                public void run() {
                    stringBuffer.add("0300000000CCFFFFFFFFFFFFFFFF");
                    send();
                }
            });
        }
    }, delay);    //in millis

}





//    public String stringGenerator(char colour, int row, int column) {    //meant for single LED and show LED straight (uses LED protocol instead of row protocol)
//        //0100FFFFFFCCXYFF -> XY = ROW COLUMN
//
//        String string = "";
//        string += "0100";   //opcode and function
//
//        if (colour == 'r' || colour == 'y') //R
//            string += "FF";
//        else //else is g or b
//            string += "00";
//
//        if (colour == 'g' || colour == 'y') //G
//            string += "FF";
//        else
//            string += "00";
//
//        if (colour == 'b')  //B
//            string += "FF";
//        else
//            string += "00";
//
//        string += "CC";     //'brightness'
//
//        string += String.valueOf(row);
//        string += String.valueOf(column);
//        string += "FF";       //no more further LED to light up
//        return string;
//    }

    public String generateRowString(char colour, String row0To3Hex, String row4To7Hex) {    //meant for single LED and show LED straight (uses LED protocol instead of row protocol)
        //0100FFFFFFCCXYFF -> XY = ROW COLUMN

        String string = "";
        string += "0300";   //opcode and function

        if (colour == 'r' || colour == 'y') //R
            string += "FF";
        else //else is g or b
            string += "00";

        if (colour == 'g' || colour == 'y') //G
            string += "FF";
        else
            string += "00";

        if (colour == 'b')  //B
            string += "FF";
        else
            string += "00";

        string += "CC";     //'brightness'

        string += row0To3Hex;
        string += row4To7Hex;
        return string;
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
            if (stringBuffer.get(0).charAt(1) == '1' || stringBuffer.get(0).charAt(1) == '3') {            //if stringBuffer.length == 1 && opcode == show LED && RGB got colour
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

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

    //    @Override
    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
        // TODO Auto-generated method stub
        send();
    }

}




//
//    public void lightLEDSequence1() {       //add strings and make schedules to light up the LEDs
//        stringBuffer.add("0200FFFFFFCC000000000000000F");
//        stringBuffer.add("0300FFFFFFCC00000000000000F0");
//        //stringBuffer.add("0300000000CC00000000000000FF");
//        //stringBuffer.add("0300000000CC000000000000FF00");
//
//        delay += 2000;
//        soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                soundMP3.start();
//                stringBuffer.add("0200FF0000CC000000000000000F");
//                stringBuffer.add("0300FF0000CC00000000000000F0");
//                //stringBuffer.add("0300000000CC00000000000000FF");
//                //stringBuffer.add("0300000000CC000000000000FF00");
//            }
//        }, delay);    //in millis
//
//
//        delay += 2000;
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                soundMP3.start();
//                stringBuffer.add("020000FF00CC000000000000000F");
//                stringBuffer.add("030000FF00CC00000000000000F0");
//                //stringBuffer.add("0300000000CC00000000000000FF");
//                //stringBuffer.add("0300000000CC000000000000FF00");
//            }
//        }, delay);    //in millis
//
//
//        delay += 100;
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("you may place your chips now!");
//                i.putExtra("boardMatrix", getIntent().getSerializableExtra("boardMatrix"));
//                startActivity(i);
//            }
//        }, delay);    //in millis
//
//    }



//        if (level == 1){
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    System.out.println(levelString);
//                    runOnUiThread(new Runnable() {      //anything that updates view need to use this.
//                        @Override
//                        public void run() {
//                            topTextView.setTextSize(50);
//                            topTextView.setText(levelString);
//                        }
//                    });
//                    soundLevelOne.start();
//                    soundLevelOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        public void onCompletion(MediaPlayer mp) {
//                            mp.release();
//
//                        };
//                    });
//                }
//            }, delay);    //in millis
//        }
//
//        else if (level == 2){
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    System.out.println(levelString);
//                    runOnUiThread(new Runnable() {      //anything that updates view need to use this.
//                        @Override
//                        public void run() {
//                            topTextView.setTextSize(50);
//                            topTextView.setText(levelString);
//                        }
//                    });
//                    soundLevelTwo.start();
//                    soundLevelTwo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        public void onCompletion(MediaPlayer mp) {
//                            mp.release();
//
//                        };
//                    });
//                }
//            }, delay);    //in millis
//        }
//
//        else if (level == 3){
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    System.out.println(levelString);
//                    runOnUiThread(new Runnable() {      //anything that updates view need to use this.
//                        @Override
//                        public void run() {
//                            topTextView.setTextSize(50);
//                            topTextView.setText(levelString);
//                        }
//                    });
//                    soundLevelThree.start();
//                    soundLevelThree.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                        public void onCompletion(MediaPlayer mp) {
//                            mp.release();
//
//                        };
//                    });
//                }
//            }, delay);    //in millis
//        }