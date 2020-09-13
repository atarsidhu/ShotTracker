package com.example.shottracker;

import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class ShotDatabase {
    private static final ShotDatabase shotDatabase = new ShotDatabase();
    private static HashMap<String, ArrayList<Shot>> shots;
    private static Context context;

    private ShotDatabase(){
        shots = new HashMap<>();
    }

    public static ShotDatabase getInstance(){
        return shotDatabase;
    }

    protected static HashMap<String, ArrayList<Shot>> getMap(){
        return shots;
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

    protected static Shot getLastShot(String key){
        Shot temp = new Shot();

        if(!shots.get(key).isEmpty())
            return shots.get(key).get(shots.get(key).size() - 1);

        return temp;
    }

    protected static void deleteAllShots(){
        shots.clear();
    }
}
