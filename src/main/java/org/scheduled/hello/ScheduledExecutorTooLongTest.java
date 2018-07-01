package org.scheduled.hello;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 使用 ScheduledExecutor 进行任务调度
 * 
 * <pre>
 * ScheduledExecutorService 中两种最常用的调度方法 ScheduleAtFixedRate 和 ScheduleWithFixedDelay。
 * 
 * ScheduleAtFixedRate 每次执行时间为上一次任务开始起向后推一个时间间隔，即每次执行时间为 :
 * initialDelay, initialDelay+period, initialDelay+2*period, ...
 * 
 * ScheduleWithFixedDelay 每次执行时间为上一次任务结束起向后推一个时间间隔，即每次执行时间为：
 * initialDelay, initialDelay+executeTime+delay, initialDelay+2*executeTime+2*delay
 * 
 * 由此可见:
 * ScheduleAtFixedRate 是基于固定时间间隔进行任务调度；
 * ScheduleWithFixedDelay 取决于每次任务执行的时间长短，是基于不固定时间间隔进行任务调度。
 * </pre>
 * 
 * @author yang.dongdong
 *
 */
public class ScheduledExecutorTooLongTest implements Runnable {

    private static boolean sleep   = true;

    private String         jobName = "";

    public ScheduledExecutorTooLongTest( String jobName ) {
        super();
        this.jobName = jobName;
    }

    @Override
    public void run() {
        try {
            if ( sleep ) {
                System.out.println( "Sleep 2 seconds..." );
                Thread.sleep( 2000 );
            }
            sleep = false;
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        Thread t = Thread.currentThread();
        System.out.println( String.format( "[%s, %s, %s, %4$tF %4$tT] execute %5$s", t.getName(), t.getPriority(), t
                .getThreadGroup().getName(), new Date(), jobName ) );
    }

    public static void main( String[] args ) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool( 10 );

        long initialDelay1 = 1;
        long period1 = 1;
        // 从现在开始1秒钟之后，每隔1秒钟执行一次job1
        ScheduledFuture < ? > sf1 = service.scheduleAtFixedRate( new ScheduledExecutorTooLongTest( "job1" ),
                initialDelay1, period1, TimeUnit.SECONDS );

        try {
            Thread.sleep( 10000 );
        } catch ( InterruptedException e ) {
            e.printStackTrace();
        }

        if ( !sf1.isCancelled() ) {
            sf1.cancel( false );
        }
        if ( !service.isShutdown() ) {
            service.shutdown();
        }
    }
}
