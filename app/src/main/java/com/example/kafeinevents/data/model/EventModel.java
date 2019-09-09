package com.example.kafeinevents.data.model;

import java.util.ArrayList;
import java.util.List;

public class EventModel {

    private String eventId;
    private String eventTitle;
    private String eventContent;
    private String eventDate;
    private String eventTime;
    private String creatorId;
    private List<String> attendeesId = new ArrayList<String>();

    public EventModel(){}

    public EventModel(String eventId, String eventTitle, String eventContent, String eventDate, String eventTime, String creatorId){
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventContent = eventContent;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.creatorId = creatorId;
        this.attendeesId.add(creatorId);
    }

    public EventModel(String eventId, String eventTitle, String eventContent, String eventDate, String eventTime, String creator, List<String> attendeesId){
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventContent = eventContent;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.creatorId = creator;
        this.attendeesId = attendeesId;
    }

    public String getEventId(){ return eventId; }
    public String getEventTitle(){ return eventTitle; }
    public String getEventContent(){ return eventContent; }
    public String getEventDate(){ return eventDate; }
    public String getEventTime(){ return eventTime; }
    public String getCreatorId(){ return creatorId; }
    public List<String> getAttendeesId(){ return attendeesId; }

    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public void setEventContent(String eventContent) { this.eventContent = eventContent; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setAttendeesId(List<String> attendeesId) { this.attendeesId = attendeesId; }
}
