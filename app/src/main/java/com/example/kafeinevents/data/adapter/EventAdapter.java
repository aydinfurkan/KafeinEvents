package com.example.kafeinevents.data.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.Event;
import com.example.kafeinevents.data.Transform.RadiusTransform;
import com.example.kafeinevents.data.model.EventModel;
import com.example.kafeinevents.data.model.MyCalendar;
import com.example.kafeinevents.database.StorageEvent;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class EventAdapter extends BaseAdapter {

    private LayoutInflater userInflater;
    private List<Event> events;

    public EventAdapter(Activity activity, List<Event> events){

        userInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.events = events;
        Collections.sort(this.events);

    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Object getItem(int i) {
        return events.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final View v = userInflater.inflate(R.layout.eventlist_item, null);

        TextView title = v.findViewById(R.id.eventlist_itemTitle);
        TextView time = v.findViewById(R.id.eventlist_itemTime);
        TextView attendeesCount = v.findViewById(R.id.eventlist_itemCount);
        final ImageView imageView = v.findViewById(R.id.eventlist_itemImage);

        Event event = events.get(i);
        EventModel eventModel = event.getEventModel();

        String Title = eventModel.getEventTitle();
        int maxLength = v.getResources().getInteger(R.integer.ListTitleMaxLength);
        if(eventModel.getEventTitle().length() > maxLength)
            Title = eventModel.getEventTitle().substring(0,maxLength-3) + "...";
        title.setText(Title);

        attendeesCount.setText(event.getAttendeesCount() + " Katılımcı");

        MyCalendar myCalendar = new MyCalendar(null, eventModel.getEventTime());
        time.setText(myCalendar.getClockToString());

        StorageEvent storageEvent = new StorageEvent();
        storageEvent.readImage(eventModel.getEventId(), new StorageEvent.OngetImage() {
            @Override
            public void successful(File file) {
                imageView.setAlpha(1f);
                Picasso.with(v.getContext()).load(file).transform(new RadiusTransform()).into(imageView);
            }

            @Override
            public void cancelled(String exception) {

            }
        });

        return v;

    }

}
