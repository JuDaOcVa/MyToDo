package com.dev.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dev.Models.ListItem;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(ListItem listItem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, listItem.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, listItem.getDescription());
        values.put(DatabaseHelper.COLUMN_EMOJI, listItem.getEmoji());
        values.put(DatabaseHelper.COLUMN_IS_CHECK, listItem.getCheck());
        values.put(DatabaseHelper.COLUMN_FECHA, listItem.getFecha());
        values.put(DatabaseHelper.COLUMN_HORA, listItem.getHora());
        database.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    public void update(ListItem listItem) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, listItem.getTitle());
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, listItem.getDescription());
        values.put(DatabaseHelper.COLUMN_EMOJI, listItem.getEmoji());
        values.put(DatabaseHelper.COLUMN_IS_CHECK, listItem.getCheck());
        values.put(DatabaseHelper.COLUMN_FECHA, listItem.getFecha());
        values.put(DatabaseHelper.COLUMN_HORA, listItem.getHora());

        database.update(DatabaseHelper.TABLE_NAME, values, DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(listItem.getId())});
    }

    public void delete(int id) {
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public Cursor fetch() {
        String[] columns = new String[] { DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_EMOJI, DatabaseHelper.COLUMN_IS_CHECK, DatabaseHelper.COLUMN_FECHA, DatabaseHelper.COLUMN_HORA };
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
}