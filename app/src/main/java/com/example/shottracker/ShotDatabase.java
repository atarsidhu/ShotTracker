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
    static boolean saved = false;

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

        /*File file = new File("savedShots");
        try {
            FileOutputStream fos = new FileOutputStream(new File(context.getFilesDir(), "tours"));
            //FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(shots);
            o.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    protected static ArrayList<Shot> loadShots(String key){
        /*File file = new File("savedShots");
        try {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(f);
            HashMap<String, ArrayList<Shot>> fileObj2 = (HashMap<String, ArrayList<Shot>>) o.readObject();
            o.close();
            return fileObj2.get(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/


        return null;
    }

    protected static ArrayList<Shot> getValue(String key){
        ArrayList<Shot> temp = new ArrayList<>();

        if(!shots.isEmpty())
                return shots.get(key);

        return temp;
    }
}
