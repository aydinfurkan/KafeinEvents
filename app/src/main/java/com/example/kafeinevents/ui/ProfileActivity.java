package com.example.kafeinevents.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.Event;
import com.example.kafeinevents.data.adapter.PagerAdapter;
import com.example.kafeinevents.data.dialog.EditTextDialog;
import com.example.kafeinevents.data.model.EventModel;
import com.example.kafeinevents.data.model.UserModel;
import com.example.kafeinevents.data.photo.Gallery;
import com.example.kafeinevents.database.DatabaseEvent;
import com.example.kafeinevents.database.DatabaseUser;
import com.example.kafeinevents.database.StorageUser;
import com.example.kafeinevents.fragment.ListTab;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private ImageView image;
    private ProgressBar loading;

    private TextView userIdView;
    private TextView userName;
    private TextView participationCount;
    private TextView createdCount;
    private PagerAdapter adapter;
    private ViewPager viewPager;
    private ListTab listTab1;
    private ListTab listTab2;

    private List<Event> currentCEvents = new ArrayList<>();
    private List<Event> pastCEvents = new ArrayList<>();
    private List<Event> currentPEvents = new ArrayList<>();
    private List<Event> pastPEvents = new ArrayList<>();

    private String targetId;
    private String userId;
    private UserModel targetUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userIdView = findViewById(R.id.profileUserId);
        userName = findViewById(R.id.profileUserName);
        participationCount = findViewById(R.id.profileParticipationCount);
        createdCount = findViewById(R.id.profileCreatedCount);
        image = findViewById(R.id.profileImage);
        loading = findViewById(R.id.profileLoading);
        viewPager = findViewById(R.id.profileViewPager);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        targetId = intent.getStringExtra("targetId");

        if(userId.equals(targetId)){
            initializeLogout();
            initializeImageButton();
            initializeEditName();
        }

        initializeViewPager();
        initializeSwitch();
        initializeListeners();
        initializeDragDrop();

    }

    private void initializeEditName() {

        ImageButton editName = findViewById(R.id.profileEditName);
        editName.setVisibility(View.VISIBLE);

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditTextDialog edittextDialog = new EditTextDialog(ProfileActivity.this, "İsim", targetUser.getName());
                edittextDialog.show(new EditTextDialog.OngetText() {
                    @Override
                    public void changed(String changedText) {
                        targetUser.setName(changedText);
                        DatabaseUser databaseUser = new DatabaseUser();
                        databaseUser.setProfile(userId, targetUser);
                    }
                });
            }
        });

    }

    private void initializeDragDrop() {

        final TextView eventTitle = findViewById(R.id.profileEventTitle);
        final ConstraintLayout profileInfoLayout = findViewById(R.id.profileInfoLayout);
        final ConstraintLayout profileListLayout = findViewById(R.id.profileListLayout);

        final LinearLayout.LayoutParams infoParams = (LinearLayout.LayoutParams) profileInfoLayout.getLayoutParams();
        final LinearLayout.LayoutParams listParams = (LinearLayout.LayoutParams) profileListLayout.getLayoutParams();

        eventTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(infoParams.weight == 1){
                    infoParams.weight = 0;
                    listParams.weight = 1;
                }
                else{
                    infoParams.weight = 1;
                    listParams.weight = 0;
                }
                profileInfoLayout.setLayoutParams(infoParams);
                profileListLayout.setLayoutParams(listParams);
            }
        });
    }

    private void initializeViewPager() {

        TabLayout tabLayout = findViewById(R.id.profileTab);

        adapter = new PagerAdapter(getSupportFragmentManager());

        tabLayout.setupWithViewPager(viewPager);
        listTab1 = new ListTab(currentPEvents, userId);
        adapter.addFrag(listTab1, "");

        listTab2 = new ListTab(currentCEvents, userId);
        adapter.addFrag(listTab2, "");


    }

    private void initializeListeners() {

        // User Listener
        DatabaseUser databaseUser = new DatabaseUser();

        databaseUser.readUser(targetId, new DatabaseUser.OngetUser() {
            @Override
            public void successful(List<String> cEventIds, List<String> pEventIds, UserModel userModel) {

                if(currentCEvents.isEmpty())
                    initializeEvents(cEventIds, pEventIds);

                targetUser = userModel;
                refreshProfile();

            }

            @Override
            public void cancelled(String exception) {
                Toast.makeText(ProfileActivity.this, exception, Toast.LENGTH_SHORT).show();
            }
        });

        // Image Listener
        StorageUser storageUser = new StorageUser();

        storageUser.readImage(targetId, new StorageUser.OngetImage() {
            @Override
            public void successful(File file) {
                image.setImageURI(Uri.fromFile(file));
                image.setAlpha(1f);
                loading.setVisibility(View.GONE);
            }

            @Override
            public void cancelled(String exception) {
                loading.setVisibility(View.GONE);
            }
        });

    }

    private void refreshProfile() {

        String CC = "Oluşturma sayısı : " + (pastCEvents.size() + currentCEvents.size());
        createdCount.setText(CC);

        String PC = "Katılım sayısı : " + (pastPEvents.size() + currentPEvents.size());
        participationCount.setText(PC);

        String N = "İsim : " + targetUser.getName();
        userName.setText(N);

        String ID = "ID : " + targetUser.getUserId();
        userIdView.setText(ID);

    }

    private void initializeEvents(final List<String> cEventIds, final List<String> pEventIds) {

        DatabaseEvent databaseEvent = new DatabaseEvent();

        databaseEvent.readEvents(new DatabaseEvent.OngetEvents() {
            @Override
            public void successful(List<EventModel> eventModelList) {

                pastCEvents.clear();
                pastPEvents.clear();
                currentCEvents.clear();
                currentPEvents.clear();

                for (EventModel eventModel : eventModelList) {

                    if (cEventIds.contains(eventModel.getEventId())) {
                        Event e = new Event(eventModel);
                        if (e.isPast() == 1) {
                            pastCEvents.add(e);
                        } else if (e.isPast() == 0) {
                            currentCEvents.add(e);
                        }

                    } else if (pEventIds.contains(eventModel.getEventId())) {
                        Event e = new Event(eventModel);
                        if (e.isPast() == 1) {
                            pastPEvents.add(e);
                        } else if (e.isPast() == 0) {
                            currentPEvents.add(e);
                        }
                    }
                }
                refreshList();
                refreshProfile();
            }

            @Override
            public void cancelled(String exception) {
                Toast.makeText(ProfileActivity.this, exception, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void refreshList() {

        int f = viewPager.getCurrentItem();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(f);

    }

    private void initializeSwitch(){

        Switch pSwitch = findViewById(R.id.profileSwitch);

        ColorDrawable[] colors = {new ColorDrawable(0xFAFAFA), new ColorDrawable(0xFFD5EFEC)};
        final TransitionDrawable trans = new TransitionDrawable(colors);
        viewPager.setBackground(trans);

        pSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    trans.startTransition(1000);
                    listTab1.setEvents(pastPEvents);
                    listTab2.setEvents(pastCEvents);
                    adapter.setFragTitleList(0, "Katıldıkları");
                    adapter.setFragTitleList(1, "Oluşturdukları");
                }else{
                    trans.reverseTransition(1000);
                    listTab1.setEvents(currentPEvents);
                    listTab2.setEvents(currentCEvents);
                    adapter.setFragTitleList(0, "Katılmayı düşündüğü");
                    adapter.setFragTitleList(1, "Oluşturdukları");
                }

                refreshList();
            }
        });

        pSwitch.setChecked(true);
        pSwitch.setChecked(false);

    }

    private void initializeLogout() {

        Button logout = findViewById(R.id.profileLogout);

        logout.setVisibility(View.VISIBLE);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences SP = getSharedPreferences("com.example.kafeinevents.login", MODE_PRIVATE);
                SP.edit().clear().apply();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });

    }

    private void initializeImageButton(){

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Gallery(ProfileActivity.this, false, GALLERY_REQUEST).open();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null)try{
            Uri eventImage = data.getData();
            updateProfileStorage(eventImage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateProfileStorage(final Uri eventImage) {

        StorageUser storageUser = new StorageUser();

        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Uploading image...");
        progressDialog.show();

        storageUser.writeImage(eventImage, userId, new StorageUser.OnsetImage() {
            @Override
            public void successful() {
                image.setImageURI(eventImage);
                progressDialog.dismiss();
            }

            @Override
            public void cancelled(String exception) {
                Toast.makeText(ProfileActivity.this, exception, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void progress(int progress) {
                progressDialog.setProgress(progress);
            }
        });

    }
}
