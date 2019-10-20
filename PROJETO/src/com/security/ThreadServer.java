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
        private int nTables;

    public ServerThread(Socket c, String name, Client clientClass, SharedQueue queue) {
        this.client = c;
        this.name = name;
        this.clientClass = clientClass;
        this.queue = queue;
        this.nTables = 0;
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
            while(true) {create_table();
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
        thread.setName("Table" + nTables);
        thread.start();
        nTables++;
        this.clientClass.setTable(table);
    }

    public static void join_table(){
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