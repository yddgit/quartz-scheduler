package com.my.project.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

import org.quartz.Job;
import org.quartz.Trigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 一个简单的Quartz的示例
 * @author yang
 */
public class QuartzSample {

	public static void main(String[] args) throws InterruptedException {
        try {
            // Grab the Scheduler instance from the Factory
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            // define the job and tie it to our HelloJob class
            JobDetail job = newJob(HelloJob.class)
                .withIdentity("job1", "group1")
                .build();

            // Trigger the job to run now, and then repeat every 40 seconds
            Trigger trigger = newTrigger()
                .withIdentity("trigger1", "group1")
                .startNow()
                    .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(5)
                        .repeatForever())            
                .build();

            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(job, trigger);

            // allow some time for the job to be triggered and executed
            Thread.sleep(10000);

            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

	/**
	 * 实现具体job执行逻辑
	 * @author yang
	 */
	public static class HelloJob implements Job {
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			System.out.println("HelloJob is executed.");
		}
	}
}
