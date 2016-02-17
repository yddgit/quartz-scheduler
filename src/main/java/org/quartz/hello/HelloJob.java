package org.quartz.hello;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob implements Job {

    private static int count = 0;

    public void execute( JobExecutionContext context ) throws JobExecutionException {
        System.out.println( "Hello Job: " + count++ );
    }

}
