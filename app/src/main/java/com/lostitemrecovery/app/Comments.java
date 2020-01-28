package com.lostitemrecovery.app;

public class Comments
{
    public String comment, date, time, username, lastname;


    public Comments()
    {

    }

    public Comments(String comment, String date, String time, String username, String lastname) {
        this.comment = comment;
        this.date = date;
        this.time = time;
        this.username = username;
        this.lastname = lastname;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
