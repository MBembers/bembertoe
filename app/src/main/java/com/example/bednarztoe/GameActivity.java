package com.example.bednarztoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private DatabaseReference dbref;
    private String roomKey;
    private Room currRoom;
    private int currPlayer = 0;
    private Game game;
    private GameOnline gameOnline;
    private boolean isStarting;
    private boolean joined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            boolean value = extras.getBoolean("online");
            if(value) {
                joined = false;
                isStarting = true;
                getDbConnection();
                checkForRooms();
            }
            else {
                game = new Game(0, findViewById(R.id.player1), findViewById(R.id.player2),
                        GameActivity.this, findViewById(R.id.board));
                game.start();
            }
        }

        LinearLayout box1 = findViewById(R.id.player1);
        box1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                test();
            }
        });


    }

    private void getDbConnection(){
        dbref = FirebaseDatabase.getInstance("https://bednarztoe-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    }

    private void checkForRooms(){
        dbref.child("rooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Room rum = snapshot.getValue(Room.class);
                    if(rum.getTurn() == 5) {
                        dbref.child("rooms").removeEventListener(this);
                        if(!joined)
                            joinRoom(rum);
                        return;
                    }
                }
                dbref.child("rooms").removeEventListener(this);
                if(!joined)
                    createRoom();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    private void joinRoom(Room newRoom){
        Log.d("XXX", "joinRoom: KURWA");
        joined = true;
        roomKey = newRoom.getKey();
        currRoom = newRoom;
        currRoom.setPlayer2("ready");
        currPlayer = 1;
        currRoom.setTurn(0);

        uploadRoom();
        connectToRoom();
    }

    private void createRoom(){
        joined = true;
        String newRoomKey = dbref.child("rooms").push().getKey();

        currRoom = new Room(newRoomKey, "ready");
        currRoom.setTurn(5);
        currPlayer = 0;

        uploadRoom();
        connectToRoom();
    }

    private void test(){
        Log.d("XXX", "test: " + currRoom.toString());
//        gameOnline.updateRoom(currRoom);
        gameOnline.place(1,1);
//        if(currRoom.getTurn() == currPla
//        yer){
//            currRoom.setTurn(currRoom.getTurn() == 0 ? 1 : 0);
//            uploadRoom();
//        }
    }

    private void uploadRoom(){
        dbref.child("rooms/" + currRoom.getKey()).setValue(currRoom);
    }

    private void connectToRoom(){
        dbref.child("rooms/"+currRoom.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Room rum = dataSnapshot.getValue(Room.class);
                if(rum == null){
                    dbref.child("rooms/"+currRoom.getKey()).removeEventListener(this);
                    return;
                }
                currRoom = rum;
                if(currRoom.getTurn() == 0 && Objects.equals(currRoom.getPlayer(currPlayer), "ready")){
                        currRoom.setPlayer(currPlayer,"playing");
                        gameOnline = new GameOnline(0, findViewById(R.id.player1), findViewById(R.id.player2),
                                GameActivity.this, findViewById(R.id.board), currRoom, currPlayer, dbref);
                        gameOnline.start();
                        gameOnline.updateRoom(currRoom);
                        gameOnline.uploadGame();
                        return;
                }
                if(currRoom != null && gameOnline != null){
                    Log.d("XXX", "onDataChange: " + currRoom.toString());
                    gameOnline.updateRoom(currRoom);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }
}