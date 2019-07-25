package com.team3000.logit;

import android.os.Bundle;

public interface EntryListener {
    interface OnUpdateListener {
        void onUpdate(String entryId, Entry updatedEntry);
    }

    interface OnDateChangeListener {
        void notifyMonthAndOrYearChanged(Bundle data);
    }
}
