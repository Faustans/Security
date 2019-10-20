package com.security;
import java.sql.Time;
import java.util.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedQueue extends AbstractQueue {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    private int arrSize = 10;
    private String[] array = new String[arrSize];

    private Queue<String> queue = new LinkedList<>();

    public SharedQueue(){
    }

    public boolean add(String val){
        try{
            writeLock.lock();
            try {
                this.queue.add(val);
                System.out.println(Thread.currentThread().getName() + " Added: " + val);
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

    public String peek(){
        try{
            readLock.lock();
            return this.queue.peek();
        }
        finally {
            readLock.unlock();
        }


    }
    public String remove(){
        try{
            writeLock.lock();
            try {
                String val = "null";
                val = this.queue.remove();
                System.out.println("Consumer: " + Thread.currentThread().getName() + " consumed: " + val);
                return val;
            }
            finally
            {
                writeLock.unlock();
            }
        }
        catch (Exception E){
        }

        return "null";
    }

    public int size(){
        try{
            readLock.lock();
            try {
                return this.queue.size();
            }
            finally
            {
                readLock.unlock();
            }
        }
        catch (Exception E){
        }
        return 0;
    }


}
