package com.security;


import javafx.scene.control.Tab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;

public class ThreadServer {

    static class ServerThread implements Runnable {

        protected Thread current = null;
        Socket client = null;
        String name;
        Client clientClass;
        SharedQueue queue;
        private SharedTableList tableList;
        private boolean joined;
        private SharedPlayersList playerList;

    public ServerThread(Socket c, String name, Client clientClass, SharedQueue queue, SharedTableList tableList, SharedPlayersList playersList) {
        this.client = c;
        this.name = name;
        this.clientClass = clientClass;
        this.queue = queue;
        this.tableList = tableList;
        this.joined=false;
        this.playerList = playersList;
    }

    public void run() {
        synchronized (this){
            this.clientClass = playerList.get(playerList.index(clientClass.getName()));
        }
        test();



    }

    public void test(){
        synchronized (this){
            this.current = Thread.currentThread();
        }
        try {
            boolean abc = this.current.getName().equals(name);
            System.out.println("Connected to client : "+this.client.getInetAddress().getHostAddress());
            while(true) {
                /*
                * VERIFICAR SE JÀ EXISTE ALGUMA TABLE CRIADA -> Shared TAble List
                 */
                synchronized (this) {
                    create_join_table();


                    wait_for_players();
                    boolean temp = true;
                    while(temp){
                        if(playerList.get(playerList.index(clientClass.getName())).getState() == ClientState.GAME_STARTING){
                            System.out.println("Got In-----------");
                            gameStarting();
                            temp = false;
                        }
                        else{
                            Thread.sleep(500);
                        }
                    }
                    boolean temp1 = true;
                    while(temp1){
                        if(this.playerList.get(playerList.index(clientClass.getName())).getState()  == ClientState.BETTING){
                            bet();
                        }
                        else{
                            Thread.sleep(500);
                        }
                    }



                    if (this.tableList.size() >= 1 && !joined) {
                        //join_table(1);
                        this.playerList.get(playerList.index(clientClass.getName())).getData();
                    } else if (this.tableList.size()<1 && !joined){
                        //create_table();
                        this.playerList.get(playerList.index(clientClass.getName())).getData();
                    }
                    else{
                        this.playerList.get(playerList.index(clientClass.getName())).getData();
                    }
                }

                //DataInputStream data = new DataInputStream(this.client.getInputStream());
                //String r = data.readUTF();
                //System.out.println(r);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public  void auth(){

    }

    public void checkOnlinePlayers(){}

    public void create_join_table() throws IOException {
        System.out.println("In-----------------------");
        DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
        String r = "join-create";
        data.writeUTF(r);
        data.flush();

        DataInputStream response = new DataInputStream(this.client.getInputStream());
        boolean resp = Boolean.parseBoolean(response.readUTF());
        /**
        * @true  -> show all tables     -> join table
        * @false -> show all players    -> create table
         */

        if(resp){
            data = new DataOutputStream(this.client.getOutputStream());
            r = "";
            for(int i=0; i<tableList.size();i++){
                Table t = tableList.get(i);
                r += t.getName() + " \n";
            }
            data.writeUTF(r);
            data.flush();
            /**
             * @ "-1"  -> Randomness True
             * @ "> -1" -> Select Table
             */
            response = new DataInputStream(this.client.getInputStream());
            int out = Integer.parseInt(response.readUTF());

            join_table(out);

        }
        else{
            data = new DataOutputStream(this.client.getOutputStream());
            r = "";
            for(int i=0; i<playerList.size();i++){
                Client t = playerList.get(i);
                r += t.getName() + " \n";
            }
            data.writeUTF(r);
            data.flush();

            response = new DataInputStream(this.client.getInputStream());
            int out = Integer.parseInt(response.readUTF());
            create_table(out);
        }
    }

    public void create_table(int maxPlayers){
        synchronized (this) {
            Table table = new Table(maxPlayers, this.queue, this.playerList.get(playerList.index(clientClass.getName())).getRandomness(), this.playerList.get(playerList.index(clientClass.getName())), this.playerList);
            Thread thread = new Thread(table);
            thread.setName("Table" + tableList.size());
            table.setName("Table" + tableList.size());
            table.setThread(thread);

            tableList.add(table);

            thread.start();
            this.joined = true;


            int pos = playerList.index(clientClass.getName());
            Client c = playerList.get(pos);
            playerList.remove(c);
            c.setTable(table);
            c.setState(ClientState.WAITING_FOR_PLAYERS);
            playerList.add(c);
        }
    }

    public void join_table(int val) {
        synchronized (this){
            Table t = tableList.get(val);
            tableList.remove(t);
            synchronized (this){
                t.addPlayer(playerList.get(playerList.index(clientClass.getName())));
            }
            Client c = playerList.get(playerList.index(clientClass.getName()));
            playerList.remove(c);
            c.setTable(t);
            tableList.add(t);
            this.joined = true;


            c.setState(ClientState.WAITING_FOR_PLAYERS);
            playerList.add(c);
        }
    }

    public void wait_for_players() throws Exception{


        DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
        String r = "wait";
        data.writeUTF(r);
        data.flush();

        //DataInputStream response = new DataInputStream(this.client.getInputStream());
       // System.out.println(response.readUTF());


        while (playerList.get(playerList.index(clientClass.getName())).getState() == ClientState.WAITING_FOR_PLAYERS){
           /* if(this.clientClass.getTable().getCurrPlayers()== this.clientClass.getTable().getcurrPlayers()){
                this.clientClass.setState(ClientState.GAME_STARTING);
            }*/
           Thread.sleep((long)Math.random()*250);


            }


    }

    public void provideIdentity(){

    }

    public synchronized void gameStarting()  throws Exception{
        while (this.playerList.get(playerList.index(clientClass.getName())).getState()  == ClientState.GAME_STARTING){
            System.out.println("GAME IS STARTING");

            int pos = playerList.index(clientClass.getName());
            Client c = playerList.get(pos);
            playerList.remove(c);

            c.setState(ClientState.BETTING);
            playerList.add(c);

            DataInputStream response = new DataInputStream(this.client.getInputStream());
            String resp = response.readUTF();
            System.out.println(resp);

        }

    }

    public void bet() throws Exception{
        System.out.println("In-----------------------");
        DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
        String r = "bet";
        data.writeUTF(r);
        data.flush();

        DataInputStream response = new DataInputStream(this.client.getInputStream());
        int resp = Integer.parseInt(response.readUTF());
        clientClass.setBet(resp);

        while (this.playerList.get(playerList.index(clientClass.getName())).getState() == ClientState.BETTING){
            Thread.sleep((long)(Math.random()*500));
        }
        int pos = playerList.index(clientClass.getName());
        Client c = playerList.get(pos);
        playerList.remove(c);

        c.setState(ClientState.SHUFFLING);
        playerList.add(c);
        shuffle();
    }

    public static void shuffle(){
    }

    public static void distribute_cards(){

    }

    public static void play(){

    }

    public static void account(){}






    }
}