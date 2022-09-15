package com.example.bednarztoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private DatabaseReference dbref;
    private String roomKey;
    private Room currRoom;
    private int currPlayer = 0;
    private boolean isStarting;
    private boolean joined;
    private int turn = 0; // 0 - x | 1 - o | 2 - winX | 3 - winY | 4 - draw | 5 - pending
    private LinearLayout[][] buttons;
    private int[][] game; // 0 - empty | 1 - x | 2 - y
    private LinearLayout player1;
    private LinearLayout player2;
    private LinearLayout boardLayout;
    private boolean isOnline;
    private String rematchKey = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);
        boardLayout = findViewById(R.id.board);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isOnline = extras.getBoolean("online");
            if(isOnline) {
                rematchKey = extras.getString("rematchKey");
                joined = false;
                isStarting = true;
                getDbConnection();
                if(rematchKey != null){
                    Log.d("XXX", "rematchKey: " + rematchKey);
                    currRoom = new Room(rematchKey, "ready", 6);
                    connectToRoom();
                }
                else{
                    checkForRooms();
                }
            }
            else {
                this.start();
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
                Log.d("XXX", "checkForRoomsDataChange: ");
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
        Log.d("XXX", "joinRoom: " + newRoom.toString());
        roomKey = newRoom.getKey();
        currRoom = newRoom;
        currRoom.setPlayer2("playing");
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
                Log.d("XXX", "onDataChange "+currPlayer+" : " + currRoom.toString() + " : " + Objects.equals(currRoom.getPlayer(currPlayer), "ready"));

                if(currRoom.getTurn() == 0 && Objects.equals(currRoom.getPlayer(currPlayer), "ready") ){
                        currRoom.setPlayer(currPlayer,"playing");
                        uploadRoom();
                        turn = currRoom.getTurn();
                        start();
                        return;
                }
                if(currRoom != null && !joined){
                    joined = true;
                    start();
                    return;
                }
                if(currRoom != null){
                    updateRoom();
                    if(currRoom.getTurn() > 1 && currRoom.getTurn() < 5){
                        dbref.child("rooms/"+currRoom.getKey()).removeEventListener(this);
                        end(currRoom.getTurn());
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
            }
        });
    }

    public void start(){

        Log.d("XXX", "start: ");
        player1.setBackground(getDrawable(R.color.opaqRed));

        game = new int[3][3];
        buttons = new LinearLayout[3][3];

        for (int i = 0; i < 3; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics()));
            row.setLayoutParams(params);
            for (int j = 0; j < 3; j++){
                LinearLayout tile = new LinearLayout(this);
                tile.setBackground(getDrawable(R.drawable.tile));

                LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics()));
                tile.setLayoutParams(paramss);

                int finalI = i;
                int finalJ = j;
                tile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("XXX", "onClick: " + turn);
                        if(game[finalI][finalJ] == 0 && (turn == 0 || turn == 1))
                            if(isOnline) placeOnline(finalI, finalJ);
                            else place(finalI, finalJ);
                    }
                });
                row.addView(tile);
                buttons[i][j] = tile;
                game[i][j] = 0;
            }
            boardLayout.addView(row);
        }
    }

    public void uploadGame(){
        currRoom.setTurn(turn);
        currRoom.setBoard(twoDArrayToList(game));
        dbref.child("rooms/" + currRoom.getKey()).setValue(currRoom);
    }

    public void updateRoom(){
        turn = currRoom.getTurn();
        Log.d("XXX", "updatecurrRoom: " + currRoom.toString());

        player1.setBackground(currRoom.getTurn() == 1 ? getDrawable(R.color.opaqWhite) : getDrawable(R.color.opaqRed));
        player2.setBackground(currRoom.getTurn() == 1 ? getDrawable(R.color.opaqRed) : getDrawable(R.color.opaqWhite));

        int[][] firebaseGame = twoDArrayFromList(currRoom.getBoard());
        if(firebaseGame != null)
            game = firebaseGame;
        for (int x = 0; x < 3; x++){
            for ( int y = 0; y < 3; y++){
                if(game != null)
                {
                    Drawable drawable = getDrawable(R.drawable.tile);
                    if(game[y][x] == 1)
                        drawable = getDrawable(R.drawable.cross);
                    else if( game[y][x] == 2)
                        drawable= getDrawable(R.drawable.circle);

                    buttons[y][x].setBackground(drawable);
                }
            }
        }
    }
    
    public void placeOnline(int y, int x) {
        Log.d("XXX", "placeOnline: " + currRoom.toString());
        if (currRoom.getTurn() == currPlayer) {
            player1.setBackground(currPlayer == 0 ? getDrawable(R.color.opaqWhite) : getDrawable(R.color.opaqRed));
            player2.setBackground(currPlayer == 0 ? getDrawable(R.color.opaqRed) : getDrawable(R.color.opaqWhite));
            buttons[y][x].setBackground(currPlayer == 0 ? getDrawable(R.drawable.cross) : getDrawable(R.drawable.circle));

            game[y][x] = currPlayer + 1;
            turn = currPlayer == 0 ? 1 : 0;
            uploadGame();
        } else {
            return;
        }
        check();
    }

    private void place(int y, int x){
        Log.d("XXX", "place: ");
        if(turn == 0){
            player1.setBackground(getDrawable(R.color.opaqWhite));
            player2.setBackground(getDrawable(R.color.opaqRed));
            buttons[y][x].setBackground(getDrawable(R.drawable.cross));
            game[y][x] = 1;
            turn = 1;
        }
        else if(turn == 1){
            player2.setBackground(getDrawable(R.color.opaqWhite));
            player1.setBackground(getDrawable(R.color.opaqRed));
            buttons[y][x].setBackground(getDrawable(R.drawable.circle));
            game[y][x] = 2;
            turn = 0;
        }
        else {
            return;
        }
        check();
    }

    private void end(int result){
        if(isOnline){
            currRoom.setTurn(result);
            uploadRoom();
        }
        if(result == 2){
            Log.d("XXX", "end: cross wins");
        }
        if(result == 3){
            Log.d("XXX", "end: circle wins");
        }
        Intent i = new Intent(GameActivity.this, EndActivity.class);
        i.putExtra("result", result);
        i.putExtra("isOnline", isOnline);
        i.putExtra("player", currPlayer);
        if(isOnline) i.putExtra("key", roomKey);
        startActivity(i);
    }

    private void check(){
        boolean isFull = true;
        int crossc = 0;
        int circlec = 0;
        for(int i = 0; i < 3; i++){
            circlec = 0;
            crossc = 0;
            for (int j = 0; j < 3; j++){
                if(game[i][j] == 1){
                    crossc += 1;
                    circlec = 0;
                }
                else if(game[i][j] == 2){
                    circlec += 1;
                    crossc = 0;
                }
                else {
                    isFull = false;
                    circlec = 0;
                    crossc = 0;
                }
            }
            if(crossc >= 3) {
                turn = 2;
                end(2);
                return;
            }
            if (circlec >= 3) {
                turn = 3;
                end(3);
                return;
            }
        }

        for(int i = 0; i < 3; i++){
            circlec = 0;
            crossc = 0;
            for (int j = 0; j < 3; j++){
                if(game[j][i] == 1){
                    crossc += 1;
                    circlec = 0;
                }
                else if(game[j][i] == 2){
                    circlec += 1;
                    crossc = 0;
                }
                else {
                    circlec = 0;
                    crossc = 0;
                }
            }
            if(crossc >= 3) {
                turn = 2;
                end(2);
                return;
            }
            if (circlec >= 3){
                turn = 3;
                end(3);
                return;
            }
        }
        if((game[0][0] == 1 && game[1][1] == 1 && game[2][2] == 1) ||(game[0][2] == 1 && game[1][1] == 1 && game[2][0] == 1)){
            turn = 2;
            end(2);
            return;
        }

        if((game[0][0] == 2 && game[1][1] == 2 && game[2][2] == 2) ||(game[0][2] == 2 && game[1][1] == 2 && game[2][0] == 2)){
            turn = 2;
            end(2);
            return;
        }
        if(circlec < 3 && crossc < 3 && isFull){
            end(4);
            return;
        }
    }
    public ArrayList<ArrayList<Integer>> twoDArrayToList(int[][] twoDArray) {
        if(twoDArray == null) return null;
        ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
        int i = 0;
        for (int[] array : twoDArray) {
            list.add(new ArrayList<Integer>());
            for(int num : array) {
                list.get(i).add(num);
            }
            i++;
        }
        return list;
    }

    public int[][] twoDArrayFromList( ArrayList<ArrayList<Integer>> list){
        if(list == null) return null;
        int[][] array = new int[3][3];
        int i = 0;
        for(ArrayList<Integer> arr : list){
            int j = 0;
            for(Integer num : arr){
                array[i][j] = num;
                j++;
            }
            i++;
        }
        return array;
    }
}