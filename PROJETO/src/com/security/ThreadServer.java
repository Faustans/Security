package com.security;


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
                    System.out.println(create_join_table());


                    wait_for_players();

                    if (this.tableList.size() >= 1 && !joined) {
                        join_table();
                        this.clientClass.getData();
                    } else if (this.tableList.size()<1 && !joined){
                        create_table();
                        this.clientClass.getData();
                    }
                    else{
                        this.clientClass.getData();
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

    public boolean create_join_table() throws IOException {
        System.out.println("In-----------------------");
        DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
        String r = "join-create";
        data.writeUTF(r);
        data.flush();

        DataInputStream response = new DataInputStream(this.client.getInputStream());
        boolean resp = Boolean.parseBoolean(response.readUTF());

        return resp;
    }

    public void create_table(){
        Table table = new Table(4, this.queue, this.clientClass.getRandomness(), this.clientClass);
        Thread thread = new Thread(table);
        thread.setName("Table" + tableList.size());
        table.setName("Table" + tableList.size());
        table.setThread(thread);
        synchronized (this){
            tableList.add(table);
        }
        thread.start();
        this.joined=true;
        this.clientClass.setTable(table);
    }

    public void join_table() {
        Table t = tableList.get();
        tableList.remove(t);
        t.addPlayer(clientClass);
        t.getPlayers();
        this.clientClass.setTable(t);
        tableList.add(t);
        tableList.get();
        this.joined = true;
    }

    public void wait_for_players() throws Exception{
        clientClass.setState(ClientState.WAITING_FOR_PLAYERS);
        System.out.println("In-----------------------");
        DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
        String r = "wait";
        data.writeUTF(r);
        data.flush();

        while (this.clientClass.getState() == ClientState.WAITING_FOR_PLAYERS){
            Thread.sleep((long)(Math.random()*500));
        }
        if(this.clientClass.getState() == ClientState.GAME_STARTING){
            gameStarting();
        }
    }

    public void provideIdentity(){

    }

    public void gameStarting()  throws Exception{
        while (this.clientClass.getState() == ClientState.WAITING_FOR_PLAYERS){
            Thread.sleep((long)(Math.random()*500));
        }
        if(this.clientClass.getState() == ClientState.BETTING){
            bet();
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

        while (this.clientClass.getState() == ClientState.BETTING){
            Thread.sleep((long)(Math.random()*500));
        }
        this.clientClass.setState(ClientState.SHUFFLING);
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