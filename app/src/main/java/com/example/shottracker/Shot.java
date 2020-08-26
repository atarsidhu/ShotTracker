package com.example.shottracker;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Shot {

    private String club, ballFlight, notes;
    double distance;
    private HashMap<String, ArrayList<Shot>> setOfShots;

    public Shot(String club, double distance, String ballFlight, String notes){
        this.club = club;
        this.ballFlight = ballFlight;
        this.distance = distance;
        this.notes = notes;
        setOfShots = new HashMap<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addShot(String club, Shot shot){
        setOfShots.putIfAbsent(club, new ArrayList());
        setOfShots.get(club).add(shot);
    }

    public ArrayList<Shot> getShot(String club){
        ArrayList<Shot> temp = new ArrayList();

        if(setOfShots.containsKey(club)){
            if(setOfShots.get(club).isEmpty())
                return temp;

            for(Shot s: setOfShots.get(club))
                temp.add(s);
        }

        return temp;
    }

    public String getClub() {
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
}
