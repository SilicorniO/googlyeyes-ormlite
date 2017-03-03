package com.silicornio.geormlite.utils;

import android.util.Pair;

import com.silicornio.geormlite.general.GEL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GEDateUtils {

    /** Complete format, contains most of information yyyy-MM-dd HH:mm:ss.SSS **/
    public static final String DATE_FORMAT_COMPLETE = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final long MS_SECOND = 1000;
    public static final long MS_MINUTE = MS_SECOND * 60;
    public static final long MS_HOUR = MS_MINUTE * 60;
    public static final long MS_DAY = MS_HOUR * 24;
    public static final long MS_WEEK = MS_DAY * 7;

    /**
     * Format calendar to string
     * @param c Calendar to convert
     * @param format String format of SimpleDateFormat
     * @return String converted
     */
    public static String formatDate(Calendar c, String format){
        if(c==null) {
            c = Calendar.getInstance();
            c.setTimeInMillis(0);
            c.set(Calendar.YEAR, 1990);
        }

        return formatDate(c.getTime(), format);
    }

    /**
     * Format calendar to string
     * @param date Date to convert
     * @param format String format of SimpleDateFormat
     * @return String converted
     */
    public static String formatDate(Date date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if(date==null){
            date = new Date(0);
        }
        return sdf.format(date);
    }

    /**
     * Date string to calendar
     * @param sDate String with date
     * @param format String format of SimpleDateFormat
     * @return Calendar read
     */
    public static Date formatDateString(String sDate, String format){

        //check if date is null
        if(sDate==null){
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(sDate);
        }catch(Exception e){
            GEL.e("Exception reading date '" + sDate + "' with format '" + format + "': " + e.toString());
        }
        return new Date(0);
    }

    /**
     * Get Calendar from Date
     * @param date Date
     * @return Calendar generated
     */
    public static Calendar generateCalendar(Date date){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.getTime());
        return c;
    }

    /**
     * Get Calendar from Date
     * @param cal Calendar
     * @return Calendar generated
     */
    public static Calendar generateCalendar(Calendar cal){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(cal.getTimeInMillis());
        return c;
    }

    /**
     * Get Date from Calendar
     * @param cal Calendar
     * @return Date generated
     */
    public static Date generateDate(Calendar cal){
        return new Date(cal.getTimeInMillis());
    }

    /**
     * Get Date from Date
     * @param date Date
     * @return Date generated
     */
    public static Date generateDate(Date date){
        return new Date(date.getTime());
    }

    /**
     * Get a list of initial and end calendar of months in the range received
     * @param cStart Calendar date to start
     * @param cEnd Calendar date to end
     * @return List<Pair<Calendar, Calendar>> list of calendars (initial and end)
     */
    public static List<Pair<Calendar, Calendar>> getRangeInMonths(Calendar cStart, Calendar cEnd){

        //generate the list
        List<Pair<Calendar, Calendar>> calendars = new ArrayList<>();

        //from the first calendar start adding a month until the actual calendar is after the end
        Calendar cActual = generateCalendar(cStart);
        cActual.set(Calendar.DAY_OF_MONTH, 1);
        Calendar c0;
        Calendar cF;

        while(cActual.compareTo(cEnd)<0){

            //calendar start
            if(calendars.size()==0) {
                c0 = generateCalendar(cStart);
            }else{
                c0 = generateCalendar(cActual);
            }

            //increment a month
            cActual.add(Calendar.MONTH, 1);

            //calendar end
            if(cActual.after(cEnd)){
                cF = generateCalendar(cEnd);
            }else{
                cF = generateCalendar(cActual);

                //remove 1 day to set the last day of the month
                cF.add(Calendar.DAY_OF_YEAR, -1);
            }

            //add the pair to the list
            calendars.add(new Pair<Calendar, Calendar>(c0, cF));
        }

        //return the list
        return calendars;
    }

    /**
     * Get the number of days between calendars
     * @param cIni Calendar initial
     * @param cEnd Calendar end
     * @return int num of days
     */
    public static int getNumDays(Calendar cIni, Calendar cEnd){
        long difMillis = cEnd.getTimeInMillis() - cIni.getTimeInMillis();
        return (int)(difMillis / 86400000);
    }

    /**
     * Get a Calendar with all values to zero
     * @return Calendar generated
     */
    public static Calendar getCalendar0(){
        Calendar c = Calendar.getInstance();
        c.setTime(formatDateString("0000-00-00 00:00:00.000", "yyyy-MM-dd HH:mm:ss.SSS"));
        return c;
    }

    /**
     * Get a Date with all values to zero (new Date(0))
     * @return Date date generated
     */
    public static Date getDate0(){
        return new Date(0);
    }

    /**
     * Clean hours, minutes, seconds and milliseconds
     * @param c Calendar to clean
     */
    public static void cleanDay(Calendar c){
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Convert the day in calendar in the end of the day: 23:59:59.999
     * @param c Calendar to change
     */
    public static void endOfDay(Calendar c){

        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
    }

    /**
     * Generate a calendar adding 1 day
     * @param c Calendar generated
     */
    public static Calendar generateAdding1Day(Calendar c){
        Calendar cal = GEDateUtils.generateCalendar(c);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        return cal;
    }

    /**
     * Generate a calendar adding 1 day
     * @param c Calendar generated
     */
    public static Calendar generateAdding1Month(Calendar c){
        Calendar cal = GEDateUtils.generateCalendar(c);
        cal.add(Calendar.MONTH, 1);
        return cal;
    }

    /**
     * Calculate the number of weeks between two dates
     * @param cIni Calendar starts
     * @param cEnd Calendar ends
     * @return int number of weeks
     */
    public static int getWeeksBetweenDates(Calendar cIni, Calendar cEnd){
        return (int)((cEnd.getTimeInMillis() - cIni.getTimeInMillis())/MS_WEEK) + 1;
    }

    /**
     * Set the last day of the month
     * @param c Calendar
     */
    public static void setLastDayMonth(Calendar c){
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.DAY_OF_YEAR, -1);
    }

    /**
     * Copy the year, month and day from a date to another
     * @param dateOrigin Date origin where to get the data
     * @param dateDestiny Date destiny where to set the data
     * @return Date with year month and day from dateOrigin and rest from dateDestiny
     */
    public static Date copyYearMonthDay(Date dateOrigin, Date dateDestiny){

        //check null values
        if(dateOrigin==null && dateDestiny==null){
            return Calendar.getInstance().getTime();
        }else if(dateOrigin==null){
            return dateDestiny;
        }else if(dateDestiny==null){
            return dateOrigin;
        }

        //convert to calendars
        Calendar calOrigin = Calendar.getInstance();
        calOrigin.setTime(dateOrigin);
        Calendar calDestiny = Calendar.getInstance();
        calDestiny.setTime(dateDestiny);

        //return the time of destiny
        return copyYearMonthDay(calOrigin, calDestiny).getTime();
    }

    /**
     * Copy the year, month and day from a date to another
     * @param cOrigin Calendar origin where to get the data
     * @param cDestiny Calendar destiny where to set the data
     * @return Date with year month and day from dateOrigin and rest from dateDestiny
     */
    public static Calendar copyYearMonthDay(Calendar cOrigin, Calendar cDestiny) {

        //check null values
        if (cOrigin == null && cDestiny == null) {
            return Calendar.getInstance();
        } else if (cOrigin == null) {
            return cDestiny;
        } else if (cDestiny == null) {
            return cOrigin;
        }

        //copy year, month and day
        cDestiny.set(Calendar.YEAR, cOrigin.get(Calendar.YEAR));
        cDestiny.set(Calendar.MONTH, cOrigin.get(Calendar.MONTH));
        cDestiny.set(Calendar.DAY_OF_MONTH, cOrigin.get(Calendar.DAY_OF_MONTH));

        //return the time of destiny
        return cDestiny;
    }

    /**
     * Copy the hour and minutes from a date to another
     * @param dateOrigin Date origin where to get the data
     * @param dateDestiny Date destiny where to set the data
     * @return Date with hour and minutes from dateOrigin and rest from dateDestiny
     */
    public static Date copyHourMinute(Date dateOrigin, Date dateDestiny){

        //check null values
        if(dateOrigin==null && dateDestiny==null){
            return Calendar.getInstance().getTime();
        }else if(dateOrigin==null){
            return dateDestiny;
        }else if(dateDestiny==null){
            return dateOrigin;
        }

        //convert to calendars
        Calendar calOrigin = Calendar.getInstance();
        calOrigin.setTime(dateOrigin);
        Calendar calDestiny = Calendar.getInstance();
        calDestiny.setTime(dateDestiny);

        //return the time of destiny
        return copyHourMinute(calOrigin, calDestiny).getTime();
    }

    /**
     * Copy the hour and minutes from a date to another
     * @param cOrigin Calendar origin where to get the data
     * @param cDestiny Calendar destiny where to set the data
     * @return Calendar with hour and minutes from dateOrigin and rest from dateDestiny
     */
    public static Calendar copyHourMinute(Calendar cOrigin, Calendar cDestiny){

        //check null values
        if (cOrigin == null && cDestiny == null) {
            return Calendar.getInstance();
        } else if (cOrigin == null) {
            return cDestiny;
        } else if (cDestiny == null) {
            return cOrigin;
        }

        //copy year, month and day
        cDestiny.set(Calendar.HOUR_OF_DAY, cOrigin.get(Calendar.HOUR_OF_DAY));
        cDestiny.set(Calendar.MINUTE, cOrigin.get(Calendar.MINUTE));

        //return the time of destiny
        return cDestiny;
    }


}
