package com.my.project.quartz;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Matcher;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.my.project.quartz.jobs.HelloJob;

/**
 * Quartz
 * 
 * <pre>
 * Quartz 设计的核心类包括 Scheduler, Job 以及 Trigger。
 * 
 * 其中，Job 负责定义需要执行的任务，Trigger 负责设置调度策略，
 * Scheduler 将二者组装在一起，并触发任务开始执行。
 * 
 * 1. Job
 * 使用者只需要创建一个 Job 的继承类，实现 execute 方法。JobDetail
 * 负责封装 Job 以及 Job 的属性，并将其提供给 Scheduler 作为参数。
 * 每次 Scheduler 执行任务时，首先会创建一个 Job 的实例，然后再调用
 * execute方法执行。Quartz没有为 Job 设计带参数的构造函数，因此需要
 * 通过额外的JobDataMap来存储Job的属性。JobDataMap可以存储任意数量的key-value对
 * 
 * 2. Trigger 的作用是设置调度策略。Quartz 设计了多种类型的 Trigger，
 * 其中最常用的是 SimpleTrigger 和 CronTrigger。
 * 
 * 3. CronTigger表达式
 * Seconds(0-59)
 * Minutes(0-59)
 * Hours(0-23)
 * Day-of-Month(0-31)
 * Month(0-11或JAN，FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC)
 * Day-of-Week(1-7或SUN, MON, TUE, WED, THU, FRI, SAT)
 * Year(Optional field)
 * 
 * 每个字段可以取单个值，多个值，或一个范围，例如 Day-of-Week 可取值为"MON,TUE,SAT", "MON-FRI"或者"TUE-THU, SUN"。
 * 
 * * 表示该字段可接受任何可能取值。例如 Month 字段赋值 * 表示每个月
 * 
 * / 表示开始时刻与间隔时段。例如 Minutes 字段赋值 2/10 表示在一个小时内每 10 分钟执行一次，从第 2 分钟开始。
 * 
 * ? 仅适用于 Day-of-Month 和 Day-of-Week。? 表示对该字段不指定特定值。
 * 适用于需要对这两个字段中的其中一个指定值，而对另一个不指定值的情况。一般情况下，这两个字段只需对一个赋值。
 * 
 * L 仅适用于 Day-of-Month 和 Day-of-Week。L 用于 Day-of-Month 表示该月最后一天。L 单独用于 Day-of-Week
 * 表示周六，否则表示一个月最后一个星期几，例如 5L 或者 THUL 表示该月最后一个星期四。
 * 
 * W 仅适用于 Day-of-Month，表示离指定日期最近的一个工作日，例如 Day-of-Month 赋值为 10W 表示该月离 10 号最近的一个工作日。
 * 
 * # 仅适用于 Day-of-Week，表示该月第 XXX 个星期几。例如 Day-of-Week 赋值为 5#2 或者 THU#2，表示该月第二个星期四。
 * </pre>
 * 
 * @author yang.dongdong
 *
 */
public class QuartzTest {

    private static final Logger logger = Logger.getLogger( QuartzTest.class );

    public static void main( String[] args ) {
        try {
            // 创建一个Scheduler
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // 添加一个JobListener
            scheduler.getListenerManager().addJobListener( new MyListener(), new Matcher < JobKey >() {

                private static final long serialVersionUID = -4010606345084704760L;

                @Override
                public boolean isMatch( JobKey key ) {
                    if ( "myJobCron".equals( key.getName() ) && "myJobCronGroup".equals( key.getGroup() ) ) {
                        return true;
                    }
                    return false;
                }
            } );
            scheduler.start();

            // 创建JobDetail，指明name，groupname，以及具体的Job类名，
            // 该Job负责定义需要执行任务
            JobDetail jobDetail = JobBuilder.newJob( HelloJob.class ).withIdentity( "myJob", "myJobGroup" ).build();
            jobDetail.getJobDataMap().put( HelloJob.JOB_TYPE, "Simple" );
            JobDetail jobDetailCron = JobBuilder.newJob( HelloJob.class ).withIdentity( "myJobCron", "myJobCronGroup" )
                    .build();
            jobDetailCron.getJobDataMap().put( HelloJob.JOB_TYPE, "Crontab" );

            // 创建每周触发的Trigger
            Trigger trigger = TriggerBuilder.newTrigger()
                    // 指明trigger的name和group
                    .withIdentity( "myTrigger", "myTriggerGroup" )
                    // 从当前时间的下1秒开始执行
                    .startAt( DateBuilder.futureDate( 1, IntervalUnit.SECOND ) )
                    .withSchedule(
                            DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule()
                                    // 指明星期二16：38：10执行
                                    .onDaysOfTheWeek( Calendar.TUESDAY )
                                    .startingDailyAt( TimeOfDay.hourMinuteAndSecondOfDay( 16, 38, 10 ) ) ).build();
            Trigger triggerCron = TriggerBuilder.newTrigger().withIdentity( "cronTrigger", "cronTriggerGroup" )
                    .startAt( DateBuilder.futureDate( 1, IntervalUnit.SECOND ) )
                    .withSchedule( CronScheduleBuilder.cronSchedule( "10 38 16 ? * TUE" ) ).build();

            // 用scheduler将JobDetail与Trigger关联在一起，开始调度任务
            scheduler.scheduleJob( jobDetail, trigger );
            scheduler.scheduleJob( jobDetailCron, triggerCron );
        } catch ( Exception e ) {
            logger.error( "Application Error!", e );
        }
    }

    static class MyListener implements JobListener {

        @Override
        public String getName() {
            return "My Listener";
        }

        @Override
        public void jobToBeExecuted( JobExecutionContext context ) {
            logger.info( "jobToBeExecuted()" );
        }

        @Override
        public void jobExecutionVetoed( JobExecutionContext context ) {
            logger.info( "jobExecutionVetoed()" );
        }

        @Override
        public void jobWasExecuted( JobExecutionContext context, JobExecutionException jobException ) {
            if ( jobException != null ) {
                try {
                    // 停止Scheduler
                    context.getScheduler().shutdown();
                    // TODO 给管理员发送邮件
                } catch ( SchedulerException e ) {
                    logger.error( "Error occurs when executing jobs, shut down the scheduler.", e );
                }
            }
        }
    }
}
