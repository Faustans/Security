package com.security;

import java.util.*;

public class Table implements Runnable{

    private Thread t;
    private String name;
    private Client creator;
    private int maxPlayers;
    private int currPlayers;
    private Client[] c;
    private SharedQueue queue;
    private GameState state;
    private Map<String,Integer> bets = new HashMap<String, Integer>();
    private boolean randomness;
    private SharedPlayersList playersList;


    public Table(int maxPlayers, SharedQueue queue, boolean rand, Client Creator, SharedPlayersList playersList){
        this.creator = Creator;
        this.maxPlayers = maxPlayers;
        this.c = new Client[maxPlayers];
        this.c[0] = this.creator;
        this.queue = queue;
        this.state = GameState.WAITING_FOR_PLAYERS;
        this.currPlayers = 1;
        this.randomness = rand;
        this.playersList = playersList;

        if(this.randomness){
            pickRandomPlayers(maxPlayers-1);
            }
        }
    public synchronized void addPlayer(Client c){
        this.c[currPlayers] = c;
        this.currPlayers++;
    }

    public void setThread(Thread t){
        this.t = t;
    }

    public int getCurrPlayers(){
        return this.currPlayers;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public void getPlayers(){
        String res = "";
        for (int i = 0; i < this.c.length; i++) {
            if(c[i]!=null) {
                res += c[i] + " ";
            }

        }
    }

    public void pickRandomPlayers(int amount) {
    }

    public int getMaxPlayers(){
        return this.maxPlayers;
    }
    public int getcurrPlayers(){
        return this.currPlayers;
    }

    @Override
    public void run(){
        //TODO
        /* PROCESS QUEUE INFORMATION */
        /* CHANGE STATE OF PLAYERS AND TABLE*/
        boolean done = false;
        while(!done){
            synchronized (this) {
                if (this.maxPlayers == this.currPlayers) {
                    for (int i = 0; i < c.length; i++) {
                        int pos = playersList.index(c[i].getName());
                        Client cl = playersList.get(pos);
                        playersList.remove(cl);

                        cl.setState(ClientState.GAME_STARTING);
                        playersList.add(cl);


                    }
                    System.out.println("Setting state");
                    done = true;
                }
            }



        }

    }
}
