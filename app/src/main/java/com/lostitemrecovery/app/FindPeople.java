package com.lostitemrecovery.app;

public class FindPeople
{
    public  String profileimage, fullname, lastname, status;

    public  FindPeople()
    {

    }

    public FindPeople(String profileimage, String fullname, String lastname, String status) {
        this.profileimage = profileimage;
        this.fullname = fullname;
        this.status = status;
        this.lastname = lastname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
