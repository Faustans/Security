package com.security;


import javafx.scene.control.Tab;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.util.Collection;
import java.util.Iterator;

public class ThreadServerBak {

    static class ServerThreadBak implements Runnable {

        protected Thread current = null;
        Socket client = null;
        String name;
        Client clientClass;
        SharedQueue queue;
        private SharedTableList tableList;
        private boolean joined;
        private SharedPlayersList playerList;

        public ServerThreadBak(Socket c, String name, Client clientClass, SharedQueue queue, SharedTableList tableList, SharedPlayersList playersList) {
            this.client = c;
            this.name = name;
            this.clientClass = clientClass;
            this.queue = queue;
            this.tableList = tableList;
            this.joined=false;
            this.playerList = playersList;
        }

        public void run() {
            synchronized (playerList){
                this.clientClass = playerList.get(clientClass.getName());
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
                    create_join_table();


                    wait_for_players();

                   /* ClientState st = playerList.get(playerList.index(clientClass.getName())).getState();
                    switch (st){
                        case GAME_STARTING:
                            System.out.println("Got In-----------");
                            gameStarting();
                    }*/

                    boolean temp = true;
                    while(temp){
                        synchronized (playerList){
                            System.out.println(playerList.get(clientClass.getName()).getState() + "     ------      " + clientClass.getName());
                            if(playerList.get(clientClass.getName()).getState() == ClientState.GAME_STARTING){
                                System.out.println("Got In-----------");
                                gameStarting();
                                temp = false;
                            }
                            else{
                                Thread.sleep(500);
                            }
                        }

                    }
                    boolean temp1 = true;
                    while(temp1){
                        synchronized (playerList) {
                            if (this.playerList.get(clientClass.getName()).getState() == ClientState.BETTING) {
                                bet();
                            } else {
                                Thread.sleep(500);
                            }
                        }
                    }

                    boolean temp2 = true;
                    while(temp2){
                        if(this.playerList.get(clientClass.getName()).getState()  == ClientState.SHUFFLING){
                            System.out.println("Suffling called");
                            shuffle();

                        }
                        else{
                            Thread.sleep(500);
                        }
                    }



                    if (this.tableList.size() >= 1 && !joined) {
                        //join_table(1);
                        this.playerList.get(clientClass.getName()).getData();
                    } else if (this.tableList.size()<1 && !joined){
                        //create_table();
                        this.playerList.get(clientClass.getName()).getData();
                    }
                    else{
                        this.playerList.get(clientClass.getName()).getData();
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
            System.out.println("create_join");
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
                Iterator<Table> iC = tableList.getAll().iterator();
                while (iC.hasNext()){
                    r += iC.next().getName() + " \n";
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
                Iterator<Client> iC = playerList.getAll().iterator();
                while (iC.hasNext()){
                    r += iC.next().getName() + " \n";
                }
                data.writeUTF(r);
                data.flush();

                response = new DataInputStream(this.client.getInputStream());
                int out = Integer.parseInt(response.readUTF());
                create_table(out);
            }
        }

        public void create_table(int maxPlayers){
            /*synchronized (tableList) {
                Table table = new Table(maxPlayers, this.queue, this.playerList.get(clientClass.getName()).getRandomness(), this.playerList.get(clientClass.getName()), this.playerList, this.tableList);
                Thread thread = new Thread(table);
                thread.setName("Table" + tableList.size());
                table.setName("Table" + tableList.size());
                table.setThread(thread);

                tableList.add(table);

                thread.start();
                this.joined = true;

                //Changing client set table

                synchronized (playerList){


                    Client c = playerList.get(clientClass.getName());
                    c.setTable(table);
                    c.setState(ClientState.WAITING_FOR_PLAYERS);
                    playerList.replace(clientClass.getName(),c);
                }
            }*/
        }

        public void join_table(int val) {
            /*synchronized (tableList){
                Table t = tableList.get("Table"+val);
                synchronized (playerList){
                    t.addPlayer(playerList.get(clientClass.getName()));
                }
                Client c = playerList.get(clientClass.getName());

                c.setTable(t);
                tableList.replace(t.getName(),t);
                this.joined = true;


                c.setState(ClientState.WAITING_FOR_PLAYERS);
                playerList.replace(clientClass.getName(),c);
            }*/
        }

        public void wait_for_players() throws Exception{


            DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
            String r = "wait";
            data.writeUTF(r);
            data.flush();

            //DataInputStream response = new DataInputStream(this.client.getInputStream());
            // System.out.println(response.readUTF());


            while (playerList.get(clientClass.getName()).getState() == ClientState.WAITING_FOR_PLAYERS){
           /* if(this.clientClass.getTable().getCurrPlayers()== this.clientClass.getTable().getcurrPlayers()){
                this.clientClass.setState(ClientState.GAME_STARTING);
            }*/
                Thread.sleep((long)Math.random()*250);
                synchronized (playerList){
                    if(playerList.get(clientClass.getName()).getState() == ClientState.WAITING_FOR_PLAYERS){
                        continue;
                    }
                }


            }


        }

        public void provideIdentity(){

        }

        public void gameStarting()  throws Exception{
            synchronized (playerList){
                while (this.playerList.get(clientClass.getName()).getState()  == ClientState.GAME_STARTING){
                    System.out.println("GAME IS STARTING");

                    Client c = playerList.get(clientClass.getName());

                    c.setState(ClientState.BETTING);
                    playerList.replace(clientClass.getName(),c);

                    DataInputStream response = new DataInputStream(this.client.getInputStream());
                    String resp = response.readUTF();
                    System.out.println(resp);
                }
            }


        }

        public void bet() throws Exception{
            System.out.println("bet------------");
            DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
            String r = "bet";
            data.writeUTF(r);
            data.flush();

            DataInputStream response = new DataInputStream(this.client.getInputStream());
            int resp = Integer.parseInt(response.readUTF());
            Client abc = playerList.get(this.name);
            abc.setBet(resp);
            playerList.replace(name, abc);

            while (this.playerList.get(clientClass.getName()).getState() == ClientState.BETTING){
                Thread.sleep((long)(Math.random()*500));
            }

            synchronized (playerList){
                Client c = playerList.get(clientClass.getName());
                c.setState(ClientState.SHUFFLING);
                playerList.replace(clientClass.getName(),c);
            }


        }

        public void shuffle(){
            synchronized (tableList){
                Table t = tableList.get(playerList.get(this.name).getTable().getName());
                if(!(t.getState()==GameState.SHUFFLING)){
                    t.setState(GameState.SHUFFLING);
                    tableList.replace(t.getName(), t);
                }
                else{
                    /** TODO SOMETHING HERE
                     *
                     * WAIT FOR TABLE TO TELL ME TO SEND DECK TO CLIENT
                     * PROBABLY USING THE QUEUE
                     *
                     */
                }
            }

        }

        public static void distribute_cards(){

        }

        public static void play(){

        }

        public static void account(){}






    }
}