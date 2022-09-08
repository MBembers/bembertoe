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

public class GameActivity extends AppCompatActivity {

    private int turn = 0; // 0 - x | 1 - o | 2 - winX | 3 - winY | 4 - draw
    private LinearLayout[][] buttons;
    private int[][] game; // 0 - empty | 1 - x | 2 - y
    private LinearLayout player1;
    private LinearLayout player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        player1 = findViewById(R.id.player1);
        player2 = findViewById(R.id.player2);

        player1.setBackground(getDrawable(R.color.opaqRed));

        game = new int[3][3];
        buttons = new LinearLayout[3][3];

        LinearLayout boardLayout = findViewById(R.id.board);
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

    private void place(int y, int x){
        boolean isFull = true;
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

    private void end(int result){
        if(result == 2){
            Log.d("XXX", "end: cross wins");
        }
        if(result == 3){
            Log.d("XXX", "end: circle wins");
        }
        Intent i = new Intent(GameActivity.this, EndActivity.class);
        i.putExtra("result", result);
        startActivity(i);
    }
}