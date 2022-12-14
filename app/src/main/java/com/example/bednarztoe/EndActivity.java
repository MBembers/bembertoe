package com.example.bednarztoe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

                if(isOnline){
                    DatabaseReference dbref = FirebaseDatabase.getInstance("https://bednarztoe-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                    dbref.child("rooms/"+roomKey).setValue(null);
                }
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
                            Log.d("XXX", "KEY TO SEARCH: " + roomKey);
                            Room newRoom = dataSnapshot.getValue(Room.class);
                            if(newRoom != null){
                                Log.d("XXX", "REMATCH SEARCH: " + newRoom.toString());

                                if(newRoom.getTurn() == 6){
                                    Intent GameActivityIntent = new Intent(EndActivity.this, GameActivity.class);
                                    GameActivityIntent.putExtra("rematchKey", newRoom.getKey());
                                    GameActivityIntent.putExtra("online", true);
                                    startActivity(GameActivityIntent);
                                    dbref.child("rooms/"+roomKey).removeEventListener(this);
                                    return;
                                }
                            }
                            else{
                                dbref.child("rooms/"+roomKey).removeEventListener(this);
                                dbref.child("rooms/"+roomKey).setValue(null);
                                Intent MainActivityIntent = new Intent(EndActivity.this, MainActivity.class);
                                startActivity(MainActivityIntent);
                                CharSequence text = "Opponent left the game";
                                int duration = Toast.LENGTH_SHORT;

                                Toast toast = Toast.makeText(EndActivity.this, text, duration);
                                toast.show();
                                return;
                            }
                            dbref.child("rooms/"+roomKey).setValue(new Room(roomKey, null, 6));
                            return;


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    Log.d("XXX", "onClick: OFFLINE");
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
//oddanie: za tydzie?? w ??rod??
//
//[2pkt] dzia??a wersja dla 1 gracza
//[6pkt] dzia??a wersja dla 2 graczy
//- dane gry przechowywane w firebase
//    (pok??j tworzy 2 graczy, info kto z kim gra, wykonane ruchy)
//- parowanie graczy losowo lub inaczej (pok??j / dost??p po wpisaniu kodu)
//- nie mo??na nadpisywa?? wykonanych ju?? ruch??w przeciwnika!
//- mozna zagra?? z tym samym graczem rewan??
//- (opcjonalne 1pkt) je??li nie b??dzie 2-go gracza to u??ytkownik gra z automatem w trybie offline
//
//* trzy pierwsze osoby otrzymaj?? bonusowy punkt