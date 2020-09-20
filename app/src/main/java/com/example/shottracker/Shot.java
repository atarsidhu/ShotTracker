package com.example.shottracker;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Shot implements Serializable {

    private String club, ballFlight, notes;
    private double distance;

    public Shot(String club, double distance, String ballFlight, String notes){
        this.club = club;
        this.ballFlight = ballFlight;
        this.distance = distance;
        this.notes = notes;
    }

    public Shot(){}

    public String getClub(){
        return club;
    }

    public double getDistance() {
        return distance;
    }

    public String getBallFlight() {
        return ballFlight;
    }

    public String getNotes() {
        return notes;
    }

    public String toString(){
        return club + " " + String.format("%.1f", distance) + " " + ballFlight + " " + notes;
    }
}
