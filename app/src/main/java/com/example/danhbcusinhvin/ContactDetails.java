package com.example.danhbcusinhvin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ContactDetails extends AppCompatActivity {
    private static final int REQUEST_EDIT = 3;
    private int contactIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        contactIndex = getIntent().getIntExtra(AddEditContact.EXTRA_INDEX, -1);
        showContact(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.editContact) {
            Intent edit = new Intent(this, AddEditContact.class);
            copyContactExtras(getIntent(), edit);
            startActivityForResult(edit, REQUEST_EDIT);
            return true;
        }
        if (item.getItemId() == R.id.deleteContact) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK && data != null) {
            showContact(data);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void showContact(Intent intent) {
        setTitle(intent.getStringExtra(AddEditContact.EXTRA_NAME));
        setText(R.id.nameTv, intent.getStringExtra(AddEditContact.EXTRA_NAME));
        setText(R.id.phoneTv, intent.getStringExtra(AddEditContact.EXTRA_PHONE));
        setText(R.id.emailTv, intent.getStringExtra(AddEditContact.EXTRA_EMAIL));
        setText(R.id.WorkplaceTv, intent.getStringExtra(AddEditContact.EXTRA_WORKPLACE));
        setText(R.id.ClassIDtv, intent.getStringExtra(AddEditContact.EXTRA_CLASS_ID));
        setText(R.id.addedTimeTv, intent.getStringExtra(AddEditContact.EXTRA_ADDED_TIME));
        setText(R.id.updatedTimeTv, intent.getStringExtra(AddEditContact.EXTRA_UPDATED_TIME));
    }

    private void setText(int viewId, String value) {
        TextView textView = findViewById(viewId);
        textView.setText(value == null || value.isEmpty() ? getString(R.string.not_provided) : value);
    }

    private void copyContactExtras(Intent source, Intent destination) {
        destination.putExtras(source);
        destination.putExtra(AddEditContact.EXTRA_INDEX, contactIndex);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_contact_confirm)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    Intent result = new Intent();
                    result.putExtra(AddEditContact.EXTRA_INDEX, contactIndex);
                    setResult(RESULT_FIRST_USER, result);
                    finish();
                })
                .show();
    }
}
