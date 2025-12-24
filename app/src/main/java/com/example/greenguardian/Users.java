package com.example.greenguardian;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Users {
    private String name;
    private String password;
    private Bitmap profilePic; // Changed to Bitmap

    // Constructor that takes username, password, and profilePic as parameters
    public Users(String name, String password, Bitmap profilePic) {
        this.name = name;
        this.password = password;
        this.profilePic = profilePic;
    }

    // Getter and setter methods for each property
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }
}



