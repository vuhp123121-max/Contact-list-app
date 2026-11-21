package com.example.danhbcusinhvin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.text.format.DateFormat;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddEditContact extends AppCompatActivity {
    static final String EXTRA_NAME = "contact_name";
    static final String EXTRA_PHONE = "contact_phone";
    static final String EXTRA_EMAIL = "contact_email";
    static final String EXTRA_NOTE = "contact_note";
    static final String EXTRA_ADDED_TIME = "contact_added_time";
    static final String EXTRA_UPDATED_TIME = "contact_updated_time";
    static final String EXTRA_INDEX = "contact_index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        EditText nameInput = findViewById(R.id.nameEt);
        EditText phoneInput = findViewById(R.id.phoneEt);
        EditText emailInput = findViewById(R.id.emailEt);
        EditText noteInput = findViewById(R.id.noteEt);
        FloatingActionButton saveButton = findViewById(R.id.fab);
        int index = getIntent().getIntExtra(EXTRA_INDEX, -1);

        if (index >= 0) {
            setTitle(R.string.edit_contact);
            nameInput.setText(getIntent().getStringExtra(EXTRA_NAME));
            phoneInput.setText(getIntent().getStringExtra(EXTRA_PHONE));
            emailInput.setText(getIntent().getStringExtra(EXTRA_EMAIL));
            noteInput.setText(getIntent().getStringExtra(EXTRA_NOTE));
        } else {
            setTitle(R.string.add_contact);
        }

        saveButton.setOnClickListener(view -> {
            String name = nameInput.getText().toString().trim();
            if (name.isEmpty()) {
                nameInput.setError(getString(R.string.contact_name_required));
                nameInput.requestFocus();
                return;
            }

            Intent result = new Intent();
            result.putExtra(EXTRA_INDEX, index);
            result.putExtra(EXTRA_NAME, name);
            result.putExtra(EXTRA_PHONE, phoneInput.getText().toString().trim());
            result.putExtra(EXTRA_EMAIL, emailInput.getText().toString().trim());
            result.putExtra(EXTRA_NOTE, noteInput.getText().toString().trim());
            result.putExtra(EXTRA_ADDED_TIME, getIntent().getStringExtra(EXTRA_ADDED_TIME));
            result.putExtra(EXTRA_UPDATED_TIME, DateFormat.format("yyyy-MM-dd HH:mm", new Date()).toString());
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
