package com.example.kafeinevents.ui;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.Event;
import com.example.kafeinevents.data.adapter.PagerAdapter;
import com.example.kafeinevents.data.model.EventModel;
import com.example.kafeinevents.database.DatabaseEvent;
import com.example.kafeinevents.database.DatabaseUser;
import com.example.kafeinevents.fragment.ListTab;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private List<Event> currentAEvents = new ArrayList<>();
    private List<Event> pastAEvents = new ArrayList<>();
    private List<Event> currentPEvents = new ArrayList<>();
    private List<Event> pastPEvents = new ArrayList<>();

    private ViewPager viewPager;
    private ImageButton newButton;

    private PagerAdapter adapter;
    private ListTab listTab1;
    private ListTab listTab2;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        viewPager = findViewById(R.id.listViewPager);
        newButton = findViewById(R.id.listNewButton);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initializeViewPager();
        initializeActionBar();
        initializeNewButton();
        initializeAEvents();
    }

    private void initializeViewPager() {

        TabLayout tabLayout = findViewById(R.id.listTab);

        adapter = new PagerAdapter(getSupportFragmentManager());

        tabLayout.setupWithViewPager(viewPager);
        listTab1 = new ListTab(currentAEvents, userId);
        adapter.addFrag(listTab1, "");

        listTab2 = new ListTab(currentPEvents, userId);
        adapter.addFrag(listTab2, "");

    }

    private void initializeActionBar() {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.actionbar_list);
        View actionView = getSupportActionBar().getCustomView();

        // Switch
        Switch actionSwitch = actionView.findViewById(R.id.actionbar_listSwitch);

        ColorDrawable[] colors = {new ColorDrawable(getResources().getColor(R.color.colorCurrentListBackground)),
                new ColorDrawable(getResources().getColor(R.color.colorPastListBackground))};

        final TransitionDrawable trans = new TransitionDrawable(colors);
        viewPager.setBackground(trans);

        actionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    trans.startTransition(1000);

                    listTab1.setEvents(pastAEvents);
                    listTab2.setEvents(pastPEvents);
                    adapter.setFragTitleList(0, "Geçmiş Etkinlikler");
                    adapter.setFragTitleList(1, "Katıldıklarım");
                }else{
                    trans.reverseTransition(1000);
                    listTab1.setEvents(currentAEvents);
                    listTab2.setEvents(currentPEvents);
                    adapter.setFragTitleList(0, "Gelecek Etkinlikler");
                    adapter.setFragTitleList(1, "Katılacaklarım");
                }

                refreshList();
            }
        });

        actionSwitch.setChecked(true);
        actionSwitch.setChecked(false);

        // Profile button
        ImageView image = actionView.findViewById(R.id.actionbar_listImage);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, ProfileActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("targetId", userId);
                startActivity(intent);
            }
        });

    }

    private void initializeAEvents(){

        DatabaseEvent databaseEvent = new DatabaseEvent();

        databaseEvent.readEvents(new DatabaseEvent.OngetEvents() {
            @Override
            public void successful(List<EventModel> eventModelList) {
                initializePEvents(eventModelList);
            }

            @Override
            public void cancelled(String exception) {
                Toast.makeText(ListActivity.this, exception, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializePEvents(final List<EventModel> eventModelList){

        // User - Participated Listener
        DatabaseUser databaseUser = new DatabaseUser();

        databaseUser.getParticipatedList(userId, new DatabaseUser.OngetParticipatedList() {
            @Override
            public void successful(List<String> participatedList) {

                currentPEvents.clear();
                pastPEvents.clear();
                currentAEvents.clear();
                pastAEvents.clear();

                for (EventModel eventModel : eventModelList){

                    if (participatedList.contains(eventModel.getEventId())) {
                        Event e = new Event(eventModel);
                        if (e.isPast() == 1) {
                            pastPEvents.add(e);
                        } else if (e.isPast() == 0) {
                            currentPEvents.add(e);
                        }
                    } else{
                        Event e = new Event(eventModel);
                        if (e.isPast() == 1) {
                            pastAEvents.add(e);
                        } else if (e.isPast() == 0) {
                            currentAEvents.add(e);
                        }
                    }
                }
                refreshList();
            }

            @Override
            public void cancelled(String exception) {
                Toast.makeText(ListActivity.this, exception, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void refreshList(){

        int f = viewPager.getCurrentItem();
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(f);
    }

    private void initializeNewButton(){

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, CreateActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

    }

}


