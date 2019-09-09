package com.example.kafeinevents.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.model.EventModel;
import com.example.kafeinevents.data.model.MyCalendar;
import com.example.kafeinevents.data.photo.Gallery;
import com.example.kafeinevents.database.DatabaseEvent;
import com.example.kafeinevents.database.DatabaseUser;
import com.example.kafeinevents.database.StorageEvent;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class CreateActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 1;
    private EditText eventTitle;
    private EditText eventContent;
    private EditText eventDate;
    private TimePicker eventClock;
    private ImageButton createButton;
    private ImageButton cancelButton;
    private String userId;
    private ImageView image;
    private Uri eventImage;
    private MyCalendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        eventTitle = findViewById(R.id.createEventTitle);
        eventContent = findViewById(R.id.createEventContent);
        eventDate = findViewById(R.id.createEventDate);
        eventClock = findViewById(R.id.createEventClock);
        createButton = findViewById(R.id.createCreateButton);
        cancelButton = findViewById(R.id.createCancelButton);
        image = findViewById(R.id.createImage);

        eventClock.setIs24HourView(true);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initializeCancelButton();
        initializeCalendar();
        initializeCreateButton();
        initializeImage();

    }

    private void initializeCancelButton() {

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);

                final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                dialog.cancel();
                                break;
                        }
                    }
                };
                builder.setMessage("Etkinliği oluşturmadan çıkmak istediğinize emin misiniz?").setPositiveButton("Evet", dialogClickListener)
                        .setNegativeButton("Hayır", dialogClickListener).show();
            }
        });

    }

    private void initializeImage() {

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Gallery(CreateActivity.this, false, GALLERY_REQUEST).open();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == GALLERY_REQUEST) try {
            eventImage = data.getData();
            Picasso.with(CreateActivity.this).load(eventImage).into(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeCreateButton(){

        final ProgressDialog progressDialog = new ProgressDialog(CreateActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Creating event...");

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Upload Event
                myCalendar.setHour(eventClock.getCurrentHour());
                myCalendar.setMinute(eventClock.getCurrentMinute());
                String eH = myCalendar.getClockToString();
                String eD = eventDate.getText().toString();
                String eT = eventTitle.getText().toString();
                String eC = eventContent.getText().toString();

                if (TextUtils.isEmpty(eT) || TextUtils.isEmpty(eC)){
                    Toast.makeText(CreateActivity.this,"Başlık ya da açıklama boş olamaz.", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();

                DatabaseEvent databaseEvent = new DatabaseEvent();

                try {

                    String eventId = databaseEvent.createEventId();
                    EventModel eventModel = new EventModel(eventId, eT, eC, eD, eH, userId);
                    databaseEvent.setEvent(eventId, eventModel);

                    DatabaseUser databaseUser = new DatabaseUser();
                    databaseUser.setParticipated(userId, eventId, true);
                    databaseUser.setCreated(userId, eventId, true);

                    // Upload Image
                    if(eventImage != null){

                        StorageEvent storageEvent = new StorageEvent();

                        storageEvent.writeImage(eventImage, eventId, new StorageEvent.OnsetImage() {
                            @Override
                            public void successful() {
                                finish();
                            }

                            @Override
                            public void cancelled(String exception) {
                                Toast.makeText(CreateActivity.this, exception, Toast.LENGTH_LONG).show();
                                finish();
                            }

                            @Override
                            public void progress(int progress) {
                                progressDialog.setProgress(progress);
                            }
                        });

                    }else{
                        finish();
                    }

                } catch (Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void initializeCalendar(){

        myCalendar = new MyCalendar(null, null);

        eventDate.setText(myCalendar.getDateToString());

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                myCalendar.setYear(year);
                myCalendar.setMonth(month);
                myCalendar.setDay(day);

                eventDate.setText(myCalendar.getDateToString());
            }
        };

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateActivity.this, date,
                        myCalendar.getYear(), myCalendar.getMonth(),
                        myCalendar.getDay());

                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

    }

}
