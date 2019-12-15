package com.security;


import javafx.scene.control.Tab;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.json.JSONWriter;
import java.lang.Object;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

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
        private boolean waitInfo;

        public ServerThread(Socket c, String name, Client clientClass, SharedQueue queue, SharedTableList tableList, SharedPlayersList playersList) {
            this.client = c;
            this.name = name;
            this.clientClass = clientClass;
            this.queue = queue;
            this.tableList = tableList;
            this.joined = false;
            this.playerList = playersList;
            this.waitInfo=false;
        }

        public void run() {
            while(true){
                synchronized (playerList) {
                    this.clientClass = playerList.get(this.name);
                }
                    switch (this.clientClass.getState()){
                        case CREATE_OR_JOIN_TABLE:
                            try{
                                create_join_table();

                            }
                            catch (Exception E){
                                System.out.println(E.toString());
                            }
                            break;
                        case CREATING_TABLE:
                            create_table(this.clientClass.getNum());
                            break;
                        case JOINING_TABLE:
                            join_table(this.clientClass.getNum());
                            break;
                        case WAITING_FOR_PLAYERS:
                            try{
                                wait_for_players();

                            }
                            catch (Exception E){
                                System.out.println(E.toString());
                            }
                            break;
                        case GAME_STARTING:
                            // WORKING UNTIL HERE
                            //System.out.println("Game Starting --- >" + this.name);
                            break;
                            //TODO MAKE TABLE HANDLE THE DECK SHUFFLE AND DECK DISTRIBUTION
                    }

            }
        }
        public void create_join_table() throws IOException {
            System.out.println("create_join");
            DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
            String r = "join-create";
            JSONObject js = new JSONObject();
            js.put("val", r);
            js.put("id",Integer.parseInt(this.clientClass.getName().split(" ")[1]));
            data.writeUTF(js.toString());
            data.flush();





            DataInputStream response = new DataInputStream(this.client.getInputStream());
            JSONObject abc = new JSONObject(response.readUTF());
            boolean resp = abc.getBoolean("val");
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
                js = new JSONObject();
                js.put("val", "info");
                js.put("info", r);
                data.writeUTF(js.toString());
                data.flush();
                /**
                 * @ "-1"  -> Randomness True
                 * @ "> -1" -> Select Table
                 */
                response = new DataInputStream(this.client.getInputStream());
                int out = Integer.parseInt(new JSONObject(response.readUTF()).getString("val"));
                //TODO JOIN TABLE
                synchronized (playerList){
                    Client temp = playerList.get(this.name);
                    temp.setState(ClientState.JOINING_TABLE);
                    temp.setNum(out);
                    playerList.replace(this.name, temp);
                }

            }
            else{
                data = new DataOutputStream(this.client.getOutputStream());
                r = "";
                Iterator<Client> iC = playerList.getAll().iterator();
                while (iC.hasNext()){
                    r += iC.next().getName() + " \n";
                }
                js = new JSONObject();
                js.put("val", "info");
                js.put("info", r);
                data.writeUTF(js.toString());
                data.flush();

                response = new DataInputStream(this.client.getInputStream());
                int out = Integer.parseInt(new JSONObject(response.readUTF()).getString("val"));
                //TODO CREATE TABLE
                synchronized (playerList){
                    Client c = playerList.get(this.name);
                    c.setState(ClientState.CREATING_TABLE);
                    c.setNum(out);
                    playerList.replace(this.name, c);
                }
            }
        }

        public void create_table(int maxPlayers){
            synchronized (tableList) {
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

                    Client temp = playerList.get(clientClass.getName());
                    temp.setTable(table);
                    temp.setState(ClientState.WAITING_FOR_PLAYERS);
                    playerList.replace(clientClass.getName(),temp);
                }
            }
        }

        public void join_table(int val) {
            synchronized (tableList){
                Table t = tableList.get("Table"+val);
                synchronized (playerList){
                    Client c = playerList.get(clientClass.getName());
                    c.setTable(t);
                    this.joined = true;
                    c.setState(ClientState.WAITING_FOR_PLAYERS);
                    playerList.replace(clientClass.getName(),c);
                    t.addPlayer(playerList.get(clientClass.getName()));
                    tableList.replace(t.getName(),t);
                }
            }
        }

        public void wait_for_players() throws Exception{

            //TODO VERIFY IF TABLE HAS ENOUGH PLAYERS TO START THE GAME AND CHANGE PLAYERS STATE
            int maxPlayers;
            int currPlayers;
            synchronized (tableList){
                maxPlayers = tableList.get(this.clientClass.getTable().getName()).getMaxPlayers();
                currPlayers = tableList.get(this.clientClass.getTable().getName()).getcurrPlayers();
            }
            if(maxPlayers==currPlayers){
                synchronized (playerList){
                    Client temp = playerList.get(this.name);
                    temp.setState(ClientState.GAME_STARTING);
                    playerList.replace(this.name, temp);
                }
                synchronized (tableList){
                    Table temp = tableList.get(this.clientClass.getTable().getName());
                    temp.setState(GameState.PROVIDE_IDENTITY_TO_ALL);
                    tableList.replace(temp.getName(), temp);
                }
            }
            else{
                if(!waitInfo){
                    DataOutputStream data = new DataOutputStream(this.client.getOutputStream());
                    String r = "wait";
                    JSONObject js = new JSONObject();
                    js.put("val", r);
                    data.writeUTF(js.toString());
                    data.flush();


                    //DataInputStream response = new DataInputStream(this.client.getInputStream());
                    //System.out.println(new JSONObject(response.readUTF()).getString("val"));
                    waitInfo = true;
                }
            }



        }

    }
}