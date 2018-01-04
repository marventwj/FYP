package com.dfrobot.angelo.blunobasicdemo;

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
import java.util.concurrent.TimeUnit;

public class Game1 extends BaseActivity {

    private static final String TAG = "OCVSample::Activity";

    int level , gameSelected = 1;
    private Button buttonScan, buttonSerialSend;
    private TextView topTextView, bottomTextView;
    boolean flag = true;
    ArrayList<String> stringBuffer = new ArrayList<String>();
    ArrayList<String> lightedLEDStringList = new ArrayList<String>();
    ScheduledFuture<?> sendHandler;
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    Timer timer = new Timer();
    int delay = 0;
    Intent i;
    MediaPlayer soundMP3, soundPayAttention, soundGetReady, soundThree, soundTwo, soundOne, soundLevelOne, soundLevelTwo, soundLevelThree;
    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        onCreateProcess();                                                        //onCreate Process by BlunoLibrary
//        serialBegin(9600);                                                    //set the Uart Baudrate on BLE chip to 115200

        soundPayAttention = MediaPlayer.create(this, R.raw.please_pay_attention_to_the_board);
        soundGetReady = MediaPlayer.create(this, R.raw.get_ready);
        soundThree = MediaPlayer.create(this, R.raw.three);
        soundTwo = MediaPlayer.create(this, R.raw.two);
        soundOne = MediaPlayer.create(this, R.raw.one);
        soundLevelThree = MediaPlayer.create(this, R.raw.level_three);
        soundLevelTwo = MediaPlayer.create(this, R.raw.level_two);
        soundLevelOne = MediaPlayer.create(this, R.raw.level_one);


        level =  getIntent().getIntExtra("Level",0);
        i = new Intent(this, VerifyResults.class);



        topTextView = (TextView) findViewById(R.id.topTextView);
        //bottomTextView = (TextView) findViewById(R.id.bottomTextView);
//        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);        //initial the button for sending the data
//        buttonScan = (Button) findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device


        countDownDisplay();

//        delay += 1000;
//        sendHandler = executorService.scheduleAtFixedRate(new Runnable() {    //initiate send function after 1s after countdown 3,2,1.
//            @Override
//            public void run() {
//                send();     //send first string
//                soundMP3.start();
//            }
//        }, delay, delay, TimeUnit.MILLISECONDS);
        //lightLEDSequence1();

        if (level == 1)
             generateGame(1000, 4);//easy mode
        if (level == 2)
            generateGame(1000, 6);//medium mode
        if (level == 3)
            generateGame(1000, 8);//hard mode

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
                i.putExtra("Level",level);
                i.putExtra("GameSelected",gameSelected);
                progress.dismiss();
                startActivity(i);
            }
        }, delay);    //in millis


//        buttonSerialSend.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//            }
//        });
//
//
//        buttonScan.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                buttonScanOnClickProcess();                                        //Alert Dialog for selecting the BLE device
//            }
//        });


    }


    String levelString = "Level ";

    public void countDownDisplay() {
        levelString += String.valueOf(level);// "1" should be a variable called level from intent. current level
        if (level == 1){
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
                    soundLevelOne.start();
                    soundLevelOne.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        };
                    });
                }
            }, delay);    //in millis
        }

        else if (level == 2){
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
                    soundLevelTwo.start();
                    soundLevelTwo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        };
                    });
                }
            }, delay);    //in millis
        }

        else if (level == 3){
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
                    soundLevelThree.start();
                    soundLevelThree.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();

                        };
                    });
                }
            }, delay);    //in millis
        }



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


        delay +=2000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Get ready.");

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


        delay += 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //System.out.println("Pattern Will Be Shown On The Board In");
                System.out.println("3");

                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        //topTextView.setText("Pattern Will Be Shown On The Board In");
                        topTextView.setText("3");
                    }
                });
                soundThree.start();
                soundThree.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();

                    };
                });
            }
        }, delay);    //in millis

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

                    };
                });
            }
        }, delay);    //in millis


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

                    };
                });
            }
        }, delay);    //in millis

        delay += 1000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setText("");
                        progress = new ProgressDialog(Game1.this);
                        progress.setMessage("");
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();
                    }
                });
            }
        }, delay);    //in millis
    }

    boolean occupied;
    int row = 0, column = 0;
    char colour;
    char[][] generatedPlacement = new char[8][8];

    public void generateGame(int delayTime, int numberOfLED) {

        //soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
        //fill content of generatedPlacement array with 0's
        for (int i = 0; i < 8; i++) {
            Arrays.fill(generatedPlacement[i], '0');
        }

        for (int i = 0; i < numberOfLED; i++) {
            if (i!=0)
                delay += delayTime;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //soundMP3.start();
                    //random generate colour (either r g b or y)
                    Random random = new Random();
                    String colourPool = "rgby";
                    colour = colourPool.charAt(random.nextInt(colourPool.length()));
                    Log.e(TAG, "chosen random colour is: " + colour);
                    occupied = true;

                    while (occupied) {       //re-generate random row and column if position occupied
                        //random generate 2 position number (each number between 0-7)
                        int min = 0;
                        int max = 7;
                        //row = random.nextInt(max - min) + min;
                        row =7;
                        column = random.nextInt(max - min) + min;
                        Log.e(TAG, "chosen random row is: " + row);
                        Log.e(TAG, "chosen random column is: " + column);

                        //update generated position and colour into 2d array (to be sent to next activity)
                        if (generatedPlacement[row][column] == '0') { //if no colour have been generated at this array position
                            generatedPlacement[row][column] = colour;
                            occupied = false;
                        }
                        else {
                            occupied = true;
                            Log.e(TAG, "occupied slot, re-generating " + column);
                        }
                    }
                    //generate string based on colour and position(returns a string)
                    String generatedString = stringGenerator(colour, row, column);
                    stringBuffer.add("0200000000CCFFFFFFFFFFFFFFFF");       //clear all LED
                    stringBuffer.add(generatedString);                      //light up based on random generated position and colour
                    lightedLEDStringList.add(generatedString);
                    send();
                }
            }, delay);    //in millis
        }

        //clear the last LED
        delay += delayTime;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stringBuffer.add("0300000000CCFFFFFFFFFFFFFFFF");       //clear all LED
                send();
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


    public void send() {        //this function invoked when ack received
        setConnectionStateConnected();
        if (!stringBuffer.isEmpty()) {    //ensures no message sent if no more string.
//            if (!sendHandler.isCancelled()) {
//                sendHandler.cancel(true);
//            }
            Log.e(TAG, "SENT");
            Log.e(TAG, "charAt(1) = " + stringBuffer.get(0).charAt(1) );
            if (stringBuffer.get(0).charAt(1) == '1' ) {            //if stringBuffer.length == 1 && opcode == show LED && RGB got colour
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
//        else {
//            //System.out.println("NO MORE STRING LIAO!");
//            if (sendHandler.isCancelled()) {
//                System.out.println("SWITCHING ON executorService");
//                sendHandler = executorService.scheduleAtFixedRate(new Runnable() {
//                    @Override
//                    public void run() {
//                        send();
//                    }
//                }, 100, 100, TimeUnit.MILLISECONDS);
//            }
//        }
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