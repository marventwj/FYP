package com.marven.fyp.memorytraining;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class SelectGame extends BaseActivity {

    CardView gameCard1 ,gameCard2, gameCard3, gameCard4;
    private TextView game1NameText, game2NameText, game3NameText, game4NameText;
    private TextView game1DescText, game2DescText, game3DescText, game4DescText;
    Intent i ;
    ProgressDialog progress;
    Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);



      //  timer.schedule(new TimerTask() {
        //    @Override
        //    public void run() {
                //connect to bluetooth automatically
                if (DataHolder.getBluetoothConnected()) {
                    Log.e("hello", "bluetooth already connected!!");
                }
                else {
                    buttonScanOnClickProcess();                                        //Connect to board process

                    //runOnUiThread(new Runnable() {      //anything that updates view need to use this.
                        //@Override
                        //public void run() {
                            progress = new ProgressDialog(SelectGame.this);
                            progress.setMessage("Connecting To The Board...");
                            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                            progress.show();


                        //}
                    //});

                }
       //     }
     //   }, 100);    //in millis



        i = new Intent (this, HowToPlay.class);

        game1NameText = (TextView) findViewById(R.id.game1Name);
        game2NameText = (TextView) findViewById(R.id.game2Name);
        game3NameText = (TextView) findViewById(R.id.game3Name);
        game4NameText = (TextView) findViewById(R.id.game4Name);

        game1DescText = (TextView) findViewById(R.id.game1Desc);
        game2DescText = (TextView) findViewById(R.id.game2Desc);
        game3DescText = (TextView) findViewById(R.id.game3Desc);
        game4DescText = (TextView) findViewById(R.id.game4Desc);

        gameCard1 = (CardView) findViewById(R.id.Game1Card);
        gameCard2 = (CardView) findViewById(R.id.Game2Card);
        gameCard3 = (CardView) findViewById(R.id.Game3Card);
        gameCard4 = (CardView) findViewById(R.id.Game4Card);

        game1NameText.setText("Memorize The Colours");
        game2NameText.setText("CopyCat Simon Says");
        game3NameText.setText("Memory Time Challenge");
        game4NameText.setText("Game 4 Name");

        game1DescText.setText("Test Your Memory With Beautiful Colours!");
        game2DescText.setText("Think You Have A Good Memory? Try CopyCat!");
        game3DescText.setText("Challenge Your Memory Against Time!");
        game4DescText.setText("Game 4 Desc");

        gameCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //i.putExtra("GameSelected",1);
                DataHolder.setGameSelected(1);
                startActivity(i);
            }
        });

        gameCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //i.putExtra("GameSelected",2);
                DataHolder.setGameSelected(2);
                startActivity(i);
            }
        });

        gameCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //i.putExtra("GameSelected",3);
                DataHolder.setGameSelected(3);
                startActivity(i);
            }
        });

        gameCard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //i.putExtra("GameSelected",4);
                DataHolder.setGameSelected(4);
                startActivity(i);
            }
        });
    }


    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                progress.dismiss();
                Toast.makeText(this, "Connection Successful!",
                        Toast.LENGTH_LONG).show();
                DataHolder.setBluetoothConnected(true);
                break;
            case isToScan:
                Log.e("hello", "Connection CHANGED TO IS-TO-SCAN");
                buttonScanOnClickProcess();                                        //Connect to board process
                progress = new ProgressDialog(SelectGame.this);
                progress.setMessage("Connecting To The Board...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();
                break;
//            case isConnecting:
//                buttonConnectBluetooth.setText("Connecting");
//                break;
//            case isScanning:
//                buttonConnectBluetooth.setText("Scanning");
//                break;
//            case isDisconnecting:
//                buttonConnectBluetooth.setText("isDisconnecting");
//                break;
            default:
                break;
        }
    }

}
