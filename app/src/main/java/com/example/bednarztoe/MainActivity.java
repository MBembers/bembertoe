package com.example.bednarztoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatButton offlineBtn = findViewById(R.id.offlinebtn);
        offlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(GameActivityIntent);
            }
        });
    }
}