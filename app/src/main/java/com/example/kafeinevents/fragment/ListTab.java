package com.example.kafeinevents.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.Event;
import com.example.kafeinevents.data.NonScrollListView;
import com.example.kafeinevents.data.adapter.EventAdapter;
import com.example.kafeinevents.data.model.MyCalendar;
import com.example.kafeinevents.ui.DetailActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListTab extends Fragment {

    private View v;
    private String userId;
    private LinearLayout linearLayout;
    private List<Event> events;

    public ListTab(List<Event> events, String userId){
        this.events = events;
        this.userId = userId;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        boolean isRight = true;

        v = inflater.inflate(R.layout.fragment_list_tab, container, false);
        linearLayout = v.findViewById(R.id.list_tabLinear);

        Collections.sort(events);
        List<List<Event>> eventLists;

        eventLists = splitEvents();

        for(List<Event> eventList : eventLists){
            isRight ^= true;
            createDate(eventList.get(0).getEventModel().getEventDate(), isRight);
            createList(eventList);
        }

        return v;
    }

    private List<List<Event>> splitEvents() {

        List<List<Event>> eventLists = new ArrayList<>();
        List<Event> tempEvents = new ArrayList<>();
        String pastDate = "";

        for (Event event : events){

            String currentDate = event.getEventModel().getEventDate();

            if(!currentDate.equals(pastDate) && !pastDate.equals("")){
                eventLists.add(new ArrayList<>(tempEvents));
                tempEvents.clear();
            }

            tempEvents.add(event);

            pastDate = currentDate;
        }

        if(!tempEvents.isEmpty())
            eventLists.add(new ArrayList<>(tempEvents));

        return eventLists;

    }

    private void createList(List<Event> tempEvents) {

        NonScrollListView listView = new NonScrollListView(v.getContext());
        EventAdapter adapter = new EventAdapter(getActivity(), tempEvents);
        listView.setAdapter(adapter);
        initializeListView(listView, tempEvents);

        listView.setDivider(new ColorDrawable(getResources().getColor(R.color.colorTransparent)));
        listView.setDividerHeight(30);
        listView.setPadding(30,10,30,10);

        linearLayout.addView(listView);



    }

    private void createDate(String currentEventDate, boolean isRight) {

        SpannableString eventDate = changeDateFormat(currentEventDate);

        TextView textView = new TextView(v.getContext());
        textView.setText(eventDate);
        textView.setTextSize(25);
        textView.setTextColor(getResources().getColor(R.color.colorDate));

        if(isRight)
            textView.setGravity(Gravity.END);

        linearLayout.addView(textView);


    }

    private SpannableString changeDateFormat(String currentEventDate) {


        MyCalendar myCalendar = new MyCalendar(currentEventDate, null);

        SpannableString content = new SpannableString(myCalendar.getLongDateToString());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        return content;

    }

    private void initializeListView(ListView listView, final List<Event> tempEvents) {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                intent.putExtra("eventId", tempEvents.get(i).getEventModel().getEventId());
                intent.putExtra("userId", userId);

                startActivity(intent);

            }
        });

    }


}
