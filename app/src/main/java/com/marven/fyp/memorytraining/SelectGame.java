package com.marven.fyp.memorytraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

public class SelectGame extends BaseActivity {

    CardView gameCard1 ,gameCard2, gameCard3, gameCard4;
    private TextView game1NameText, game2NameText, game3NameText, game4NameText;
    private TextView game1DescText, game2DescText, game3DescText, game4DescText;
    Intent i ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

//        DataHolder.setData("hello");


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

        game1NameText.setText("Game 1 Name");
        game2NameText.setText("Game 2 Name");
        game3NameText.setText("Game 3 Name");
        game4NameText.setText("Game 4 Name");

        game1DescText.setText("Game 1 Desc");
        game2DescText.setText("Game 2 Desc");
        game3DescText.setText("Game 3 Desc");
        game4DescText.setText("Game 4 Desc");

        gameCard1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("GameSelected",1);
                startActivity(i);
            }
        });

        gameCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("GameSelected",2);
                startActivity(i);
            }
        });

        gameCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("GameSelected",3);
                startActivity(i);
            }
        });

        gameCard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("GameSelected",4);
                startActivity(i);
            }
        });
    }

}
