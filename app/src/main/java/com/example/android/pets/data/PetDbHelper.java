package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.pets.data.PetContract.PetEntry;

public class PetDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shelter.db";
    public static final int VERSION_NUMBER = 1;

    public static final String CREATE_ENTRIES_COMMAND =
            "CREATE TABLE " + PetEntry.TABLE_NAME + "("
                    + PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL,"
                    + PetEntry.COLUMN_PET_BREED + " TEXT,"
                    + PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL,"
                    + PetEntry.COLUMN_PET_WEIGHT + " INTEGER DEFAULT 0);";


    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ENTRIES_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
