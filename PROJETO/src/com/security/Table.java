package com.security;

import java.util.*;

public class Table implements Runnable{

    private Thread t;
    private Client creator;
    private int maxPlayers;
    private int currPlayers;
    private Client[] c;
    private SharedQueue queue;
    private GameState state;
    private Map<String,Integer> bets = new HashMap<String, Integer>();
    private boolean randomness;


    public Table(int maxPlayers, SharedQueue queue, boolean rand, Client Creator){
        this.t = Thread.currentThread();
        this.creator = Creator;
        this.maxPlayers = maxPlayers;
        this.c = new Client[maxPlayers];
        this.c[0] = this.creator;
        this.queue = queue;
        this.state = GameState.WAITING_FOR_PLAYERS;
        this.currPlayers = 1;
        this.randomness = rand;
        try{
            this.c[0].getData();
        }
        catch (Exception e){

        }

        if(this.randomness){
            pickRandomPlayers(maxPlayers-1);
            }
        }
    public void addPlayer(Client c){
        this.c[currPlayers] = c;
    }

    public void pickRandomPlayers(int amount) {
    }
    @Override
    public void run() {
        //TODO
        /* PROCESS QUEUE INFORMATION */
        /* CHANGE STATE OF PLAYERS AND TABLE*/

    }
}
