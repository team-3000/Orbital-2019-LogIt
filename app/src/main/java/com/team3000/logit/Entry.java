package com.team3000.logit;

public class Entry implements Comparable<Entry> {
    private String id;
    private String type;
    private String title;
    private String date;
    private String time;
    private String desc;
    private int year;
    private String month;

    public Entry() {
    }

    public Entry(String id, String type, String title, String date, String time, String desc) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.date = date;
        this.time = time;
        this.desc = desc;
        String[] dateArr = date.split(" ");
        this.year = Integer.parseInt(dateArr[2]);
        this.month = dateArr[1];
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
        return year;
    }

    public String getMonth() {
        return month;
    }

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

        int thisDay = Integer.parseInt(thisDateArr[0]);
        int otherDay = Integer.parseInt(otherDateArr[0]);
        if (thisDay == otherDay) {
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
    }
}
