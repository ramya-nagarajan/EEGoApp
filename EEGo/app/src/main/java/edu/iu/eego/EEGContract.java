package edu.iu.eego;

import android.provider.BaseColumns;

/**
 * Created by Ramya on 11/29/2017.
 */

public final class EEGContract {

    private EEGContract(){};

    /* Inner class that defines the table contents */
    public static class EEGo_Profile implements BaseColumns {
        public static final String TABLE_NAME = "EEGo_Profile";
        public static final String COLUMN_NAME_PLAN_NAME = "planName";
        public static final String COLUMN_NAME_SESSION_NUMBER = "sessionNumber";
        public static final String COLUMN_NAME_MOOD_BEFORE = "moodBefore";
        public static final String COLUMN_NAME_SESSION_LENGTH = "sessionLength";
        public static final String COLUMN_NAME_MOOD_AFTER = "moodAfter";
        public static final String COLUMN_NAME_MOOD_DESC = "moodDesc";
        public static final String COLUMN_NAME_CALM_SECONDS = "calmSeconds";
        public static final String COLUMN_NAME_CALM_POINTS = "calmPoints";
        public static final String COLUMN_NAME_RECOVERIES = "recoveries";
        public static final String COLUMN_NAME_HEART_RATE = "heartRate";
    }

    /* Inner class that defines the table contents */
    public static class EEGo_Community implements BaseColumns {
        public static final String TABLE_NAME = "EEGo_Community";
        public static final String COLUMN_NAME_TOTAL_POINTS = "totalPoints";
        public static final String COLUMN_NAME_CALM_POINTS = "totalCalmPoints";
    }
}
