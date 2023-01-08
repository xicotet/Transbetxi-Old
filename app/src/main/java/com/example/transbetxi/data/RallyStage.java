package com.example.transbetxi.data;

import android.os.Parcel;
import android.os.Parcelable;

public class RallyStage  {
    private String name;
    private String id;
    private String startTime;
    private String length;

    public RallyStage(String name, String startTime, String id, String length) {
        this.name = name;
        this.id = id;
        this.startTime = startTime;
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public String getLength() {
        return length;
    }

    public String getId() {
        return id;
    }

    public String getStartTime() {
        return startTime;
    }



}
