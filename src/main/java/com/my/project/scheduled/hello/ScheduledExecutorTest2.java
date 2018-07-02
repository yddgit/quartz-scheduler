package com.my.project.scheduled.hello;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用 ScheduledExecutor 和 Calendar 实现复杂任务调度
 * 
 * <pre>
 * 本类实现了每星期二 16:38:10 调度任务的功能。
 * 
 * 其核心在于根据当前时间推算出最近一个星期二 16:38:10 的绝对时间，然后计算与当前时间的时间差，
 * 作为调用 ScheduledExceutor 函数的参数。计算最近时间要用到 java.util.calendar 的功能。首先
 * 需要解释 calendar 的一些设计思想。Calendar 有以下几种唯一标识一个日期的组合方式：
 * 
 *   YEAR + MONTH + DAY_OF_MONTH
 *   YEAR + MONTH + WEEK_OF_MONTH + DAY_OF_WEEK
 *   YEAR + MONTH + DAY_OF_WEEK_IN_MONTH + DAY_OF_WEEK
 *   YEAR + DAY_OF_YEAR
 *   YEAR + DAY_OF_WEEK + WEEK_OF_YEAR
 * 
 * 上述组合分别加上 HOUR_OF_DAY + MINUTE + SECOND 即为一个完整的时间标识。本例采用了最后一种
 * 组合方式。输入为 DAY_OF_WEEK, HOUR_OF_DAY, MINUTE, SECOND 以及当前日期 , 输出为一个满足
 * DAY_OF_WEEK, HOUR_OF_DAY, MINUTE, SECOND 并且距离当前日期最近的未来日期。计算的原则是从
 * 输入的 DAY_OF_WEEK 开始比较，如果小于当前日期的 DAY_OF_WEEK，则需要向 WEEK_OF_YEAR 进一， 
 * 即将当前日期中的 WEEK_OF_YEAR 加一并覆盖旧值；如果等于当前的 DAY_OF_WEEK, 则继续比较
 * HOUR_OF_DAY；如果大于当前的 DAY_OF_WEEK，则直接调用 java.util.calenda 的 calendar.set(field, value)
 * 函数将当前日期的 DAY_OF_WEEK, HOUR_OF_DAY, MINUTE, SECOND 赋值为输入值，依次类推，直到比较
 * 至 SECOND。读者可以根据输入需求选择不同的组合方式来计算最近执行时间。
 * </pre>
 * 
 * @author yang.dongdong
 *
 */
public class ScheduledExecutorTest2 extends TimerTask {

    private String jobName = "";

    public ScheduledExecutorTest2( String jobName ) {
        super();
        this.jobName = jobName;
    }

    @Override
    public void run() {
        Thread t = Thread.currentThread();
        System.out.println( String.format( "[%s, %s, %s, %4$tF %4$tT] execute %5$s", t.getName(), t.getPriority(), t
                .getThreadGroup().getName(), new Date(), jobName ) );
    }

    /**
     * 计算从当前时间currentDate开始，满足条件dayOfWeek, hourOfDay, minuteOfHour, secondOfMinite的最近时间
     * 
     * @return
     */
    public Calendar getEarliestDate( Calendar currentDate, int dayOfWeek, int hourOfDay, int minuteOfHour,
            int secondOfMinite ) {
        // 计算当前时间的WEEK_OF_YEAR,DAY_OF_WEEK, HOUR_OF_DAY, MINUTE,SECOND等各个字段值
        int currentWeekOfYear = currentDate.get( Calendar.WEEK_OF_YEAR );
        int currentDayOfWeek = currentDate.get( Calendar.DAY_OF_WEEK );
        int currentHour = currentDate.get( Calendar.HOUR_OF_DAY );
        int currentMinute = currentDate.get( Calendar.MINUTE );
        int currentSecond = currentDate.get( Calendar.SECOND );

        // 如果输入条件中的dayOfWeek小于当前日期的dayOfWeek,则WEEK_OF_YEAR需要推迟一周
        boolean weekLater = false;
        if ( dayOfWeek < currentDayOfWeek ) {
            weekLater = true;
        } else if ( dayOfWeek == currentDayOfWeek ) {
            // 当输入条件与当前日期的dayOfWeek相等时，如果输入条件中的
            // hourOfDay小于当前日期的
            // currentHour，则WEEK_OF_YEAR需要推迟一周
            if ( hourOfDay < currentHour ) {
                weekLater = true;
            } else if ( hourOfDay == currentHour ) {
                // 当输入条件与当前日期的dayOfWeek, hourOfDay相等时，
                // 如果输入条件中的minuteOfHour小于当前日期的
                // currentMinute，则WEEK_OF_YEAR需要推迟一周
                if ( minuteOfHour < currentMinute ) {
                    weekLater = true;
                } else if ( minuteOfHour == currentSecond ) {
                    // 当输入条件与当前日期的dayOfWeek, hourOfDay，
                    // minuteOfHour相等时，如果输入条件中的
                    // secondOfMinite小于当前日期的currentSecond，
                    // 则WEEK_OF_YEAR需要推迟一周
                    if ( secondOfMinite < currentSecond ) {
                        weekLater = true;
                    }
                }
            }
        }
        if ( weekLater ) {
            // 设置当前日期中的WEEK_OF_YEAR为当前周推迟一周
            currentDate.set( Calendar.WEEK_OF_YEAR, currentWeekOfYear + 1 );
        }
        // 设置当前日期中的DAY_OF_WEEK,HOUR_OF_DAY,MINUTE,SECOND为输入条件中的值。
        currentDate.set( Calendar.DAY_OF_WEEK, dayOfWeek );
        currentDate.set( Calendar.HOUR_OF_DAY, hourOfDay );
        currentDate.set( Calendar.MINUTE, minuteOfHour );
        currentDate.set( Calendar.SECOND, secondOfMinite );
        return currentDate;

    }

    public static void main( String[] args ) throws Exception {

        ScheduledExecutorTest2 test = new ScheduledExecutorTest2( "job1" );
        // 获取当前时间
        Calendar currentDate = Calendar.getInstance();
        long currentDateLong = currentDate.getTime().getTime();
        System.out.println( "Current Date = " + currentDate.getTime().toString() );
        // 计算满足条件的最近一次执行时间
        Calendar earliestDate = test.getEarliestDate( currentDate, 3, 16, 38, 10 );
        long earliestDateLong = earliestDate.getTime().getTime();
        System.out.println( "Earliest Date = " + earliestDate.getTime().toString() );
        // 计算从当前时间到最近一次执行时间的时间间隔
        long delay = earliestDateLong - currentDateLong;
        // 计算执行周期为一星期
        long period = 7 * 24 * 60 * 60 * 1000;
        ScheduledExecutorService service = Executors.newScheduledThreadPool( 10 );
        // 从现在开始delay毫秒之后，每隔一星期执行一次job1
        service.scheduleAtFixedRate( test, delay, period, TimeUnit.MILLISECONDS );

    }
}
