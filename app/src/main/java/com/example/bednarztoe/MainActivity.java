package com.example.bednarztoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference dbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatButton offlineBtn = findViewById(R.id.offlinebtn);
        offlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                GameActivityIntent.putExtra("online", false);
                startActivity(GameActivityIntent);
            }
        });

        AppCompatButton onlinebtn = findViewById(R.id.onlinebtn);
        onlinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent GameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                GameActivityIntent.putExtra("online", true);
                startActivity(GameActivityIntent);
            }
        });
    }
}