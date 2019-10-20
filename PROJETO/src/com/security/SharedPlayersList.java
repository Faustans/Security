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

    public SharedPlayersList(){
    }

    public boolean add(Client val){
        try{
            writeLock.lock();
            try {
                this.tableList.add(val);
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

    public Client get(){

        try {
            readLock.lock();
            return tableList.get(0);
        }
        finally {
            readLock.unlock();
        }


    }

    public boolean remove(Client t){
        try{
            writeLock.lock();
            try {
                boolean out =  this.tableList.remove(t);
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
            return this.tableList.size();
        }
        finally
        {
            readLock.unlock();
        }
    }
}

