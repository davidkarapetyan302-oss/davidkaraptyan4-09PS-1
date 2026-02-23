package com.example.cpszadanie;

public class TimeRecord {
    private int id;
    private int userId;
    private String date;
    private String timeIn;
    private String timeOut;
    private Double hours;

    public TimeRecord(int id, int userId, String date, String timeIn, String timeOut, Double hours) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.hours = hours;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTimeIn() { return timeIn; }
    public void setTimeIn(String timeIn) { this.timeIn = timeIn; }

    public String getTimeOut() { return timeOut; }
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }

    public Double getHours() { return hours; }
    public void setHours(Double hours) { this.hours = hours; }
}