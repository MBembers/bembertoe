package com.example.bednarztoe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameOnline extends Game{

    private Room room;
    private int currPlayer;
    private DatabaseReference dbref;

    public GameOnline(int turn, LinearLayout player1, LinearLayout player2, Context _context,
                      LinearLayout boardLayout, Room room, int currPlayer, DatabaseReference dbref) {
        super(turn, player1, player2, _context, boardLayout);
        this.room = room;
        this.currPlayer = currPlayer;
        this.dbref = dbref;
    }

    @Override
    public void start(){
        Log.d("XXX", "start: curr player : " + currPlayer);
        player1.setBackground(_context.getDrawable(R.color.opaqRed));

        game = new int[3][3];
        buttons = new LinearLayout[3][3];

        for (int i = 0; i < 3; i++) {
            LinearLayout row = new LinearLayout(_context);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, _context.getResources().getDisplayMetrics()));
            row.setLayoutParams(params);
            for (int j = 0; j < 3; j++){
                LinearLayout tile = new LinearLayout(_context);
                tile.setBackground(_context.getDrawable(R.drawable.tile));

                LinearLayout.LayoutParams paramss = new LinearLayout.LayoutParams(
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, _context.getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, _context.getResources().getDisplayMetrics()));
                tile.setLayoutParams(paramss);

                int finalI = i;
                int finalJ = j;
                tile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(game[finalI][finalJ] == 0 && (turn == 0 || turn == 1))
                            place(finalI, finalJ);
                    }
                });
                row.addView(tile);
                buttons[i][j] = tile;
                game[i][j] = 0;
            }
            boardLayout.addView(row);
        }
    }

    public void place(int y, int x) {
        Log.d("XXX", "place: " + room.toString());
        if (room.getTurn() == currPlayer) {
            player1.setBackground(currPlayer == 0 ? _context.getDrawable(R.color.opaqWhite) : _context.getDrawable(R.color.opaqRed));
            player2.setBackground(currPlayer == 0 ? _context.getDrawable(R.color.opaqRed) : _context.getDrawable(R.color.opaqWhite));
            buttons[y][x].setBackground(currPlayer == 0 ? _context.getDrawable(R.drawable.cross) : _context.getDrawable(R.drawable.circle));
            game[y][x] = currPlayer + 1;
            turn = currPlayer == 0 ? 1 : 0;

            uploadGame();
        } else {
            return;
        }
        check();
    }

    public void updateRoom(Room room1){
        room = new Room(room1);
        turn = room.getTurn();
        Log.d("XXX", "updateRoom: " + room.toString());

        player1.setBackground(room.getTurn() == 1 ? _context.getDrawable(R.color.opaqWhite) : _context.getDrawable(R.color.opaqRed));
        player2.setBackground(room.getTurn() == 1 ? _context.getDrawable(R.color.opaqRed) : _context.getDrawable(R.color.opaqWhite));

        int[][] newGame = twoDArrayFromList(room.getBoard());
        if(newGame != null)
            game = newGame;
        for (int x = 0; x < 3; x++){
            for ( int y = 0; y < 3; y++){
                if(game != null)
                {
                    Drawable drawable = _context.getDrawable(R.drawable.tile);
                    if(game[y][x] == 1)
                        drawable = _context.getDrawable(R.drawable.cross);
                    else if( game[y][x] == 2)
                        drawable= _context.getDrawable(R.drawable.circle);

                    buttons[y][x].setBackground(drawable);
                }
            }
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

    public void uploadGame(){
        room.setTurn(turn);
        room.setBoard(twoDArrayToList(game));
        dbref.child("rooms/" + room.getKey()).setValue(room);
    }
}
