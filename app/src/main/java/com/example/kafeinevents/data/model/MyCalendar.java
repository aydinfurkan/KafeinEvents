package com.example.kafeinevents.data.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyCalendar{

    private DateFormat dateFormat;
    private Calendar Calendar;
    private Date date;

    public MyCalendar(String eventDate, String eventClock){

        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.US);

        Calendar = Calendar.getInstance();
        if(eventClock == null){
            eventClock = "12:00";
        }

        if(eventDate == null){
            eventDate = "29/12/1997";
        }

        try {
            date = dateFormat.parse(eventDate + " " + eventClock);
            Calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Date getDate(){
        return date;
    }

    public String getLongDateToString(){
        return new SimpleDateFormat("dd MMMM yyyy",Locale.US).format(Calendar.getTime());
    }

    public String getDateToString(){
        return new SimpleDateFormat("dd/MM/yyyy",Locale.US).format(Calendar.getTime());
    }
    public String getClockToString(){
        return new SimpleDateFormat("HH:mm",Locale.US).format(Calendar.getTime());
    }

    public int getYear(){
        return Calendar.get(Calendar.YEAR);
    }
    public int getMonth(){
        return Calendar.get(Calendar.MONTH);
    }
    public int getDay(){
        return Calendar.get(Calendar.DAY_OF_MONTH);
    }
    public int getHour(){
        return Calendar.get(Calendar.HOUR_OF_DAY);
    }
    public int getMinute(){
        return Calendar.get(Calendar.MINUTE);
    }

    public void setYear(int year){
        Calendar.set(Calendar.YEAR, year);
    }
    public void setMonth(int month){
        Calendar.set(Calendar.MONTH, month);
    }
    public void setDay(int day){
        Calendar.set(Calendar.DAY_OF_MONTH, day);
    }
    public void setHour(int hour){
        Calendar.set(Calendar.HOUR_OF_DAY, hour);
    }
    public void setMinute(int minute){
        Calendar.set(Calendar.MINUTE, minute);
    }

}
