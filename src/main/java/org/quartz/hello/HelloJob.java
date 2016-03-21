package org.quartz.hello;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {

    private static int    count  = 0;
    private static Logger logger = Logger.getLogger( HelloJob.class );

    public void execute( JobExecutionContext context ) throws JobExecutionException {
        logger.info( "Hello Job: " + count++ );
    }

}
