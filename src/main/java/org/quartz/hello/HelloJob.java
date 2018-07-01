package org.quartz.hello;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {

    private static final Logger logger   = Logger.getLogger( HelloJob.class );
    public static final String  JOB_TYPE = "type";

    /**
     * 该方法实现需要执行的任务
     */
    @Override
    public void execute( JobExecutionContext context ) throws JobExecutionException {
        Thread t = Thread.currentThread();
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        logger.info( String.format( "[%s, %s, %s, %4$tF %4$tT] type=%5$s", t.getName(), t.getPriority(), t
                .getThreadGroup().getName(), new Date(), dataMap.get( JOB_TYPE ) ) );
        throw new JobExecutionException( "Job Executetion Failed." );
    }
}
