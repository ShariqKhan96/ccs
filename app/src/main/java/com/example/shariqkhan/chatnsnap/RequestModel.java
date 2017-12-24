package com.example.shariqkhan.chatnsnap;

/**
 * Created by ShariqKhan on 8/11/2017.
 */

public class RequestModel {


    String req_type;
    String imageurl;

    public RequestModel() {
    }

    public String getReq_type() {
        return req_type;
    }

    public void setReq_type(String req_type) {
        this.req_type = req_type;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public RequestModel(String name, String imageurl) {
        this.req_type = name;
        this.imageurl = imageurl;
    }
}
