package com.team3000.logit;

public interface EntryListener {
    interface OnUpdateListener {
        void onUpdate(int entryPosition, Entry updatedEntry);
    }
}
