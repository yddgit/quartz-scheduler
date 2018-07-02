package com.my.project;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class PausableThreadPoolExecutor extends ThreadPoolExecutor {

    private boolean       isPaused;
    private ReentrantLock pauseLock = new ReentrantLock();
    private Condition     unpaused  = pauseLock.newCondition();

    public PausableThreadPoolExecutor() {
        super( 10, 2, 3, TimeUnit.MILLISECONDS, new LinkedBlockingQueue < Runnable >() );
    }

    protected void beforeExecute( Thread t, Runnable r ) {
        super.beforeExecute( t, r );
        pauseLock.lock();
        try {
            while ( isPaused )
                unpaused.await();
        } catch ( InterruptedException ie ) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }
}
