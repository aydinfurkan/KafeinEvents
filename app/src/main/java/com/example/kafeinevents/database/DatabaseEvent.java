package com.example.kafeinevents.database;

import androidx.annotation.NonNull;

import com.example.kafeinevents.data.model.EventModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseEvent {

    private DatabaseReference mDatabase;

    public DatabaseEvent(){
         mDatabase = FirebaseDatabase.getInstance().getReference("events");
    }

    public interface OngetEvents{
        void successful(List<EventModel> eventModelList);
        void cancelled(String exception);
    }

    public void readEvents(final OngetEvents ongetEvents){

        // Events Listener
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final List<EventModel> eventModelList = new ArrayList<>();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    eventModelList.add(postSnapshot.getValue(EventModel.class));
                }

                ongetEvents.successful(eventModelList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ongetEvents.cancelled("Failed to read events.");
            }
        });

    }

    public interface OngetEvent{
        void successful(EventModel eventModel);
        void cancelled(String exception);
    }

    public void readEvent(String eventId, final OngetEvent ongetEvent){

        // Event Listener
        mDatabase.child(eventId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EventModel eventModel = dataSnapshot.getValue(EventModel.class);
                if(eventModel != null)
                    ongetEvent.successful(eventModel);
                else
                    ongetEvent.cancelled("Failed to read event.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ongetEvent.cancelled("Failed to read event.");
            }
        });
    }

    public void setEvent(String eventId, EventModel eventModel){
        mDatabase.child(eventId).setValue(eventModel);
    }

    public String createEventId() throws Exception {
        String eventId = mDatabase.child("events").push().getKey();
        if(eventId == null)throw new Exception("Failed to create event.");
        return eventId;
    }

    public void removeEvent(String eventId, EventModel eventModel){

        for(String attendee : eventModel.getAttendeesId()){
            new DatabaseUser().setParticipated(attendee, eventModel.getEventId(), false);
        }
        new DatabaseUser().setCreated(eventModel.getCreatorId(), eventModel.getEventId(), false);

        new StorageEvent().deleteImage(eventId);
        mDatabase.child(eventId).removeValue();
    }


}
