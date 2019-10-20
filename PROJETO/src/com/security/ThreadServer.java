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

    public ServerThread(Socket c, String name, Client clientClass, SharedQueue queue, SharedTableList tableList) {
        this.client = c;
        this.name = name;
        this.clientClass = clientClass;
        this.queue = queue;
        this.tableList = tableList;
        this.joined=false;
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

    public static boolean create_join_table(){
        return true;
    }

    public void create_table(){
        Table table = new Table(4, this.queue, this.clientClass.getRandomness(), this.clientClass);
        Thread thread = new Thread(table);
        thread.setName("Table" + tableList.size());
        table.setThread(thread);
        synchronized (this){
            tableList.add(table);
        }
        thread.start();
        this.joined=true;
        this.clientClass.setTable(table);
        System.out.println(tableList.get().getCurrPlayers());
    }

    public void join_table(){
        Table t = tableList.get();
        tableList.remove(t);
        t.addPlayer(clientClass);
        t.getPlayers();
        this.clientClass.setTable(t);
        tableList.add(t);
        tableList.get();
        this.joined=true;
        System.out.println(tableList.get().getCurrPlayers());
    }

    public static void wait_for_players(){
    }

    public static void provideIdentity(){
    }

    public static void gameStarting(){
    }

    public static void bet(){}

    public static void shuffle(){
    }

    public static void distribute_cards(){

    }

    public static void play(){

    }

    public static void account(){}






    }
}