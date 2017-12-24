package com.example.shariqkhan.chatnsnap;

/**
 * Created by ShariqKhan on 7/14/2017.
 */

public class UsersModelClass {
    public String name;
    private String status;
    private String image;

    public UsersModelClass(String name, String status, String image) {
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public UsersModelClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
