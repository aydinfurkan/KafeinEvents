package com.example.kafeinevents.data.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.kafeinevents.R;
import com.example.kafeinevents.data.model.MyCalendar;

import java.util.Calendar;

public class EditTimeDialog extends Dialog {

    private TextView title;
    private TimePicker clockPicker;
    private DatePicker datePicker;
    private Button submit;
    private Button cancel;
    private MyCalendar myCalendar;

    public EditTimeDialog(@NonNull Context context, String titleString, String dateString, String clockString) {
        super(context);
        setContentView(R.layout.edittime_layout);

        title = findViewById(R.id.edittime_layoutTitle);
        title.setText(titleString);

        clockPicker = findViewById(R.id.edittime_layoutClock);
        datePicker = findViewById(R.id.edittime_layoutDate);

        submit = findViewById(R.id.edittime_layoutSubmit);
        cancel = findViewById(R.id.edittime_layoutCancel);

        myCalendar = new MyCalendar(dateString, clockString);
        datePicker.init(myCalendar.getYear(), myCalendar.getMonth(), myCalendar.getDay(), null);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

        clockPicker.setIs24HourView(true);
        clockPicker.setCurrentHour(myCalendar.getHour());
        clockPicker.setCurrentMinute(myCalendar.getMinute());

    }

    public interface OngetTime{
        void changed(String changedDate, String changedClock);
    }

    public void show(OngetTime ongetTime) {
        super.show();
        initializeTimeListener(ongetTime);
    }

    private void initializeTimeListener(final OngetTime ongetTime) {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myCalendar.setYear(datePicker.getYear());
                myCalendar.setMonth(datePicker.getMonth());
                myCalendar.setDay(datePicker.getDayOfMonth());

                myCalendar.setHour(clockPicker.getCurrentHour());
                myCalendar.setMinute(clockPicker.getCurrentMinute());

                ongetTime.changed(myCalendar.getDateToString(), myCalendar.getClockToString());
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

}
