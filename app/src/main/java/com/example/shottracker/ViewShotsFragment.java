package com.example.shottracker;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private TextView tvShowShot;
    private Spinner spinnerClubs;
    private String club;
    TrackShotFragment trackShotFragment = new TrackShotFragment();
    ShotDatabase shotDatabase = ShotDatabase.getInstance();

    public ViewShotsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_shots, container, false);
        tvShowShot = view.findViewById(R.id.tvShowShot);
        spinnerClubs = view.findViewById(R.id.spinner_clubs_graph);

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();

        if(ShotDatabase.getValue(club) != null && loadShots(club) != null)
            //tvShowShot.setText(ShotDatabase.getValue(club).toString());
            tvShowShot.setText(loadShots(club).toString());
            //tvShowShot.setText(ShotDatabase.loadShots(club).toString());
        else
            tvShowShot.setText("No " + club + " shots saved");
    }

    private ArrayList<Shot> loadShots(String key){
        ArrayList<Shot> temp = new ArrayList<>();

        File file = new File(getContext().getFilesDir() + "/savedShots");
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
        }

        return temp;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
