package com.marven.fyp.memorytraining;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Game1and3 extends BaseActivity {

    private static final String TAG = "OCVSample::Activity";
    private TextView topTextView;
    ArrayList<String> stringBuffer = new ArrayList<String>();
    ArrayList<String> lightedLEDStringList = new ArrayList<String>();
    Timer timer = new Timer();
    int delay = 0;
    Intent i;
    MediaPlayer soundMP3, soundPayAttention, soundGetReady, soundThree, soundTwo, soundOne, soundLevelOne, soundLevelTwo, soundLevelThree;
    ProgressDialog progress;
    String levelString = "Level ";

    //declarations for game 3
    long startTime;
    boolean allowPressStartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1and3);

        soundPayAttention = MediaPlayer.create(this, R.raw.please_pay_attention_to_the_board);
        soundGetReady = MediaPlayer.create(this, R.raw.get_ready);
        soundThree = MediaPlayer.create(this, R.raw.three);
        soundTwo = MediaPlayer.create(this, R.raw.two);
        soundOne = MediaPlayer.create(this, R.raw.one);
        soundLevelThree = MediaPlayer.create(this, R.raw.level_three);
        soundLevelTwo = MediaPlayer.create(this, R.raw.level_two);
        soundLevelOne = MediaPlayer.create(this, R.raw.level_one);
        topTextView = (TextView) findViewById(R.id.topTextView);
        i = new Intent(this, VerifyResults.class);

        //necessary displays before start of game
        beforeStartDisplay();


        //-------------------------------configure difficulty for game-----------------------------------------------------------------------
        int timeLEDOn = 0, numLED = 0, numColour = 0, numSideBySideLED = 0;
        //easy mode - 1 colour
        if (DataHolder.getMode() == DataHolder.Mode.EASY) {
            numColour = 1;
        }

        //medium mode - 2 colour
        else if (DataHolder.getMode() == DataHolder.Mode.MEDIUM) {
            numColour = 2;
        }

        //hard mode - 3 colour
        else if (DataHolder.getMode() == DataHolder.Mode.HARD) {
            numColour = 3;
        }

        // level 1 - board size 3, level 3 - board size 4, level 9 - board size 5, level 17 - board size 6, level 27 - board size 7, level 39 - board size 8
        //based on current level, generate the game, delay time is how long it will show on the board
        if (DataHolder.getLevel() == 1) {
            numLED = numColour*3;
            DataHolder.setBoardSize(3);
        } else if (DataHolder.getLevel() == 2) {
            if (numColour == 3)
                numLED = numColour*4 - 3;
            else
                numLED = numColour*4;
            DataHolder.setBoardSize(3);
        } else if (DataHolder.getLevel() == 3) {
            numLED = numColour*3;
            DataHolder.setBoardSize(4);
        } else if (DataHolder.getLevel() == 4) {
            numLED = numColour*3;
            DataHolder.setBoardSize(4);
        } else if (DataHolder.getLevel() == 5) {
            numLED = numColour*4;
            DataHolder.setBoardSize(4);
        } else if (DataHolder.getLevel() == 6) {
            numLED = numColour*4;
            DataHolder.setBoardSize(4);
        } else if (DataHolder.getLevel() == 7) {
            numLED = numColour*5;
            DataHolder.setBoardSize(4);
        } else if (DataHolder.getLevel() == 8) {
            numLED = numColour*5;
            DataHolder.setBoardSize(4);
        } else if (DataHolder.getLevel() == 9) {
            numLED = numColour*3;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 10) {
            numLED = numColour*3;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 11) {
            numLED = numColour*4;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 12) {
            numLED = numColour*4;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 13) {
            numLED = numColour*5;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 14) {
            numLED = numColour*5;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 15) {
            numLED = numColour*6;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 16) {
            numLED = numColour*6;
            DataHolder.setBoardSize(5);
        } else if (DataHolder.getLevel() == 17) {
            numLED = numColour*3;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 18) {
            numLED = numColour*3;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 19) {
            numLED = numColour*4;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 20) {
            numLED = numColour*4;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 21) {
            numLED = numColour*5;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 22) {
            numLED = numColour*5;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 23) {
            numLED = numColour*6;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 24) {
            numLED = numColour*6;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 25) {
            numLED = numColour*7;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 26) {
            numLED = numColour*7;
            DataHolder.setBoardSize(6);
        } else if (DataHolder.getLevel() == 27) {
            numLED = numColour*3;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 28) {
            numLED = numColour*3;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 29) {
            numLED = numColour*4;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 30) {
            numLED = numColour*4;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 31) {
            numLED = numColour*5;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 32) {
            numLED = numColour*5;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 33) {
            numLED = numColour*6;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 34) {
            numLED = numColour*6;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 35) {
            numLED = numColour*7;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 36) {
            numLED = numColour*7;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 37) {
            numLED = numColour*8;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 38) {
            numLED = numColour*8;
            DataHolder.setBoardSize(7);
        } else if (DataHolder.getLevel() == 39) {
            numLED = numColour*3;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 40) {
            numLED = numColour*3;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 41) {
            numLED = numColour*4;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 42) {
            numLED = numColour*4;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 43) {
            numLED = numColour*5;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 44) {
            numLED = numColour*5;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 45) {
            numLED = numColour*6;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 46) {
            numLED = numColour*6;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 47) {
            numLED = numColour*7;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 48) {
            numLED = numColour*7;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 49) {
            numLED = numColour*8;
            DataHolder.setBoardSize(8);
        } else if (DataHolder.getLevel() == 50) {
            numLED = numColour*8;
            DataHolder.setBoardSize(8);
        }

        timeLEDOn = numLED/numColour * 1000 + 1000;   //e.g, if numLED = 4, give 5 seconds
        if (DataHolder.getLevel() % 2 == 0)
            numSideBySideLED = 0;
        else
            numSideBySideLED = 2;

        //-------------------------------end of configure difficulty for game-----------------------------------------------------------------------



        generateGame(numLED, numColour, numSideBySideLED);

        // TODO Auto-generated method stub - For game 3 no need countdowndisplay
        if (DataHolder.getGameSelected() == 1) {
            countDownDisplay(timeLEDOn);    //count down, and clears LED after specified time
        }

    }

    public void beforeStartDisplay() {
        levelString += String.valueOf(DataHolder.getLevel());

        //-----------------------------------------level X--------------------------------------------------
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setTextSize(50);
                        topTextView.setText(levelString);
                    }
                });

                if (DataHolder.getNaturalReaderSoundStatus()) {
                    if (DataHolder.getLevel() == 1) {
                        soundLevelOne.start();
                        soundLevelOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    } else if (DataHolder.getLevel() == 2) {
                        soundLevelTwo.start();
                        soundLevelTwo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    } else if (DataHolder.getLevel() == 3) {
                        soundLevelThree.start();
                        soundLevelThree.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                }


                //show board play area
                lightUpBoardPlayArea(DataHolder.getBoardSize());


            }
        }, delay);    //in millis

        //-----------------------------------------Please Pay Attention To The Board--------------------------------------------------
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
                if (DataHolder.getNaturalReaderSoundStatus()) {
                    soundPayAttention.start();
                    soundPayAttention.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        }

                        ;
                    });
                }

            }
        }, delay);    //in millis

        //----------------------------------------------------Get Ready-------------------------------------------------------
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
                if (DataHolder.getNaturalReaderSoundStatus()) {
                    soundGetReady.start();
                    soundGetReady.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        }

                        ;
                    });
                }
            }
        }, delay);    //in millis
    }



    public void lightUpBoardPlayArea(int boardSize){

        if (boardSize == 3)     //if level = bottom right of board 3x3
            stringBuffer.add(generateRowString('w', "0000000000070707")); //show LED since is last, no need to append

        else if (boardSize == 4)     //if level = bottom right of board 4x4
            stringBuffer.add(generateRowString('w', "000000000F0F0F0F"));    //bottom right 4x4 board

        else if (boardSize == 5)     //if level = bottom right of board 5x5
            stringBuffer.add(generateRowString('w', "0000001F1F1F1F1F"));    //bottom right 5x5 board

        else if (boardSize == 6)     //if level = bottom right of board 6x6
            stringBuffer.add(generateRowString('w', "00003F3F3F3F3F3F"));    //bottom right 6x6 board

        else if (boardSize == 7)    //if level = bottom right of board 7x7
            stringBuffer.add(generateRowString('w', "007F7F7F7F7F7F7F"));    //bottom right 7x7 board

        else if (boardSize == 8)     //if level = bottom right of board 8x8
            stringBuffer.add(generateRowString('w', "FFFFFFFFFFFFFFFF"));    //bottom right 8x8 board

        send();
    }



    boolean occupied;
    int row = 0, column = 0, prevRow, prevColumn;
    char colour;
    char[][] generatedPlacement = new char[8][8];

    public void generateGame(final int numberOfLED, final int numColour, final int numSideBySideLED) {
        delay += 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {


                String redRow0To7HexString = "";
                String greenRow0To7HexString = "";
                String blueRow0To7HexString = "";
                String yellowRow0To7HexString = "";
                String generatedString = "";

                BigInteger redRow0To7 = BigInteger.valueOf(0);
                BigInteger greenRow0To7 = BigInteger.valueOf(0);
                BigInteger blueRow0To7 = BigInteger.valueOf(0);
                BigInteger yellowRow0To7 = BigInteger.valueOf(0);

                //fill content of generatedPlacement array with 0's
                for (int i = 0; i < 8; i++) {
                    Arrays.fill(generatedPlacement[i], '0');
                }

                Log.e(TAG, "number of LED: " + numberOfLED);

                int numRedLED=0, numGreenLED=0, numBlueLED=0, numYellowLED=0;
                //number of LED must not exceed the board size
                for (int i = 0; i < numberOfLED; i++) {

                    //soundMP3.start();
                    //random generate colour (either r g b or y)
                    Random random = new Random();
                    String colourPool = "rgby";
                    colourPool = colourPool.substring(0, numColour);      //if 1 colour, will only have "r" in the colour pool string, if 2, will have "rg", etc.

                    Log.e(TAG, "NOW COLOUR POOL BECOMES " + colourPool);

                    //this is to generate equal number of LEDs
                    if (numRedLED >= (numberOfLED / numColour))
                        colourPool = colourPool.replace("r","");
                    if (numGreenLED >= (numberOfLED / numColour))
                        colourPool = colourPool.replace("g","");
                    if (numBlueLED >= (numberOfLED / numColour))
                        colourPool = colourPool.replace("b","");
                    if (numYellowLED >= (numberOfLED / numColour))
                        colourPool = colourPool.replace("y","");

                    //colour = colourPool.charAt(random.nextInt(numColour));
                    colour = colourPool.charAt(random.nextInt(colourPool.length()));
                    Log.e(TAG, "chosen random colour is: " + colour);
                    occupied = true;

                    while (occupied) {       //re-generate random row and column if position occupied
                        //random generate 2 position number (each number between 0-7)
                        int min = 8 - DataHolder.getBoardSize();    //generate LEDs more towards the bottom side of the board based on boardSize.
                        int max = 8;
                        row = random.nextInt(max - min) + min;  //random generate row
                        column = random.nextInt(max - min) + min;   //random generate column
                        //Log.e(TAG, "chosen random row is: " + row);
                        //Log.e(TAG, "chosen random column is: " + column);


                        //to generate LED that are side by side
                        if (i < numSideBySideLED) {
                            if (i == 0) {
                                while ((column + numSideBySideLED - 1) >= 8) {
                                    column = random.nextInt(max - min) + min;   //re-generate the random column
                                }
                            } else {
                                row = prevRow;
                                column = prevColumn + 1;
                            }
                        }
                        prevRow = row;
                        prevColumn = column;

                        //update generated position and colour into 2d array (to be sent to next activity), need to 7-row because of mirror
                        if (generatedPlacement[7 - row][column] == '0') { //if no colour have been generated at this array position
                            generatedPlacement[7 - row][column] = colour;
                            occupied = false;
                        } else {
                            occupied = true;
                            //    Log.e(TAG, "occupied slot, re-generating " + column);
                        }
                    }

                    int position = row * 8 + column;  //row 0 column 0 is position 0 , row 0 column 1 is position 1, just a representation for the board.
                    int bitPositionToChange = 63-position;
                    BigInteger two = BigInteger.valueOf(2);
                    BigInteger temp = two.pow(bitPositionToChange);

//set specific bits position to be lighted up, which will be converted into hex string to be sent to the bluno board so as to light up LED based on bluetooth protocol specification.
                    if (colour == 'r') {
                        redRow0To7 = redRow0To7.add(temp);
                        numRedLED++;
                    }
                    else if (colour == 'g') {
                        greenRow0To7 = greenRow0To7.add(temp);
                        numGreenLED++;
                    }
                    else if (colour == 'b') {
                        blueRow0To7 = blueRow0To7.add(temp);
                        numBlueLED++;
                    }
                    else if (colour == 'y') {
                        yellowRow0To7 = yellowRow0To7.add(temp);
                        numYellowLED++;
                    }

                }

                redRow0To7HexString = String.format("%016X" , redRow0To7);
                greenRow0To7HexString = String.format("%016X" , greenRow0To7);
                blueRow0To7HexString = String.format("%016X" , blueRow0To7);
                yellowRow0To7HexString = String.format("%016X" , yellowRow0To7);

                Log.e(TAG, "red string: " + redRow0To7HexString);
                Log.e(TAG, "green string: " + greenRow0To7HexString);
                Log.e(TAG, "blue string: " + blueRow0To7HexString);
                Log.e(TAG, "yellow string: " + yellowRow0To7HexString);

                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++)
                        Log.e(TAG, "generatedPlacement is " + generatedPlacement[i][j] + i + "" + j);



                //generate string for all 4 colours
                generatedString = generateRowString('r', redRow0To7HexString);
                lightedLEDStringList.add(generatedString);
                generatedString = generateRowString('g', greenRow0To7HexString);
                lightedLEDStringList.add(generatedString);
                generatedString = generateRowString('b', blueRow0To7HexString);
                lightedLEDStringList.add(generatedString);
                generatedString = generateRowString('y', yellowRow0To7HexString);
                lightedLEDStringList.add(generatedString);

                //send strings via bluetooth to light up the LEDs
                lightUpGeneratedPlacement();

                // TODO Auto-generated method stub - For game 3 only
                if (DataHolder.getGameSelected() == 3) {
                    startTime = SystemClock.elapsedRealtime();  //start timer
                }
                allowPressStartButton = true;


                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setText("");
                    }
                });

