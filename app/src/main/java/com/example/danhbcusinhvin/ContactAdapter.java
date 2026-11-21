package com.example.danhbcusinhvin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

final class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    interface Listener {
        void onOpen(Contact contact);
        void onEdit(Contact contact);
        void onDelete(Contact contact);
        void onDial(Contact contact);
    }

    private final List<Contact> contacts = new ArrayList<>();
    private final Listener listener;

    ContactAdapter(Listener listener) {
        this.listener = listener;
    }

    void submitList(List<Contact> items) {
        contacts.clear();
        contacts.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.name.setText(contact.name);
        holder.main.setOnClickListener(view -> listener.onOpen(contact));
        holder.edit.setOnClickListener(view -> listener.onEdit(contact));
        holder.delete.setOnClickListener(view -> listener.onDelete(contact));
        holder.dial.setOnClickListener(view -> listener.onDial(contact));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static final class ContactViewHolder extends RecyclerView.ViewHolder {
        final View main;
        final TextView name;
        final TextView edit;
        final TextView delete;
        final View dial;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.mainLayout);
            name = itemView.findViewById(R.id.contact_name);
            edit = itemView.findViewById(R.id.contact_edit);
            delete = itemView.findViewById(R.id.contact_delete);
            dial = itemView.findViewById(R.id.contact_number_dial);
        }
    }
}
