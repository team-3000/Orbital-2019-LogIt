package com.team3000.logit;

import android.os.Parcel;
import android.os.Parcelable;

public class EntryPair implements Parcelable {
    private Entry entry;
    private String entryId;

    public EntryPair(Entry e,  String s) {
        this.entry = e;
        this.entryId = s;
    }

    public Entry getEntry() {
        return entry;
    }

    public String getEntryId() {
        return entryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(entry, 0);
        dest.writeString(entryId);
    }

    public static final Parcelable.Creator<EntryPair> CREATOR
            = new Parcelable.Creator<EntryPair>() {
        @Override
        public EntryPair createFromParcel(Parcel source) {
            return new EntryPair(source.readParcelable(getClass().getClassLoader())
                , source.readString());
        }

        @Override
        public EntryPair[] newArray(int size) {
            return new EntryPair[0];
        }
    };
}
