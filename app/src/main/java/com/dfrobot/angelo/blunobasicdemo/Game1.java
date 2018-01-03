package com.dfrobot.angelo.blunobasicdemo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Game1 extends BaseActivity {

    private static final String TAG = "OCVSample::Activity";

    private Button buttonScan, buttonSerialSend;
    private TextView topTextView, bottomTextView;
    boolean flag = true;
    ArrayList<String> stringBuffer = new ArrayList<String>();
    ScheduledFuture<?> sendHandler;
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    Timer timer = new Timer();
    int delay=0;
    Intent i;
    MediaPlayer soundMP3, soundGetReady, soundThree, soundTwo, soundOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        onCreateProcess();                                                        //onCreate Process by BlunoLibrary
//        serialBegin(9600);                                                    //set the Uart Baudrate on BLE chip to 115200

        soundGetReady = MediaPlayer.create(this, R.raw.get_ready);
        soundThree = MediaPlayer.create(this, R.raw.three);
        soundTwo = MediaPlayer.create(this, R.raw.two);
        soundOne = MediaPlayer.create(this, R.raw.one);


        i = new Intent(this, VerifyResults.class);


        topTextView = (TextView) findViewById(R.id.topTextView);
        //bottomTextView = (TextView) findViewById(R.id.bottomTextView);
//        buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);        //initial the button for sending the data
//        buttonScan = (Button) findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device


        countDownDisplay();

        delay += 1000;
        sendHandler = executorService.scheduleAtFixedRate(new Runnable() {    //initiate send function after 1s after countdown 3,2,1.
            @Override
            public void run() {
                send();     //send first string
                soundMP3.start();
            }
        }, delay, delay, TimeUnit.MILLISECONDS);

        lightLEDSequence1();

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


    public void countDownDisplay() {

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Get ready.");

                runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                    @Override
                    public void run() {
                        topTextView.setText("Get ready");
                    }
                });
                soundGetReady.start();
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
            }
        }, delay);    //in millis
    }


    public void lightLEDSequence1() {       //add strings and make schedules to light up the LEDs
        stringBuffer.add("0200FFFFFFCC000000000000000F");
        stringBuffer.add("0300FFFFFFCC00000000000000F0");
        //stringBuffer.add("0300000000CC00000000000000FF");
        //stringBuffer.add("0300000000CC000000000000FF00");

        delay += 2000;
        soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                soundMP3.start();
                stringBuffer.add("0200FF0000CC000000000000000F");
                stringBuffer.add("0300FF0000CC00000000000000F0");
                //stringBuffer.add("0300000000CC00000000000000FF");
                //stringBuffer.add("0300000000CC000000000000FF00");
            }
        }, delay);    //in millis


        delay += 2000;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                soundMP3.start();
                stringBuffer.add("020000FF00CC000000000000000F");
                stringBuffer.add("030000FF00CC00000000000000F0");
                //stringBuffer.add("0300000000CC00000000000000FF");
                //stringBuffer.add("0300000000CC000000000000FF00");
            }
        }, delay);    //in millis


        delay += 100;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("you may place your chips now!");
                i.putExtra("boardMatrix", getIntent().getSerializableExtra("boardMatrix"));
                startActivity(i);
            }
        }, delay);    //in millis

    }

    public void send() {        //this function invoked when ack received
        setConnectionStateConnected();
        if (!stringBuffer.isEmpty()) {    //ensures no message sent if no more string.
            if (!sendHandler.isCancelled()) {
                sendHandler.cancel(true);
            }

//            if (stringBuffer.get(0).charAt(1) == '3' ) {            //if stringBuffer.length == 1 && opcode == show LED
//                soundMP3 = MediaPlayer.create(this, R.raw.filling_your_inbox);
//                soundMP3.start();                                     //play sound
//            }

            serialSend(stringBuffer.remove(0));


        } else {
            //System.out.println("NO MORE STRING LIAO!");
            if (sendHandler.isCancelled()) {
                System.out.println("SWITCHING ON executorService");
                sendHandler = executorService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        send();
                    }
                }, 100, 100, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
    }

//    @Override
    public void onSerialReceived(String theString) {                            //Once connection data received, this function will be called
//        // TODO Auto-generated method stub
//        System.out.println("####################### STRING ACKNOWLEDGEMENT SENT : " + theString);
            send();
//
//
////		serialReceivedText.append(theString);							//append the text into the EditText
//        //The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
//        //	((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
    }

}