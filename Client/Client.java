//package com.security;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.util.Base64;
import java.util.Random;
import java.util.stream.IntStream;

import org.json.*;


public class Client {

    static byte[] myCards;
    static int numPlayers;
    static int cardsPicked = 0;

    public static void main(String[] args) {
        String temp;
        String displayBytes;
        boolean deckReceived = false;
        int id;

        byte[] deck = new byte[52];
        try {
            //create input stream
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            //create client socket, connect to server
            Socket clientSocket = new Socket("localhost",7000);
            //create output stream attached to socket
            while(true) {
                try{
                    deckReceived = false;
                    DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
                    JSONObject disp = new JSONObject(inFromServer.readUTF());
                    System.out.println(disp.toString());
                    String val = disp.getString("val") ;
                    System.out.println(val);
                    System.out.println("-----------------------");

                    switch (disp.getString("val").toLowerCase()){
                        case("create-join"):
                            id = disp.getInt("id");
                            break;
                        case("shuffle"):
                            System.out.println("Got deck");
                            deckReceived = true;
                            numPlayers=disp.getInt("numPlayers");
                            deck = Base64.getDecoder().decode(disp.getString("deck"));
                            myCards = new byte[Double.valueOf(52/numPlayers).intValue()];
                            deck = shuffleArray(deck);
                            cardsPicked = 0;
                            break;
                        case("distribute"):
                            System.out.println("Got deck");
                            deckReceived = true;
                            deck = Base64.getDecoder().decode(disp.getString("deck"));
                            if(cardsPicked<Double.valueOf(52/numPlayers).intValue()){
                                deck = distribute(deck);
                                StringBuilder sb = new StringBuilder();
                                for (byte b : deck) {
                                    sb.append(String.format("%02X ", b));
                                }
                                System.out.println(sb.toString());
                            }
                            break;

                    }

                  }
                  catch(Exception e){

                  }
                DataOutputStream outToServer =
                        new DataOutputStream(clientSocket.getOutputStream());

                System.out.print("Command : ");

                temp = inFromUser.readLine();
                //send line to server
                //outToServer.writeBytes(temp);

                JSONObject js = new JSONObject();
                js.put("val", temp);
                if(deckReceived){
                    System.out.println("Sending deck");
                    js.put("deck",Base64.getEncoder().encodeToString(deck));
                }
                outToServer.writeUTF(js.toString());
                outToServer.flush();
            }
        }
        catch(Exception ex)
        {

        }
    }

    public static byte[] distribute(byte[] array){
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02X ", b));
        }
        System.out.println(sb.toString());
        int maxArray = indexOf(array, (byte)0xFF);
        if(maxArray==-1){
            maxArray=52;
        }
        double rand = (double) (Math.random() * 100);

        if(rand<50){
            int r = (int) (Math.random() *(maxArray-1));
            System.out.println("Picking a card " + r);
            myCards[cardsPicked]=array[r];
            System.out.println("Storing Card");
            try{
                for (int x = r; x<maxArray-1;x++){
                    array[x]=array[x+1];
                    array[x+1]=(byte)0xFF;
                }
            }
            catch (Exception e){
                System.out.println("Array out of bound -->" + e);
            }

            if(r==maxArray-1){
                array[r] = (byte) 0xFF;
            }
            System.out.println("Incrementing cards picked");
            cardsPicked= cardsPicked + 1;
            System.out.println("Cards picked---------+++++++++++++++++-----" + cardsPicked);
            return array;
        }
        else{
            System.out.println("Cards picked--------------" + cardsPicked);
            if(cardsPicked>0){
                rand = (double) (Math.random() * 100);
                if(rand<50){
                    for(int i = 0;i<cardsPicked;i++){
                        byte card;
                        //pick number to substitute this card
                        int r = (int) (Math.random() *maxArray);
                        //Swap the cards from my cards to array
                        card = array[r];
                        array[r]=myCards[i];
                        myCards[i] = card;
                    }
                }
                else{
                    array = shuffleArrayMaxSize(array,maxArray);
                }

            }
            else{
                System.out.println("------------- ARRAY.LENGTH ------------- " + array.length);
                System.out.println("------------- MAX.ARRAY ------------- " + maxArray);
                array = shuffleArrayMaxSize(array,maxArray);

            }
        }

        return array;
    }

    public static int indexOf(byte[] arr, byte val) {
        return IntStream.range(0, arr.length).filter(i -> arr[i] == val).findFirst().orElse(-1);
    }

    public static byte[] shuffleArrayMaxSize(byte[] array,int max)
    {
        int index;
        byte temp;
        Random random = new Random();

        for (int i = max - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        return array;
    }

    public static byte[] shuffleArray(byte[] array)
    {
        int index;
        byte temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        return array;
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
}
