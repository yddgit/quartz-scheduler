package org.quartz.hello;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzTest {

    public static void main( String[] args ) {

        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();
            // define the job and tie it to our HelloJob class
            JobDetail job = JobBuilder.newJob( HelloJob.class ).withIdentity( "job1", "group1" ).build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity( "trigger1", "group1" ).startNow()
                    .withSchedule( SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds( 5 ).repeatForever() )
                    .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob( job, trigger );

            Thread.sleep( 60000 );

            scheduler.shutdown();

        } catch ( SchedulerException se ) {
            se.printStackTrace();
        } catch ( InterruptedException ie ) {
            ie.printStackTrace();
        }
    }
}
