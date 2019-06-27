package com.team3000.logit;

public interface EntryListener {
    interface OnDestroyListener {
        void onDestroy(int entryPosition);
    }

    interface OnUpdateListener {
        void onUpdate(int entryPosition, Entry updatedEntry);
    }
}
