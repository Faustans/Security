package com.security;


import java.util.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedPlayersList{

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private ArrayList<Client> tableList = new ArrayList<>();

    private Map<String,Client> playList = new HashMap<>();

    public SharedPlayersList(){
    }

    public boolean add(Client val){
        try{
            writeLock.lock();
            try {
                //this.tableList.add(val);
                //adicionar ao hashmap
                this.playList.put(val.getName(), val);
                return true;
            }
            finally
            {
                writeLock.unlock();
            }
        }
        catch (Exception E){
        }
        return false;
    }

    public int index(String name){
        /*for(int i = 0; i<tableList.size(); i++){
            if(tableList.get(i).getName().equals(name)) {
                return i;
            }
        }*/
        return 0;
    }

    //Replace current associated client with updated one
    public void replace(String name, Client val){
        try{
            writeLock.lock();
            playList.replace(name, val);
        }
        finally {
            writeLock.unlock();
        }
    }

    public Collection<Client> getAll(){
        return playList.values();
    }

    //Get is set
    public Client get(String name){

        try {
            readLock.lock();
            return this.playList.get(name);
        }
        finally {
            readLock.unlock();
        }


    }

    //Remove um cliente do mapa
    public boolean remove(String name){
        try{
            writeLock.lock();
            try {
                //boolean out =  this.tableList.remove(t);
                boolean out =  this.playList.containsKey(name);
                this.playList.remove(name);
                return out;
            }
            finally
            {
                writeLock.unlock();
            }
        }
        catch (Exception E){
        }

        return false;
    }

    public int size(){
        readLock.lock();
        try {
            return this.playList.size();
        }
        finally
        {
            readLock.unlock();
        }
    }

}

