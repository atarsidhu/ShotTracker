package com.example.shottracker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Shot implements Serializable {

    private String ballFlight, notes;
    double distance;
    private HashMap<String, ArrayList<Shot>> setOfShots = new HashMap<>();

    public Shot(double distance, String ballFlight, String notes){
        this.ballFlight = ballFlight;
        this.distance = distance;
        this.notes = notes;
    }

    public Shot(){}

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
        return getDistance() + " " + getBallFlight() + " " + getNotes();
    }
}
