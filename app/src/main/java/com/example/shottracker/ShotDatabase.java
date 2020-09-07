package com.example.shottracker;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;

public class ShotDatabase {
    private static final ShotDatabase shotDatabase = new ShotDatabase();
    private static HashMap<String, ArrayList<Shot>> shots;

    private ShotDatabase(){
        shots = new HashMap<>();
    }

    public static ShotDatabase getInstance(){
        return shotDatabase;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected static void addShot(String club, Shot shot){
        shots.putIfAbsent(club, new ArrayList<>());
        shots.get(club).add(shot);
    }

    protected static ArrayList<Shot> getValue(String key){
        ArrayList<Shot> temp = new ArrayList<>();

        if(!shots.isEmpty())
            return shots.get(key);

        return temp;
    }
}
