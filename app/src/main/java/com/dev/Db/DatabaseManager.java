package com.dev.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.dev.Models.ListItem;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManager() {
    }

    public DatabaseManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long insert(String title, String description, int check, String fecha, String hora) {
        ContentValues contentValue = new ContentValues();
        contentValue.put("title", title);
        contentValue.put("description", description);
        contentValue.put("estado", check);
        contentValue.put("fecha", fecha);
        contentValue.put("hora", hora);
        return database.insert("todo_task", null, contentValue);
    }

    public long delete(int id) {
        String whereClause = "_id = ?";
        String[] whereArgs = {String.valueOf(id)};
        return database.delete("todo_task", whereClause, whereArgs);
    }

    public Cursor fetch() {
        String[] columns = new String[]{"_id", "title", "description", "estado", "fecha", "hora"};
        Cursor cursor = database.query("todo_task", columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public void updateItemState(ListItem listItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("estado", listItem.getCheck());
        String whereClause = "_id = ?";
        String[] whereArgs = {String.valueOf(listItem.getId())};
        database.update("todo_task", contentValues, whereClause, whereArgs);
    }
}
