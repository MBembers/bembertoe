package com.example.bednarztoe;

public class Room {
    private String key;
    private String player1;
    private String player2;
    private int turn;
    private int[][] board;

    public Room(){
        // Default for firebase
    }

    public Room(String key, String player1) {
        this.key = key;
        this.player1 = player1;
        this.player2 = null;
        this.turn = 5;
        this.board = null;
    }


    public Room(String key, String player1, String player2, int turn, int[][] board) {
        this.key = key;
        this.player1 = player1;
        this.player2 = player2;
        this.turn = turn;
        this.board = board;
    }

    public void update(String key, String player1, String player2, int turn, int[][] board) {
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

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }
}
