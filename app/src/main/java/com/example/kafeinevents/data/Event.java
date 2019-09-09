package com.example.kafeinevents.data;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.example.kafeinevents.data.model.EventModel;
import com.example.kafeinevents.data.model.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Event implements Comparable<Event>{

    private EventModel eventModel;
    private Uri eventImage;

    public Event(EventModel eventModel){
        this.eventModel = eventModel;
    }

    public EventModel getEventModel() { return eventModel; }

    public void setEventModel(EventModel eventModel) { this.eventModel = eventModel; }
    public void setEventImage(Uri eventImage) { this.eventImage = eventImage; }
    public Uri getEventImage() { return eventImage; }

    public boolean addAttendees(String s) {
        List<String> l = eventModel.getAttendeesId();
        if (l.add(s)) {
            eventModel.setAttendeesId(l);
            return true;
        } else
            return false;
    }

    public boolean delAttendees(String s) {
        List<String> l = eventModel.getAttendeesId();
        if (l.remove(s)) {
            eventModel.setAttendeesId(l);
            return true;
        } else
            return false;
    }

    public String getAttendeesCount(){ return Integer.toString(eventModel.getAttendeesId().size()); }

    @Override
    public int compareTo(Event tEvent) {

        MyCalendar myCalendar = new MyCalendar(this.eventModel.getEventDate(), this.eventModel.getEventTime());
        MyCalendar tCalendar = new MyCalendar(tEvent.eventModel.getEventDate(), tEvent.eventModel.getEventTime());

        return myCalendar.getDate().compareTo(tCalendar.getDate());

    }

    public int isPast (){

        try{
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String time = this.eventModel.getEventDate() + " " + this.eventModel.getEventTime();
            Date date = format.parse(time);

            if (new Date().before(date)){
                return 0;
            }
            else {
                return 1;
            }

        }catch (ParseException e){
            e.printStackTrace();
            return -1;
        }

    }

}
