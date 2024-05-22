package com.ssg.dsilbackend.dto;



public enum AvailableTimeTable {
    AFTERNOON_12("12:00"),
    AFTERNOON_1("13:00"),
    AFTERNOON_2("14:00"),
    AFTERNOON_3("15:00"),
    AFTERNOON_4("16:00"),
    AFTERNOON_5("17:00"),
    AFTERNOON_6("18:00"),
    AFTERNOON_7("19:00"),
    AFTERNOON_8("20:00"),
    AFTERNOON_9("21:00");

    private final String time;

    AvailableTimeTable(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return time;
    }
}