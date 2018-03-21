package com.marven.fyp.memorytraining;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Score extends BaseActivity {

    boolean win;
    Intent selectGameIntent , changeDifficultyIntent, continueIntent;

    //private Button buttonContinue;
    private Button buttonSelectOtherGame;
    private Button buttonChangeDifficulty;
    private TextView topTextView,currentTotalScoreTextView, stageScoreTextView, pressButtonToContinueTextView;
    private boolean allowPressStartButton = false;
    private int stageScore, stageTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        continueIntent = new Intent (this, CameraCheckChipsRemoved.class);
        changeDifficultyIntent = new Intent(this, SelectDifficulty.class);
        selectGameIntent = new Intent(this, SelectGame.class);


        win = getIntent().getBooleanExtra("win", false);
        stageScore = getIntent().getIntExtra("stageScore", 0);
        stageTime = getIntent().getIntExtra("stageTime", 0);


        topTextView = (TextView) findViewById(R.id.topTextView);
        stageScoreTextView = (TextView) findViewById(R.id.stageScoreTextView);
        currentTotalScoreTextView = (TextView) findViewById(R.id.currentTotalScoreTextView);
        pressButtonToContinueTextView = (TextView) findViewById(R.id.pressButtonToContinueTextView);
        //buttonContinue = (Button) findViewById(R.id.buttonContinue);
        buttonChangeDifficulty = (Button) findViewById(R.id.buttonChangeDifficulty);
        buttonSelectOtherGame = (Button) findViewById(R.id.buttonSelectOtherGame);


        if (DataHolder.getGameSelected() == 3) {        //score for game 3
            if (DataHolder.getScore() != 0)
                currentTotalScoreTextView.setText("Total Time: " + String.valueOf(DataHolder.getScore()) + "Seconds");
            else
                currentTotalScoreTextView.setText("Stage Time: Not Included");
        }
        else {  //score for game 1
            currentTotalScoreTextView.setText("Total Score: " + String.valueOf(DataHolder.getScore()));
        }


        if (win) {  //win
            topTextView.setText("Level " + DataHolder.getLevel() + " Cleared!");
            if (DataHolder.getLevel() < 50) {
                DataHolder.setLevel(DataHolder.getLevel() + 1); //next level
                //buttonContinue.setText("Next Level");
                pressButtonToContinueTextView.setText("Press The Red Button To Go Next Level!");
                if (DataHolder.getGameSelected() == 1)
                    stageScoreTextView.setText("Score For This Level: " + stageScore);
                if (DataHolder.getGameSelected() == 3)
                     stageScoreTextView.setText("Time For This Level: " + stageTime + "Seconds");

            }
            else {  //cleared all levels
                //buttonContinue.setText("Play Again");
                pressButtonToContinueTextView.setText("Press The Red Button To Play Again!");


                DataHolder.setLevel(1);//restart from level 1 when all level completed
            }
        }
        else {      //lose
            topTextView.setText("Level " + DataHolder.getLevel() + " Failed :(");
            //buttonContinue.setText("Retry");
            pressButtonToContinueTextView.setText("Press The Red Button To Retry Level!");

            if (DataHolder.getGameSelected() == 1)
                stageScoreTextView.setText("Score For This Level: " + stageScore);
            if (DataHolder.getGameSelected() == 3)
                stageScoreTextView.setText("Time For This Level: Not Included");
            //DataHolder.setLevel(1); //restart from level 1
            //DataHolder.setScore(0); //score back to 0
        }


        allowPressStartButton = true;
//        buttonContinue.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startActivity(continueIntent);
//            }
//        });

        buttonChangeDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(changeDifficultyIntent);
                DataHolder.setScore(0);
                DataHolder.setLevel(1); //restart from level 1
                allowPressStartButton = false;
            }
        });


        buttonSelectOtherGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(selectGameIntent);
                DataHolder.setScore(0);
                DataHolder.setLevel(1); //restart from level 1
                allowPressStartButton = false;
            }
        });
    }


    @Override
    public void onSerialReceived(String theString) {
        if (theString.equals("BUTTON PRESSED")) {           //simulates "next button" or "retry button"

            if (allowPressStartButton) {
                //Log.e("test", "button is pressed on SCORE!!!!!!!!!");
                allowPressStartButton = false;
                startActivity(continueIntent);
            }
        }
    }



}
