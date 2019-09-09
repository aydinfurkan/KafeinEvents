package com.example.kafeinevents.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.Event;
import com.example.kafeinevents.data.adapter.AttendeesAdapter;
import com.example.kafeinevents.data.dialog.EditTextDialog;
import com.example.kafeinevents.data.dialog.EditTimeDialog;
import com.example.kafeinevents.data.dialog.StorageDialog;
import com.example.kafeinevents.data.model.EventModel;
import com.example.kafeinevents.data.model.MyCalendar;
import com.example.kafeinevents.data.model.UserModel;
import com.example.kafeinevents.data.photo.Gallery;
import com.example.kafeinevents.database.DatabaseEvent;
import com.example.kafeinevents.database.DatabaseUser;
import com.example.kafeinevents.database.StorageEvent;
import com.example.kafeinevents.database.StorageStorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private static final int MULTIPLE_GALLERY_REQUEST = 2;
    private ImageView image;
    private TextView title;
    private TextView content;
    private TextView time;
    private EditText attendeesCount;
    private TextView creator;
    private Button joinButton;
    private Button joinedButton;
    private Button storageButton;
    private ProgressBar loading;

    private String userId;
    private String eventId;
    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        image = findViewById(R.id.detailImage);
        title = findViewById(R.id.detailTitle);
        content = findViewById(R.id.detailContent);
        time = findViewById(R.id.detailTime);
        attendeesCount = findViewById(R.id.detailAttendeesCount);
        creator = findViewById(R.id.detailCreator);
        joinButton = findViewById(R.id.detailJoinButton);
        joinedButton = findViewById(R.id.detailJoinedButton);
        loading = findViewById(R.id.detailLoading);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        eventId = intent.getStringExtra("eventId");
        event = new Event(new EventModel());

        initializeListener();
        initializeJButtons();
        initializeAttendees();
        initializeStorageButton();
    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(!event.getEventModel().getCreatorId().equals(userId)){
            Toast.makeText(DetailActivity.this, "Etkinlik yalnızca düzenleyicisi tarafından değiştirilebilir.", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        else if(event.isPast() == 1){
            Toast.makeText(DetailActivity.this, "Tarihi geçmiş bir etkinlik değiştirilemez.", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()) {
            case R.id.actionbar_detailEditImage:

                new Gallery(DetailActivity.this, false, GALLERY_REQUEST).open();

                return true;
            case R.id.actionbar_detailEditTime:

                EditTimeDialog edittimeDialog = new EditTimeDialog(DetailActivity.this, "Zaman",
                        event.getEventModel().getEventDate(), event.getEventModel().getEventTime());
                edittimeDialog.show(new EditTimeDialog.OngetTime() {
                    @Override
                    public void changed(String changedDate, String changedClock) {
                        event.getEventModel().setEventDate(changedDate);
                        event.getEventModel().setEventTime(changedClock);
                        DatabaseEvent databaseEvent = new DatabaseEvent();
                        databaseEvent.setEvent(eventId, event.getEventModel());
                    }
                });

                return true;
            case R.id.actionbar_detailEditTitle:

                EditTextDialog edittextDialog1 = new EditTextDialog(DetailActivity.this,"Başlık", event.getEventModel().getEventTitle());
                edittextDialog1.show(new EditTextDialog.OngetText() {
                    @Override
                    public void changed(String changedText) {
                        event.getEventModel().setEventTitle(changedText);
                        DatabaseEvent databaseEvent = new DatabaseEvent();
                        databaseEvent.setEvent(eventId, event.getEventModel());
                    }
                });

                return true;
            case R.id.actionbar_detailEditContent:

                EditTextDialog edittextDialog2 = new EditTextDialog(DetailActivity.this, "İçerik", event.getEventModel().getEventContent());
                edittextDialog2.show(new EditTextDialog.OngetText() {
                    @Override
                    public void changed(String changedText) {
                        event.getEventModel().setEventContent(changedText);
                        DatabaseEvent databaseEvent = new DatabaseEvent();
                        databaseEvent.setEvent(eventId, event.getEventModel());
                    }
                });

                return true;
            case R.id.actionbar_detailEditDelete:

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                DatabaseEvent databaseEvent = new DatabaseEvent();
                                finish();
                                databaseEvent.removeEvent(eventId, event.getEventModel());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.cancel();
                                break;
                        }
                    }
                };
                builder.setMessage("Etkinliği silmek istediğinize emin misiniz?").setPositiveButton("Evet", dialogClickListener)
                        .setNegativeButton("Hayır", dialogClickListener).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            final Uri eventImage = data.getData();
            StorageEvent storageEvent = new StorageEvent();
            storageEvent.writeImage(eventImage, eventId, new StorageEvent.OnsetImage() {
                @Override
                public void successful() {
                    image.setImageURI(eventImage);
                    image.setAlpha(1f);
                }

                @Override
                public void cancelled(String exception) {
                    Toast.makeText(DetailActivity.this, exception, Toast.LENGTH_LONG).show();
                }

                @Override
                public void progress(int progress) {

                }
            });
        }
        else if(requestCode == MULTIPLE_GALLERY_REQUEST && resultCode == RESULT_OK && data != null){


            ClipData imagesPath = data.getClipData();
            List<Uri> imagesUri = new ArrayList<>();

            if(imagesPath == null) {
                imagesUri.add(data.getData());
            }
            else{
                for (int i = 0; i < imagesPath.getItemCount(); i++) {
                    imagesUri.add(imagesPath.getItemAt(i).getUri());
                }
            }


            final ProgressDialog progressDialog = new ProgressDialog(DetailActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Uploading images...");
            progressDialog.show();

            new StorageStorage().writeImages(imagesUri, eventId, new StorageStorage.OnsetImages() {

                @Override
                public void successful(int successCount, int cancelCount) {
                    int sum = successCount + cancelCount;
                    String text = successCount + "/" + sum + " resim yüklendi.";
                    progressDialog.dismiss();
                    Toast.makeText(DetailActivity.this, text, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void progress(int progress) {
                    progressDialog.setProgress(progressDialog.getProgress() + progress);
                }
            });




        }

    }

    private void initializeAttendees() {

        final List<UserModel> attendees = new ArrayList<>();
        final AttendeesAdapter adapter = new AttendeesAdapter(DetailActivity.this, attendees);

        final Dialog dialog = new Dialog(DetailActivity.this);
        dialog.setContentView(R.layout.attendeeslist_layout);
        dialog.setTitle("Katılımcılar");
        final ListView list = dialog.findViewById(R.id.detail_attendeesList);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DetailActivity.this, ProfileActivity.class);
                intent.putExtra("userId", "testkullanicisi");
                intent.putExtra("targetId", attendees.get(i).getUserId());

                startActivity(intent);

            }
        });

        attendeesCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                attendees.clear();
                for(String attendeesId : event.getEventModel().getAttendeesId()){
                    DatabaseUser databaseUser = new DatabaseUser();
                    databaseUser.readUserModel(attendeesId, new DatabaseUser.OngetUserModel() {
                        @Override
                        public void successful(UserModel userModel) {
                            attendees.add(userModel);
                            list.setAdapter(adapter);
                        }

                        @Override
                        public void cancelled(String exception) {
                            Toast.makeText(DetailActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dialog.show();
            }
        });

    }

    private void initializeJButtons() {

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(event.isPast() == 1){
                    Toast.makeText(DetailActivity.this, "Can not join a past event", Toast.LENGTH_SHORT).show();
                }
                else if (event.isPast() == -1){
                    Toast.makeText(DetailActivity.this, "Failed to read event date.", Toast.LENGTH_SHORT).show();
                }
                else if(event.addAttendees(userId)){
                    DatabaseEvent databaseEvent = new DatabaseEvent();
                    databaseEvent.setEvent(eventId, event.getEventModel());

                    DatabaseUser databaseUser = new DatabaseUser();
                    databaseUser.setParticipated(userId, eventId, true);
                }
                else{
                    Toast.makeText(DetailActivity.this, "Failed to join event.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        joinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(event.isPast() == 1){
                    Toast.makeText(DetailActivity.this, "Can not leave a past event", Toast.LENGTH_SHORT).show();
                }
                else if (event.isPast() == -1){
                    Toast.makeText(DetailActivity.this, "Failed to read event date.", Toast.LENGTH_SHORT).show();
                }
                else if(event.delAttendees(userId)){
                    DatabaseEvent databaseEvent = new DatabaseEvent();
                    databaseEvent.setEvent(eventId, event.getEventModel());

                    DatabaseUser databaseUser = new DatabaseUser();
                    databaseUser.setParticipated(userId, eventId, false);
                }
                else{
                    Toast.makeText(DetailActivity.this, "Failed to leave event.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initializeStorageButton() {

        storageButton = findViewById(R.id.detailStorageButton);

        storageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new StorageDialog(DetailActivity.this, eventId, DetailActivity.this).show();
            }
        });

    }

    private void initializeListener(){

        DatabaseEvent databaseEvent = new DatabaseEvent();

        databaseEvent.readEvent(eventId, new DatabaseEvent.OngetEvent() {
            @Override
            public void successful(EventModel eventModel) {
                if(event.getEventModel().getCreatorId() == null){
                    event.setEventModel(eventModel);
                    isJoined();
                    isEventPast();  //TODO find effective way

                    DatabaseUser databaseUser = new DatabaseUser();

                    databaseUser.readUserModel(event.getEventModel().getCreatorId(), new DatabaseUser.OngetUserModel() {
                        @Override
                        public void successful(UserModel userModel) {
                            creator.setText(userModel.getName());
                        }

                        @Override
                        public void cancelled(String exception) {
                            Toast.makeText(DetailActivity.this, exception, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else{
                    event.setEventModel(eventModel);
                    isJoined();
                }
                refreshPage();

            }

            @Override
            public void cancelled(String exception) {
                Toast.makeText(DetailActivity.this, exception, Toast.LENGTH_SHORT).show();
            }
        });

        // Image Listener
        StorageEvent storageEvent = new StorageEvent();

        storageEvent.readImage(eventId, new StorageEvent.OngetImage() {
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

    private void isEventPast() {
        if(event.isPast() == 1){
            findViewById(R.id.detailWarning).setVisibility(View.VISIBLE);
            storageButton.setVisibility(View.VISIBLE);
            joinedButton.setEnabled(false);
            joinButton.setEnabled(false);
        }
        else if (event.isPast() == 0){
            storageButton.setVisibility(View.GONE);
            joinedButton.setEnabled(true);
            joinButton.setEnabled(true);
        }
        else
            Toast.makeText(DetailActivity.this, "Failed to read event date.", Toast.LENGTH_SHORT).show();
    }

    private void isJoined() {

        for (String s : event.getEventModel().getAttendeesId()){
            if (s.equals(userId)){
                joinButton.setVisibility(View.GONE);
                joinedButton.setVisibility(View.VISIBLE);
                storageButton.setEnabled(true);
                return;
            }
        }
        storageButton.setEnabled(false);
        joinButton.setVisibility(View.VISIBLE);
        joinedButton.setVisibility(View.GONE);

    }

    private void refreshPage(){
        EventModel eventModel = event.getEventModel();
        title.setText(eventModel.getEventTitle());
        content.setText(eventModel.getEventContent());

        MyCalendar myCalendar = new MyCalendar(eventModel.getEventDate(), eventModel.getEventTime());
        String s = myCalendar.getDateToString() + " " + myCalendar.getClockToString();
        time.setText(s);

        String aCount = event.getAttendeesCount() + " Katılımcı";
        attendeesCount.setText(aCount);


    }
}
