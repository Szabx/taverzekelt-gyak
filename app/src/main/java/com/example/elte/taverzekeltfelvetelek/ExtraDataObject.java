package com.example.elte.taverzekeltfelvetelek;

import java.util.Date;

/**
 * Created by root on 15.06.2015.
 */
public class ExtraDataObject {
    int id;

    public ExtraDataObject() {
    }

    public ExtraDataObject(int id, float lat, float lng, String message, Date created) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.message = message;
        this.created = created;
    }

    float lat, lng;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    String message;
    Date created;
}
