package com.example.hospital;

public class AppointmentDoctor {
    private String userName;
    private String date;
    private String time;


    public AppointmentDoctor() {
    }

    public AppointmentDoctor(String userName, String date, String time) {
        this.userName = userName;
        this.date = date;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String doctorName) {
        this.userName = doctorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
