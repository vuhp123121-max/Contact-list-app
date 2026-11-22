package com.example.danhbcusinhvin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class ContactRepository {
    private ContactDatabaseHelper helper;
    private SQLiteDatabase db;
    public ContactRepository(Context context)
    {
        helper = new ContactDatabaseHelper(context);
    }
    public void opentoWrite()
    {
        db = helper.getWritableDatabase();
    }
    public  void opentoRead()
    {
        db = helper.getReadableDatabase();
    }
    public void close()
    {
        db.close();
    }


}
