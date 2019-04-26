package com.example.prem.issuevote;

import android.content.Context;
import android.content.SharedPreferences;

public class IssueDataStore {

    private static final String ISSUE_VOTE = "com.example.prem.issuevote";
    private static final String ISSUE_VOTE_COUNT = "ISSUE_VOTE_COUNT";

    public static void registerAlarmRun(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISSUE_VOTE, Context.MODE_PRIVATE);
        int currentCount = sharedPreferences.getInt(ISSUE_VOTE_COUNT, 0);
        sharedPreferences.edit()
                .putInt(ISSUE_VOTE_COUNT, currentCount + 1)
                .apply();
    }

    public static int getAlarmRunCount(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ISSUE_VOTE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(ISSUE_VOTE_COUNT, 0);
    }
}

