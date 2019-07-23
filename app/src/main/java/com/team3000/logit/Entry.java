package com.team3000.logit;

import android.os.Parcel;
import android.os.Parcelable;

public class Entry implements Comparable<Entry>, Parcelable {
    private String id;
    private String type;
    private String title;
    private String date;
    private String time;
    private String desc;

    public Entry() {
        // Empty constructor needed for Firestore
    }

    public Entry(String id, String type, String title, String date, String time, String desc) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.date = date;
        this.time = time;
        this.desc = desc;
        String[] dateArr = date.split(" ");
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }

    public int getYear() {
        return Integer.parseInt(date.split(" ")[2]);
    }

    public String getMonth() {
        return date.split(" ")[1];
    }

    public void setId(String id) {
        this.id = id;
    }

    // Sort entry according to its date & time in ascending order
    @Override
    public int compareTo(Entry other) {
        String[] thisDateArr = this.date.split(" ");
        String[] otherDateArr = other.date.split(" ");
        String[] thisTimeSplit = this.time.split(" ");
        String[] otherTimeSplit = other.time.split(" ");
        String thisMeridien = thisTimeSplit[1];
        String otherMeridien = otherTimeSplit[1];
        String[] thisTimeNums = thisTimeSplit[0].split(":");
        String[] otherTimeNums = otherTimeSplit[0].split(":");

        int thisYear = Integer.parseInt(thisDateArr[2]);
        int thisMonth = convertToInt(thisDateArr[1]);
        int thisDay = Integer.parseInt(thisDateArr[0]);

        int otherYear = Integer.parseInt(otherDateArr[2]);
        int otherMonth = convertToInt(otherDateArr[1]);
        int otherDay = Integer.parseInt(otherDateArr[0]);

        // Year
        if (thisYear == otherYear) {
            // Month
            if (thisMonth == otherMonth) {
                // Day
                if (thisDay == otherDay) {
                    // Time
                    if (thisMeridien.equals(otherMeridien)) {
                        int thisHour = Integer.parseInt(thisTimeNums[0]);
                        int otherHour = Integer.parseInt(otherTimeNums[0]);
                        if (thisHour == otherHour) {
                            return Integer.parseInt(thisTimeNums[1]) - Integer.parseInt(otherTimeNums[1]);
                        } else {
                            return thisHour - otherHour;
                        }
                    } else {
                        return ("am".equals(thisMeridien) && "pm".equals(otherMeridien)) ? -1 : 1;
                    }
                } else {
                    return thisDay - otherDay;
                }
            } else {
                return thisMonth - otherMonth;
            }
        } else {
            return thisYear - otherYear;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(type);
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(desc);
    }

    public static final Parcelable.Creator<Entry> CREATOR
            = new Parcelable.Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel source) {
            String id = source.readString();
            String type = source.readString();
            String title = source.readString();
            String date = source.readString();
            String time = source.readString();
            String desc = source.readString();

            return new Entry(id, type, title, date, time, desc);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    // Convert a month string into its corresponding int
    private int convertToInt(String month) {
        int result = "Jan".equals(month) ? 1 :
                     "Feb".equals(month) ? 2 :
                     "Mar".equals(month) ? 3 :
                     "Apr".equals(month) ? 4 :
                     "May".equals(month) ? 5 :
                     "Jun".equals(month) ? 6 :
                     "Jul".equals(month) ? 7 :
                     "Aug".equals(month) ? 8 :
                     "Sep".equals(month) ? 9 :
                     "Oct".equals(month) ? 10 :
                     "Nov".equals(month) ? 11 : 12;

        return result;
    }
}
