package com.security;


import java.util.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedTableList {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private ArrayList<Table> tableList = new ArrayList<>();

    public SharedTableList(){
    }

    public boolean add(Table val){
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

    public Table get(int i){

        try {
            readLock.lock();
            return tableList.get(i);
        }
        finally {
            readLock.unlock();
        }


    }

    public boolean remove(Table t){
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

