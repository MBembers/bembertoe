package com.example.bednarztoe;

import java.util.ArrayList;

public class Room {
    private String key;
    private String player1;
    private String player2;
    private int turn;
    private ArrayList<ArrayList<Integer>> board;

    public Room(){
        // Default for firebase
    }

    public Room(Room another){
        player2 = another.player2;
        player1 = another.getPlayer1();
        turn = another.getTurn();
        key = another.getKey();
        board = another.getBoard();
    }

    public Room(String key, String player1) {
        this.key = key;
        this.player1 = player1;
        this.player2 = null;
        this.turn = 5;
        this.board = null;
    }

    public Room(String key, String player1, int turn) {
        this.key = key;
        this.player1 = player1;
        this.player2 = null;
        this.turn = turn;
        this.board = null;
    }

    public Room(String key, String player1, String player2, int turn, ArrayList<ArrayList<Integer>> board) {
        this.key = key;
        this.player1 = player1;
        this.player2 = player2;
        this.turn = turn;
        this.board = board;
    }

    public void update(String key, String player1, String player2, int turn, ArrayList<ArrayList<Integer>> board) {
        this.key = key;
        this.player1 = player1;
        this.player2 = player2;
        this.turn = turn;
        this.board = board;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public ArrayList<ArrayList<Integer>> getBoard() {
        return board;
    }

    public void setBoard(ArrayList<ArrayList<Integer>> board) {
        this.board = board;
    }

    public String getPlayer(int num){
        if(num == 0) return player1;
        else return player2;
    }
    public void setPlayer(int num, String value){
        if(num == 0) player1 = value;
        else player2 = value;
    }

    @Override
    public String toString() {
        return "Room{" +
                "key='" + key + '\'' +
                ", player1='" + player1 + '\'' +
                ", player2='" + player2 + '\'' +
                ", turn=" + turn +
                '}';
    }
}
