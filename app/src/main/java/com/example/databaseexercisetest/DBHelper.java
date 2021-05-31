package com.example.databaseexercisetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExerciseDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_Name="Exercise";

    public static final String COL_ID = "ID";
    public static final String COL_Name="Name";
    public static final String COL_Image="Image";
    public static final String COL_Description="Description";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_Name + "( " + COL_ID  +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_Name + " TEXT, " +
                COL_Description +" TEXT, " +
                COL_Image + " BLOB" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
