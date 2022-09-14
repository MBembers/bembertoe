package com.example.bednarztoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    private boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView resultBox = findViewById(R.id.resultbox);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("result");
            isOnline = extras.getBoolean("isOnline");
            if(value == 2){
                resultBox.setText("Result: cross wins");
            }
            if(value == 3){
                resultBox.setText("Result: circle wins");
            }
            if(value == 4){
                resultBox.setText("Result: draw");
            }
        }

        AppCompatButton backbtn = findViewById(R.id.backtomenu);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GameActivityIntent = new Intent(EndActivity.this, MainActivity.class);
                startActivity(GameActivityIntent);
            }
        });

        AppCompatButton restartbtn = findViewById(R.id.playagain);
        restartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GameActivityIntent = new Intent(EndActivity.this, GameActivity.class);
                GameActivityIntent.putExtra("online", isOnline);
                startActivity(GameActivityIntent);
            }
        });
    }
}