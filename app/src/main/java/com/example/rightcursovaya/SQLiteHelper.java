package com.example.rightcursovaya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "clinic.db";
    private static final int DATABASE_VERSION = 1;

    // Таблица illnesses
    private static final String TABLE_ILLNESSES = "illnesses";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблиц
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ILLNESSES_TABLE = "CREATE TABLE " + TABLE_ILLNESSES + " ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT,"
                + "description TEXT,"
                + "recommendation TEXT"
                + ")";
        db.execSQL(CREATE_ILLNESSES_TABLE);
    }

    // Обновление БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ILLNESSES);
        onCreate(db);
    }

    // Вставка в illnesses
    public void insertIllness(Illness illness) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", illness.getId());
        values.put("name", illness.getName());
        values.put("description", illness.getDescription());
        values.put("recommendation", illness.getRecommendation());
        db.insertWithOnConflict(TABLE_ILLNESSES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public List<Illness> getAllIllnesses() {
        List<Illness> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("illnesses", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Illness illness = new Illness();
                illness.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                illness.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                illness.setRecommendation(cursor.getString(cursor.getColumnIndexOrThrow("recommendation")));
                list.add(illness);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}