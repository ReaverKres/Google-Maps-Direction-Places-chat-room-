package com.test.googlemaps2019v2;

import android.app.Application;

import com.test.googlemaps2019v2.models.User;


public class UserClient extends Application {

    private User user = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
