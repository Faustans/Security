package com.security;

import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static int i = 0;
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(7000);
            while (true) {
                SharedQueue queue = new SharedQueue();
                wait_for_clients(server, queue);
            }
        } catch (Exception ex) {
            System.err.println("Error : " + ex.getMessage());
        }

    }



    public static void wait_for_clients(ServerSocket server, SharedQueue queue) throws Exception{
        Thread t;
        Socket p = server.accept();
        String name = "Client ";
        Client c = new Client(p, name+i, ClientState.AUTHENTICATION);
        ThreadServer.ServerThread tServer = new ThreadServer.ServerThread(p, name+i, c, queue);
        t = new Thread(tServer);
        t.setName(name+i);
        c.setThread(t);
        i++;
        t.start();


    }
}
