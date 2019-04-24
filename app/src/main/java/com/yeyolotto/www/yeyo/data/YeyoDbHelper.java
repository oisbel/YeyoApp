package com.yeyolotto.www.yeyo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yeyolotto.www.yeyo.data.YeyoContract.*;

/**
 * Se comunica con la base de datos
 */
public class YeyoDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yeyo.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public YeyoDbHelper( Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold tiro data
        final String SQL_CREATE_TIRO_TABLE = "CREATE TABLE " + TiroEntry.TABLE_NAME + " (" +
                TiroEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TiroEntry.COLUMN_FECHA + " TEXT NOT NULL, " +
                TiroEntry.COLUMN_HORA + " TEXT NOT NULL, " +
                TiroEntry.COLUMN_TIRO + " TEXT NOT NULL, " +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_TIRO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        // COMPLETED (9) Inside, execute a drop table query, and then call onCreate to re-create it
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TiroEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
