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

public class Game2 extends BaseActivity {

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
        setContentView(R.layout.activity_game2);

        soundPayAttention = MediaPlayer.create(this, R.raw.please_pay_attention_to_the_board);
        soundGetReady = MediaPlayer.create(this, R.raw.get_ready);
//        soundThree = MediaPlayer.create(this, R.raw.three);
//        soundTwo = MediaPlayer.create(this, R.raw.two);
//        soundOne = MediaPlayer.create(this, R.raw.one);
        soundLevelThree = MediaPlayer.create(this, R.raw.level_three);
        soundLevelTwo = MediaPlayer.create(this, R.raw.level_two);
        soundLevelOne = MediaPlayer.create(this, R.raw.level_one);
        topTextView = (TextView) findViewById(R.id.topTextView);

        //collect intent
        //level =  getIntent().getIntExtra("Level",0);


        i = new Intent(this, VerifyResults.class);

        //neccessary displays before start of game
        beforeStartDisplay();

        int timeLEDOn = 0, numLED = 0, numColour = 0;

        //easy mode
        if (DataHolder.getMode() == DataHolder.Mode.EASY) {
            //based on current level, generate the game, delay time is how long it will show on the board
            //if (level == 1) {
            if (DataHolder.getLevel() == 1) {
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
            if (DataHolder.getLevel() == 1) {
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
            if (DataHolder.getLevel() == 1) {
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

        generateGame(numLED, numColour);

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
                i.putIntegerArrayListExtra("generatedSequence", generatedSequence);
                i.putStringArrayListExtra("lightedLEDStringList", lightedLEDStringList);
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
        delay += 1500;
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

                    }

                    ;
                });
            }
        }, delay);    //in millis

        //Get Ready
        delay += 2000;
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

                    }

                    ;
                });
            }
        }, delay);    //in millis
    }

    boolean occupied;
    int row = 0, column = 0;
    char colour;

    char[][] generatedPlacement = new char[8][8];
    ArrayList<Integer> generatedSequence= new ArrayList<Integer>();

    public void generateGame(final int numberOfLED, final int numColour) {


        //fill content of generatedPlacement array with 0's
        for (int i = 0; i < 8; i++) {
            Arrays.fill(generatedPlacement[i], '0');
        }

        //soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
        for (int i = 0; i < numberOfLED; i++) {
            //if (i != 0)
            delay += 1000;      //how fast the LED shows per interval
            //delay += 1000;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //make no text after 3 , 2 , 1
                    runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                        @Override
                        public void run() {
                            topTextView.setText("");
                        }
                    });
                    Log.e(TAG, "number of LED: " + numberOfLED);
                    //for (int i = 0; i < numberOfLED; i++) {
//                    soundMP3.start();
                    //random generate colour (either r g b or y)
                    Random random = new Random();
                    colour = 'b';
                    occupied = true;

                    while (occupied) {       //re-generate random row and column if position occupied
                        //random generate 2 position number (each number between 0-7)
                        int min = 0;
                        int max = 5;
                        row = random.nextInt(max - min + 1);  //random generate row
                        //row = 7;
                        column = random.nextInt(max - min + 1);   //random generate column
                        //Log.e(TAG, "chosen random row is: " + row);
                        //Log.e(TAG, "chosen random column is: " + column);

                        //update generated position and colour into 2d array (to be sent to next activity), need to 7-row because of mirror
                        if (generatedPlacement[7 - row][column] == '0') { //if no colour have been generated at this array position
                            generatedPlacement[7 - row][column] = colour;
                            occupied = false;
                        } else {
                            occupied = true;
                            //    Log.e(TAG, "occupied slot, re-generating " + column);
                        }
                    }



                    int position = Integer.valueOf(String.valueOf(7-row) + String.valueOf(column));   //combine row and column into an integer number
                    generatedSequence.add(position);
                    //generate string based on colour and position(returns a string)
                    String generatedString = stringGenerator(colour, row, column);
                    stringBuffer.add("0200000000CCFFFFFFFFFFFFFFFF");       //clear all LED
                    stringBuffer.add(generatedString);                      //light up based on random generated position and colour
                    //lightedLEDStringList.add(generatedString);
                    send();
                    //}
                }
            }, delay);    //in millis
        }



        //clear last LED
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


    public String stringGenerator(char colour, int row, int column) {    //meant for single LED and show LED straight (uses LED protocol instead of row protocol)
        //0100FFFFFFCCXYFF -> XY = ROW COLUMN

        String string = "";
        string += "0100";   //opcode and function

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

        string += String.valueOf(row);
        string += String.valueOf(column);
        string += "FF";       //no more further LED to light up
        return string;
    }

    public void send() {        //this function invoked when ack received
        setConnectionStateConnected();
        if (!stringBuffer.isEmpty()) {    //ensures no message sent if no more string.
            Log.e(TAG, "SENT");
            Log.e(TAG, "charAt(1) = " + stringBuffer.get(0).charAt(1));
            if (stringBuffer.get(0).charAt(1) == '1' || stringBuffer.get(0).charAt(1) == '3') {            //if stringBuffer.length == 1 && opcode == show LED && RGB got colour
                Log.e(TAG, "enter charAt(1) = 1");
                soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
                soundMP3.start();                                     //play sound
                soundMP3.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();

                    }

                    ;
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
