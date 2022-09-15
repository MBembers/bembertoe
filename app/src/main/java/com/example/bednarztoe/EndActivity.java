package com.example.bednarztoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EndActivity extends AppCompatActivity {

    private boolean isOnline;
    private String roomKey;
    private int currPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        TextView resultBox = findViewById(R.id.resultbox);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("result");
            isOnline = extras.getBoolean("isOnline");
            if(isOnline){
                roomKey = extras.getString("key");
                currPlayer = extras.getInt("player");
            }
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
        if(isOnline) restartbtn.setText("Rematch");
        restartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline) {
                    DatabaseReference dbref = FirebaseDatabase.getInstance("https://bednarztoe-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    dbref.child("rooms/"+roomKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Room newRoom = dataSnapshot.getValue(Room.class);
                            if(newRoom != null){
                                if(newRoom.getTurn() == 6){
                                    Intent GameActivityIntent = new Intent(EndActivity.this, GameActivity.class);
                                    GameActivityIntent.putExtra("rematchKey", newRoom.getKey());
                                    GameActivityIntent.putExtra("online", true);
                                    startActivity(GameActivityIntent);
                                    dbref.child("rooms/"+roomKey).removeEventListener(this);
                                    return;
                                }
                            }
                            dbref.child("rooms/"+roomKey).setValue(new Room(roomKey, "rematch", 6));
                            Intent GameActivityIntent = new Intent(EndActivity.this, GameActivity.class);
                            GameActivityIntent.putExtra("rematchKey", roomKey);
                            GameActivityIntent.putExtra("online", true);
                            startActivity(GameActivityIntent);
                            return;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Intent GameActivityIntent = new Intent(EndActivity.this, GameActivity.class);
                    GameActivityIntent.putExtra("online", isOnline);
                    startActivity(GameActivityIntent);
                }
            }
        });
    }
}

//TicTacToe cz2. online
//liczba pkt: 8
//oddanie: za tydzień w środę
//
//[2pkt] działa wersja dla 1 gracza
//[6pkt] działa wersja dla 2 graczy
//- dane gry przechowywane w firebase
//    (pokój tworzy 2 graczy, info kto z kim gra, wykonane ruchy)
//- parowanie graczy losowo lub inaczej (pokój / dostęp po wpisaniu kodu)
//- nie można nadpisywać wykonanych już ruchów przeciwnika!
//- mozna zagrać z tym samym graczem rewanż
//- (opcjonalne 1pkt) jeśli nie będzie 2-go gracza to użytkownik gra z automatem w trybie offline
//
//* trzy pierwsze osoby otrzymają bonusowy punkt