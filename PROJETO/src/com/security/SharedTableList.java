package com.security;


import java.util.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedTableList {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private Map<String,Table> tableList = new HashMap<>();

    public SharedTableList(){
    }

    public boolean add(Table val){
        try{
            writeLock.lock();
            try {
                this.tableList.put(val.getName(), val);
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

    public void replace(String name, Table val){
        try{
            writeLock.lock();
            tableList.replace(name, val);
        }
        finally {
            writeLock.unlock();
        }
    }

    public Table get(String name){

        try {
            readLock.lock();
            return tableList.get(name);
        }
        finally {
            readLock.unlock();
        }


    }
    public Collection<Table> getAll(){
        return tableList.values();
    }


    public boolean remove(String name){
        try{
            writeLock.lock();
            try {
                //boolean out =  this.tableList.remove(t);
                boolean out =  this.tableList.containsKey(name);
                this.tableList.remove(name);
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

