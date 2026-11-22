package com.example.danhbcusinhvin;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ContactAdapter.Listener {
    private static final int REQUEST_ADD_EDIT = 1;
    private static final int REQUEST_DETAILS = 2;

    private final List<Contact> contacts = new ArrayList<>();
    private final List<Contact> filteredContacts = new ArrayList<>();
    private ContactAdapter adapter;
    private TextView emptyView;
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.contactRv);
        emptyView = findViewById(R.id.emptyTv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(this);
        recyclerView.setAdapter(adapter);
        refreshList();

        FloatingActionButton addContactButton = findViewById(R.id.fab);
        addContactButton.setOnClickListener(view -> openAddEdit(-1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_top_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.searchContact);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.search_contacts));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    filterContacts(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterContacts(newText);
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.deleteAllContact) {
            confirmDeleteAll();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddEdit(int index) {
        Intent intent = new Intent(this, AddEditContact.class);
        if (index >= 0) {
            putContactExtras(intent, contacts.get(index), index);
        }
        startActivityForResult(intent, REQUEST_ADD_EDIT);
    }

    private void openDetails(Contact contact) {
        int index = contacts.indexOf(contact);
        if (index < 0) return;
        Intent intent = new Intent(this, ContactDetails.class);
        putContactExtras(intent, contact, index);
        startActivityForResult(intent, REQUEST_DETAILS);
    }

    private void putContactExtras(Intent intent, Contact contact, int index) {
        intent.putExtra(AddEditContact.EXTRA_INDEX, index);
        intent.putExtra(AddEditContact.EXTRA_NAME, contact.name);
        intent.putExtra(AddEditContact.EXTRA_PHONE, contact.phone);
        intent.putExtra(AddEditContact.EXTRA_EMAIL, contact.email);
        intent.putExtra(AddEditContact.EXTRA_WORKPLACE, contact.workplace);
        intent.putExtra(AddEditContact.EXTRA_CLASS_ID, contact.classID);
        intent.putExtra(AddEditContact.EXTRA_ADDED_TIME, contact.addedTime);
        intent.putExtra(AddEditContact.EXTRA_UPDATED_TIME, contact.updatedTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD_EDIT || requestCode == REQUEST_DETAILS)) {
            int index = data.getIntExtra(AddEditContact.EXTRA_INDEX, -1);
            String name = data.getStringExtra(AddEditContact.EXTRA_NAME);
            if (name == null) return;
            String updated = data.getStringExtra(AddEditContact.EXTRA_UPDATED_TIME);
            String added = data.getStringExtra(AddEditContact.EXTRA_ADDED_TIME);
            if (added == null || added.isEmpty()) added = updated;
            Contact contact = new Contact(
                    name,
                    valueOrEmpty(data.getStringExtra(AddEditContact.EXTRA_PHONE)),
                    valueOrEmpty(data.getStringExtra(AddEditContact.EXTRA_EMAIL)),
                    valueOrEmpty(data.getStringExtra(AddEditContact.EXTRA_WORKPLACE)),
                    valueOrEmpty(added),
                    valueOrEmpty(updated));
            if (index >= 0 && index < contacts.size()) contacts.set(index, contact);
            else contacts.add(contact);
            refreshList();
        } else if (resultCode == RESULT_FIRST_USER) {
            int index = data.getIntExtra(AddEditContact.EXTRA_INDEX, -1);
            if (index >= 0 && index < contacts.size()) {
                contacts.remove(index);
                refreshList();
            }
        }
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    private void filterContacts(String query) {
        currentQuery = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        refreshList();
    }

    private void refreshList() {
        filteredContacts.clear();
        for (Contact contact : contacts) {
            if (contact.name.toLowerCase(Locale.ROOT).contains(currentQuery)
                    || contact.phone.toLowerCase(Locale.ROOT).contains(currentQuery)
                    || contact.email.toLowerCase(Locale.ROOT).contains(currentQuery)) {
                filteredContacts.add(contact);
            }
        }
        if (adapter != null) adapter.submitList(filteredContacts);
        if (emptyView != null) {
            emptyView.setText(contacts.isEmpty() ? R.string.empty_contacts : R.string.no_search_results);
            emptyView.setVisibility(filteredContacts.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onOpen(Contact contact) {
        openDetails(contact);
    }

    @Override
    public void onEdit(Contact contact) {
        openAddEdit(contacts.indexOf(contact));
    }

    @Override
    public void onDelete(Contact contact) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_contact_confirm)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    contacts.remove(contact);
                    refreshList();
                })
                .show();
    }

    @Override
    public void onDial(Contact contact) {
        if (!contact.phone.isEmpty()) {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(contact.phone))));
        }
    }

    private void confirmDeleteAll() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.delete_all_confirm)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    contacts.clear();
                    refreshList();
                })
                .show();
    }
}
