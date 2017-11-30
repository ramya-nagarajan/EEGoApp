package edu.iu.eego;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ramya on 11/29/2017.
 */

public class EEGDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EEGo.db";

    SQLiteDatabase database;

    private static final String SQL_CREATE_ENTRIES_PROFILE =
            "CREATE TABLE " + EEGContract.EEGo_Profile.TABLE_NAME + " (" +
                    EEGContract.EEGo_Profile._ID + " INTEGER PRIMARY KEY," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_PLAN_NAME + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_NUMBER + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_BEFORE + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_LENGTH + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_AFTER + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_DESC + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_CALM_SECONDS + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_CALM_POINTS + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_RECOVERIES + " TEXT," +
                    EEGContract.EEGo_Profile.COLUMN_NAME_HEART_RATE + " TEXT)";

    private static final String SQL_CREATE_ENTRIES_COMMUNITY =
            "CREATE TABLE " + EEGContract.EEGo_Community.TABLE_NAME + " (" +
                    EEGContract.EEGo_Community._ID + " INTEGER PRIMARY KEY," +
                    EEGContract.EEGo_Community.COLUMN_NAME_TOTAL_POINTS + " TEXT," +
                    EEGContract.EEGo_Community.COLUMN_NAME_CALM_POINTS + " TEXT)";

    public EEGDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        database = db;
        db.execSQL(SQL_CREATE_ENTRIES_PROFILE);
        db.execSQL(SQL_CREATE_ENTRIES_COMMUNITY);
        long id = 1;
        Community community = new Community(id, "50000", "3000");
        ContentValues values = new ContentValues();
        values.put(EEGContract.EEGo_Community.COLUMN_NAME_TOTAL_POINTS, community.getTotalPoints());
        values.put(EEGContract.EEGo_Community.COLUMN_NAME_CALM_POINTS, community.getCalmPoints());
        values.put(EEGContract.EEGo_Community._ID, community.getId());
        db.insert(EEGContract.EEGo_Community.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addSessionInfoToDB(Profile profile) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_PLAN_NAME, profile.getPlanName());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_NUMBER, profile.getSessioNumber());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_BEFORE, profile.getMoodBefore());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_LENGTH, profile.getSessionLength());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_AFTER, profile.getMoodAfter());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_DESC, profile.getMoodDesc());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_CALM_SECONDS, profile.getCalmSeconds());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_CALM_POINTS, profile.getCalmPoints());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_RECOVERIES, profile.getRecoveries());
        values.put(EEGContract.EEGo_Profile.COLUMN_NAME_HEART_RATE, profile.getHeartRate());
        db.insert(EEGContract.EEGo_Profile.TABLE_NAME, null, values);
    }

    public void updateCommunityInfo(Community community) {
        SQLiteDatabase db = getWritableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(EEGContract.EEGo_Community.COLUMN_NAME_TOTAL_POINTS, community.getTotalPoints());
        values.put(EEGContract.EEGo_Community.COLUMN_NAME_CALM_POINTS, community.getCalmPoints());

        // Which row to update, based on the title
        String selection = EEGContract.EEGo_Community._ID + " =1";
        String[] selectionArgs = {};

        db.update(
                EEGContract.EEGo_Community.TABLE_NAME,
                values,
                selection,
                selectionArgs);

    }

    public ArrayList<Profile> fetchProfilesListFromDB() {
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {
                EEGContract.EEGo_Profile._ID,
                EEGContract.EEGo_Profile.COLUMN_NAME_PLAN_NAME,
                EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_NUMBER,
                EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_BEFORE,
                EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_LENGTH,
                EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_AFTER,
                EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_DESC,
                EEGContract.EEGo_Profile.COLUMN_NAME_CALM_SECONDS,
                EEGContract.EEGo_Profile.COLUMN_NAME_CALM_POINTS,
                EEGContract.EEGo_Profile.COLUMN_NAME_RECOVERIES,
                EEGContract.EEGo_Profile.COLUMN_NAME_HEART_RATE
        };


        Cursor cursor = db.query(
                EEGContract.EEGo_Profile.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<Profile> profileList = new ArrayList<>();


        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile._ID));
            String sessionNumber = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_NUMBER));
            String planName = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_PLAN_NAME));
            String moodBefore = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_BEFORE));
            String sessionLength = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_SESSION_LENGTH));
            String moodAfter = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_AFTER));
            String moodDesc = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_MOOD_DESC));
            String calmSeconds = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_CALM_SECONDS));
            String calmPoints = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_CALM_POINTS));
            String recoveries = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_RECOVERIES));
            String heartRate = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Profile.COLUMN_NAME_HEART_RATE));


            Profile profile = new Profile(itemId, planName, sessionNumber, moodBefore, sessionLength, moodAfter, moodDesc, calmSeconds, calmPoints, recoveries, heartRate);
            profileList.add(profile);
            Log.i("DB", "planName:" + planName);
            Log.i("DB", "sessionNumber:" + sessionNumber);
            Log.i("DB", "moodBEfore:" + moodBefore);
            Log.i("DB", "sessionLength:" + sessionLength);
            Log.i("DB", "moodAfter:" + moodAfter);
            Log.i("DB", "moodDesc:" + moodDesc);
            Log.i("DB", "calmSe:" + calmSeconds);
            Log.i("DB", "calm poi:" + calmPoints);
            Log.i("DB", "rec:" + recoveries);
            Log.i("DB", "heart:" + heartRate);

        }
        cursor.close();
        return profileList;
    }

    public Community fetchCommunityFromDB() {
        SQLiteDatabase db = getWritableDatabase();
        String[] projection = {
                EEGContract.EEGo_Community._ID,
                EEGContract.EEGo_Community.COLUMN_NAME_TOTAL_POINTS,
                EEGContract.EEGo_Community.COLUMN_NAME_CALM_POINTS
        };


        Cursor cursor = db.query(
                EEGContract.EEGo_Community.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<Community> cList = new ArrayList<>();


        while (cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Community._ID));
            String totalPoints = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Community.COLUMN_NAME_TOTAL_POINTS));
            String calmPoints = cursor.getString(
                    cursor.getColumnIndexOrThrow(EEGContract.EEGo_Community.COLUMN_NAME_CALM_POINTS));

            Community community = new Community(itemId, totalPoints, calmPoints);
            cList.add(community);
            Log.i("DB", "totalPoints:" + totalPoints);
            Log.i("DB", "calmPoints:" + calmPoints);

        }
        cursor.close();
        if(cList.isEmpty()) {
            Community community = new Community(5, "50000", "3000");
        }
        return cList.get(0);
    }
}