//                    progress = new ProgressDialog(Game1and3.this);
//                    progress.setMessage("");
//                    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//                    progress.show();
            }
        }, delay);    //in millis
        //}
    }


    public void countDownDisplay(int timeLEDOn) {
        //----------------------------------------------3-----------------------------------------------------
        delay += timeLEDOn - 3000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setText("3");
                    }
                });
                if (DataHolder.getNaturalReaderSoundStatus()) {
                    soundThree.start();
                    soundThree.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        }
                    });
                }
            }
        }, delay);    //in millis

        //----------------------------------------------2-----------------------------------------------------
        delay += 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                    runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                        @Override
                        public void run() {
                            topTextView.setText("2");
                        }
                    });
                if (DataHolder.getNaturalReaderSoundStatus()) {
                    soundTwo.start();
                    soundTwo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        }
                    });
                }
            }
        }, delay);    //in millis

        //----------------------------------------------1-----------------------------------------------------
        delay += 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setText("1");
                    }
                });
                if (DataHolder.getNaturalReaderSoundStatus()) {
                    soundOne.start();
                    soundOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        }
                    });
                }
            }
        }, delay);    //in millis

        ///------------------------------------------after 3 2 1-----------------------------------------------------
        delay += 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {

                        //show board play area
                        lightUpBoardPlayArea(DataHolder.getBoardSize());

                        //proceed to next activity
                        i.putExtra("boardMatrix", getIntent().getSerializableExtra("boardMatrix"));
                        i.putExtra("generatedPlacement", generatedPlacement);
                        i.putStringArrayListExtra("lightedLEDStringList", lightedLEDStringList);
                        //progress.dismiss();
                        startActivity(i);
                    }
                });
            }
        }, delay);    //in millis
    }

    public String generateRowString(char colour, String row0To7Hex) {    //meant for row and show LED straight (uses row protocol instead of LED protocol)
        //0100FFFFFFCCXYFF -> XY = ROW COLUMN

        String string = "";
        string += "0300";   //opcode and function

        if (colour == 'r') {
            string += "FF0000";
        } else if (colour == 'g') {
            string += "00FF00";
        } else if (colour == 'b') {
            string += "0000FF";
        } else if (colour == 'y') {
            string += "FFFF00";
        } else if (colour == 'p') {
            string += "800080";
        } else if (colour == 'w') { //dim white
            string += "111111";
        }

        string += "CC";     //'brightness'

        string += row0To7Hex;
        return string;
    }


    private void lightUpGeneratedPlacement() {

        for (int i = 0; i < lightedLEDStringList.size(); i++) {
            if (i == (lightedLEDStringList.size() - 1)) { //last in lightedLEDStringlist
                stringBuffer.add(lightedLEDStringList.get(i)); //show LED since is last, no need to append
                send();
            } else {
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
            //Log.e(TAG, "SENT FROM GAME 1");
            //Log.e(TAG, "charAt(1) = " + stringBuffer.get(0).charAt(1) );
            if (stringBuffer.get(0).charAt(1) == '1' || stringBuffer.get(0).charAt(1) == '3') {            //if stringBuffer.length == 1 && opcode == show LED && RGB got colour
                //Log.e(TAG, "enter charAt(1) = 1");
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
    }

    @Override
    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
        if (theString.equals("BUTTON PRESSED")) {

            // TODO Auto-generated method stub - For game 3 only
            if (allowPressStartButton && DataHolder.getGameSelected() == 3) {
                //      Log.e(TAG, "button is pressed!!!!!!!!!");
                long endTime = SystemClock.elapsedRealtime();   //stop the timer
                long elapsedTimeUntilPressStart = (endTime - startTime);
                //turnOffLED();
                lightUpBoardPlayArea(DataHolder.getBoardSize());

                allowPressStartButton = false;
                i.putExtra("elapsedTimeUntilPressStart", elapsedTimeUntilPressStart);
                i.putExtra("boardMatrix", getIntent().getSerializableExtra("boardMatrix"));
                i.putExtra("generatedPlacement", generatedPlacement);
                i.putStringArrayListExtra("lightedLEDStringList", lightedLEDStringList);
                startActivity(i);
            }
        }
        else {
            send();
        }

    }

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