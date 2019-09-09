package com.example.kafeinevents.data.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.kafeinevents.R;


public class EditTextDialog extends Dialog {

    private TextView title;
    private EditText text;
    private Button submit;
    private Button cancel;

    public EditTextDialog(@NonNull Context context, String titleString, String textString) {
        super(context);
        setContentView(R.layout.edittext_layout);

        text = findViewById(R.id.edittext_layoutText);

        title = findViewById(R.id.edittext_layoutTitle);
        title.setText(titleString);;

        submit = findViewById(R.id.edittext_layoutSubmit);
        cancel = findViewById(R.id.edittext_layoutCancel);

        text.setText(textString);

    }

    public interface OngetText{
        void changed(String changedText);
    }

    public void show(OngetText ongetText) {
        super.show();
        initializeTextListener(ongetText);
    }

    private void initializeTextListener(final OngetText ongetText) {

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ongetText.changed(text.getText().toString());
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

    @Override
    public void show() {
    }
}
