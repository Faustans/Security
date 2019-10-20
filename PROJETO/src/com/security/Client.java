package com.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;


public class Client {

    private Socket s;
    private String name;
    private Thread thread;
    private ClientState state;//enum with current available states
    private boolean randomness;
    private int bet;
    private Table table;
    private boolean tableJoin;

    public Client(Socket s, String name, ClientState state){
        this.s = s;
        this.name = name;
        this.state = state;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setTable(Table t){
        this.table = t;
    }

    public void setBet(int bet){
        this.bet = bet;
    }

    /**
     *  sets the value:
     *  True -> Join table
     *  False -> Create table
     */
    public void setTableJoin(boolean val){
        this.tableJoin = val;
    }

    public void setRandomness(boolean rand){
        this.randomness = rand;
    }

    public boolean getRandomness(){
        return this.randomness;
    }

    public int getBet() {
        return bet;
    }

    public boolean getTableJoin(){
        return this.tableJoin;
    }

    public void getData()throws Exception{
        DataInputStream data = new DataInputStream(this.s.getInputStream());
        String r = data.readUTF();
        System.out.println(thread.getName());
        System.out.println(thread.getId());
        System.out.println(Thread.currentThread().getName());
        System.out.println(Thread.currentThread().getId());
        System.out.println(name);
        System.out.println(r);
    }
}