package com.security;

import java.io.*;
import java.util.*;

import java.util.Base64;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.json.*;

import javax.swing.plaf.synth.SynthTextAreaUI;

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
    private SharedTableList tableList;
    private byte[] deck = new byte[52];
    private int indexPlays;
    private byte[][] plays;
    private int[][] order;
    private int nextPlayer;


    public Table(int maxPlayers, SharedQueue queue, boolean rand, Client Creator, SharedPlayersList playersList, SharedTableList tableList){
        this.creator = Creator;
        this.maxPlayers = maxPlayers;
        this.c = new Client[maxPlayers];
        this.c[0] = this.creator;
        this.queue = queue;
        this.state = GameState.WAITING_FOR_PLAYERS;
        this.currPlayers = 1;
        this.randomness = rand;
        this.playersList = playersList;
        this.tableList = tableList;
        this.indexPlays = 0;
        this.plays = new byte[Double.valueOf(52/this.maxPlayers).intValue()][];
        this.order = new int[Double.valueOf(52/this.maxPlayers).intValue()][];
        this.nextPlayer = 0;

        if(this.randomness){
            pickRandomPlayers(maxPlayers-1);
            }
        }


    public void updateInternalVariables() {
        System.out.println("Updating Internal Variables");
        synchronized (tableList){
            Table t = tableList.get(this.name);
            this.c = t.c;
            this.queue = t.queue;
            this.state = t.state;
            this.currPlayers = t.currPlayers;
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

    public void setState(GameState state){
        this.state = state;
    }

    public GameState getState(){
        return this.state;
    }

    public int getMaxPlayers(){
        return this.maxPlayers;
    }
    public int getcurrPlayers(){
        return this.currPlayers;
    }

    public void createDeck(){
        System.out.println("Creating deck");
        int index = 0;
        for(int suitType = 0; suitType <= 3; suitType++) {
            for(int cardValue = 1; cardValue <= 13; cardValue++) {
                if(suitType==0){
                    byte temp = 0x00;
                    temp += (byte) cardValue;
                    deck[index] = temp;
                }
                if(suitType==1){
                    byte temp = 0x10;
                    temp += (byte) cardValue;
                    deck[index] = temp;
                }
                if(suitType==2){
                    byte temp = 0x20;
                    temp += (byte) cardValue;
                    deck[index] = temp;
                }
                if(suitType==3){
                    byte temp = 0x30;
                    temp += (byte) cardValue;
                    deck[index] = temp;
                }

                index++;
            }
        }
    }

    public void sendDeck(byte[] deck, Client c, String doThis){
        System.out.println("Sending deck to: " +c.getName());
        try{
            StringBuilder sb = new StringBuilder();
            for (byte b : deck) {
                sb.append(String.format("%02X ", b));
            }
            System.out.println(sb.toString());
            DataOutputStream out = new DataOutputStream(c.getSocket().getOutputStream());
            JSONObject js = new JSONObject();
            js.put("val", doThis);

            // Encode deck into a base64String
            String base64String = Base64.getEncoder().encodeToString(deck);
            js.put("deck",base64String);
            js.put("numPlayers", maxPlayers);

            out.writeUTF(js.toString());
            out.flush();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public boolean receiveDeck(Client c){
        try{
            DataInputStream received = new DataInputStream(c.getSocket().getInputStream());
            JSONObject resp = new JSONObject(received.readUTF());
            System.out.println(resp);
            if(received!=null){
                System.out.println("Receiving deck from " + c.getName());

                this.deck = Base64.getDecoder().decode(resp.getString("deck"));
                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            System.out.println(e);
            return false;
        }
    }

    public void sendPlay(Client c,boolean empty){
        if(empty){
            System.out.println("Sending Play to: " +c.getName());
            try{
                DataOutputStream out = new DataOutputStream(c.getSocket().getOutputStream());
                JSONObject js = new JSONObject();
                js.put("val", "play");

                // Encode deck into a base64String
                byte[] temp = new byte[0];
                String base64String = Base64.getEncoder().encodeToString(temp);
                js.put("play",base64String);
                js.put("order", temp);

                out.writeUTF(js.toString());
                out.flush();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        else{
            System.out.println("Sending Play to: " +c.getName());
            try{
                DataOutputStream out = new DataOutputStream(c.getSocket().getOutputStream());
                JSONObject js = new JSONObject();
                js.put("val", "play");

                // Encode deck into a base64String
                String base64String = Base64.getEncoder().encodeToString(plays[indexPlays]);
                js.put("play",base64String);
                js.put("order", order[indexPlays]);

                out.writeUTF(js.toString());
                out.flush();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }

    }

    public boolean receivePlay(Client c) {
        try{
            DataInputStream received = new DataInputStream(c.getSocket().getInputStream());
            JSONObject resp = new JSONObject(received.readUTF());
            System.out.println(resp);
            if(received!=null){
                System.out.println("Receiving play from " + c.getName());

                this.plays[indexPlays] = Base64.getDecoder().decode(resp.getString("play"));
                JSONArray temp = resp.getJSONArray("order");

                int[] tempArray = new int[temp.length()];
                if (temp == null) {
                    System.out.println("json is empty");
                }
                else
                {
                    int length = temp.length();
                    for (int i=0;i<length;i++){
                       tempArray[i] = temp.getInt(i);
                    }
                }

                this.order[indexPlays] = tempArray;

                return true;
            }
            else{
                return false;
            }
        }
        catch (Exception e){
            System.out.println(e);
            return false;
        }
    }
    public void evaluatePlay(){

        /** TODO
         * @evaluate_who_was_the_winning_player of the current hand and change value of this.nextPlayer to said player
         */

        byte[] currentPlay = plays[indexPlays];
        int[] currentOrder = order[indexPlays];
        int player = currentOrder[0];
        int currentWinner = 0;
        int suit =  currentPlay[player] >>> 4;
        System.out.println("SUIT IS: " + suit);

        for(int z = 0; z<currentOrder.length;z++){
            System.out.println("order: " + currentOrder[z] + " ------- play:   " + currentPlay[z]);
        }
        for(int i = 0; i< currentPlay.length;i++){
            if(currentPlay[i]>>>4 == suit){
                System.out.println(("Current play : " + currentPlay[i] + "winningPlayer: " + currentPlay[player]));
                if(currentPlay[i] > currentPlay[currentWinner]){
                    System.out.println(("Current play : " + currentPlay[i] + "winningPlayer: " + currentPlay[player]));
                    currentWinner = currentPlay[i];
                    player = currentOrder[i];
                }
            }
        }

        //Default to player 0 while function is not done
        this.nextPlayer = player;
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
    public void resetTable(){
        this.indexPlays = 0;
        this.nextPlayer = 0;
        this.plays = new byte[Double.valueOf(52/this.maxPlayers).intValue()][];
        this.order = new int[Double.valueOf(52/this.maxPlayers).intValue()][];

    }
    public static int indexOf(byte[] arr, byte val) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] == val).findFirst().orElse(-1);
    }

    @Override
    public void run(){

        /** TODO
         *
         * PROCESS QUEUE INFORMATION
         * CHANGE STATE OF PLAYERS AND TABLE
         */
        /** TODO
         *
         * SHUFFLING ALGORITHM
         * CREATE_DECK() ??  How? Which data structure?
         * SEND_DECK() ->> IN ORDER ->> player0, player1, player2, etc....
         * until all players have signed and shuffled the deck
         *
         * we need to verify who is holding the deck
         * who is shuffling right now
         * who needs to shuffle
         *
         * make everyone wait for its turn
         *
         */
        while(true){
            switch (this.getState()){
                case PROVIDE_IDENTITY_TO_ALL:
                    /** TODO
                     * Add security
                     */
                    synchronized (tableList){
                        Table thisTable = tableList.get(this.getName());
                        thisTable.setState(GameState.GAME_STARTING);
                        tableList.replace(this.getName(), thisTable);
                    }
                    /*StringBuilder sb = new StringBuilder();
                    for (byte b : deck) {
                        sb.append(String.format("%02X ", b));
                    }
                    System.out.println(sb.toString());*/
                    break;
                case GAME_STARTING:
                    createDeck();
                    resetTable();
                    synchronized (tableList){
                        Table thisTable = tableList.get(this.getName());
                        thisTable.setState(GameState.SHUFFLING);
                        tableList.replace(this.getName(), thisTable);
                    }
                    break;
                case SHUFFLING:
                    for(int i=0;i<this.c.length;i++){
                        synchronized (playersList){
                            Client temp = playersList.get(c[i].getName());
                            sendDeck(this.deck, temp,"shuffle");
                            while(!receiveDeck(temp)){System.out.println("Deck not received");};
                        }
                    }
                    synchronized (tableList){
                        Table thisTable = tableList.get(this.getName());
                        thisTable.setState(GameState.CARD_DISTRIBUTION);
                        tableList.replace(this.getName(), thisTable);
                    }
                    break;
                case CARD_DISTRIBUTION:
                    while (indexOf(deck, (byte)0xFF)!=0){
                        System.out.println(indexOf(deck, (byte)0xFF));
                        for(int i=0;i<this.c.length;i++){
                            synchronized (playersList){
                                Client temp = playersList.get(c[i].getName());
                                sendDeck(this.deck, temp,"distribute");
                                while(!receiveDeck(temp)){System.out.println("Deck not received");};
                                StringBuilder sb = new StringBuilder();
                                for (byte b : deck) {
                                    sb.append(String.format("%02X ", b));
                                }
                                System.out.println(sb.toString());
                            }
                        }
                    }
                    synchronized (tableList){
                        Table thisTable = tableList.get(this.getName());
                        thisTable.setState(GameState.PLAY);
                        tableList.replace(this.getName(), thisTable);
                    }
                    break;
                case PLAY:
                    if(indexPlays == (52/c.length-1)){
                        synchronized (tableList){
                            Table thisTable = tableList.get(this.getName());
                            thisTable.setState(GameState.GAME_STARTING);
                            tableList.replace(this.getName(), thisTable);
                        }
                    }
                    else{
                        for(int i=this.nextPlayer;i<this.c.length;i++){
                            synchronized (playersList){
                                Client temp = playersList.get(c[i].getName());
                                if(i == this.nextPlayer){
                                    sendPlay(temp,true);
                                }
                                else{
                                    sendPlay(temp,false);
                                }
                                receivePlay(temp);
                            }
                        }
                        for(int i=0;i<this.nextPlayer;i++){
                            synchronized (playersList){
                                Client temp = playersList.get(c[i].getName());
                                sendPlay(temp,false);
                                receivePlay(temp);
                            }
                        }
                        evaluatePlay();
                        indexPlays++;
                    }
                    break;
            }
        }

    }
}
