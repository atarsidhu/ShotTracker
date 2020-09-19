package com.example.shottracker;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class TrackShotFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button btnStartStop, btnSaveShot;
    private TextView tvCoordinates, tvDistance;
    private EditText tvNotes;
    private double startingLatitude = 0.0;
    private double startingLongitude = 0.0;
    private double endingLatitude = 0.0;
    private double endingLongitude = 0.0;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private String club;
    private String ballFlight;
    private double yards;
    ShotDatabase shotDatabase = ShotDatabase.getInstance();
    private String PATH_TO_DATA;
    private File savedShotsFile;
    private String fileName;

    private static final String TAG = "TrackShotFragment";
    DatabaseHelper databaseHelper;
    private TextView tvDB;

    public TrackShotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_shot, container, false);
        btnStartStop = view.findViewById(R.id.btnStartStop);
        btnSaveShot = view.findViewById(R.id.btnSaveShot);
        tvCoordinates = view.findViewById(R.id.tvCoordinates);
        tvNotes = view.findViewById(R.id.tvNotes);
        tvDistance = view.findViewById(R.id.tvDistance);
        spinner = view.findViewById(R.id.spinner_clubs);
        radioGroup = view.findViewById(R.id.radioGroup);

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //File
        fileName = "savedShots.txt";
        PATH_TO_DATA = getContext().getFilesDir() + "/" + fileName;
        savedShotsFile = new File(PATH_TO_DATA);

        //Database
        databaseHelper = new DatabaseHelper(getContext());
        tvDB = view.findViewById(R.id.tvDB);

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getAndSetGPSLocation();
                databaseHelper.deleteData();
            }
        });

        btnSaveShot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //((MainActivity)getActivity()).selectFragment(1); // Switch tab to View Shots
                /*if (club.contains("Select Club:"))
                    Toast.makeText(getContext(), "Please select a club.", Toast.LENGTH_LONG).show();
                else {
                    if(ballFlight == null)
                        ballFlight = "Straight";

                    ShotDatabase.addShot(club, new Shot(club, yards, ballFlight, tvNotes.getText().toString()));
                    Toast.makeText(getContext(), "Shot Saved!", Toast.LENGTH_SHORT).show();

                    try {
                        saveShotToFileAsString();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

                //DB
                addData(club, yards, ballFlight, tvNotes.getText().toString());
                Cursor data = databaseHelper.getAllData();
                /*ArrayList<String> listData = new ArrayList<>();
                while(data.moveToNext()){
                    listData.add(data.getString(1));
                }
                tvDB.setText(listData.toString());*/

                HashMap<Integer, ArrayList<Shot>> shots = new HashMap<>();

                if(data.getCount() == 0)
                    Toast.makeText(getContext(), "No shots saved!", Toast.LENGTH_SHORT).show();
                else{
                    while(data.moveToNext()){
                        shots.putIfAbsent(data.getInt(0), new ArrayList<>());
                        shots.get(data.getInt(0)).add(new Shot(data.getString(1), data.getFloat(2), data.getString(3), data.getString(4)));
                    }
                }

                tvDB.setText(shots.toString());
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rbDraw:
                        ballFlight = "Draw";
                        break;
                    case R.id.rbFade:
                        ballFlight = "Fade";
                        break;
                    case R.id.rbStraight:
                        ballFlight = "Straight";
                        break;
                    case R.id.rbHook:
                        ballFlight = "Hook";
                        break;
                    case R.id.rbSlice:
                        ballFlight = "Slice";
                        break;
                }
            }
        });

        return view;
    }

    private void addData(String club, Double distance, String ballFlight, String notes){
        boolean insertData = databaseHelper.addData(club, distance, ballFlight, notes);

        if(insertData)
            Toast.makeText(getContext(), "Shot saved!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Shot not saved", Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void saveShotToFileAsString() throws IOException {
        if(savedShotsFile.length() == 0) {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(ShotDatabase.getLastShot(club).toString());
            outputStreamWriter.close();
        } else{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getContext().openFileOutput(fileName, Context.MODE_APPEND));
            outputStreamWriter.append("\n" + ShotDatabase.getLastShot(club));
            outputStreamWriter.close();
        }
    }

    private void getAndSetGPSLocation(){
        GPSTracker gpsTracker = new GPSTracker(getContext());
        Location location = gpsTracker.getLocation();

        if(location != null){
            if(btnStartStop.getText().equals("Start Distance Tracker")) {
                startingLatitude = location.getLatitude();
                startingLongitude = location.getLongitude();
                endingLongitude = 0.0;
                endingLatitude = 0.0;
                btnStartStop.setText(R.string.stopTracker);
                tvCoordinates.setText(startingLatitude + " " + startingLongitude + "\n");
            } else{
                endingLatitude = location.getLatitude();
                endingLongitude = location.getLongitude();
                btnStartStop.setText(R.string.startTracker);
                tvCoordinates.append(endingLatitude + " " + endingLongitude + "\n");
            }

            if(endingLatitude != 0.0 && endingLongitude != 0.0) {
                yards = calculateDistance(startingLatitude, startingLongitude, endingLatitude, endingLongitude);
                tvDistance.setText(String.format("%.1f yards", yards));
            }
        }
    }

    private double calculateDistance(double startingLatitude, double startingLongitude, double endingLatitude, double endingLongitude){
        double earthRadius = 6371;

        double diffLat = Math.toRadians(endingLatitude - startingLatitude);
        double diffLong = Math.toRadians(endingLongitude - startingLongitude);

        startingLatitude = Math.toRadians(startingLatitude);
        endingLatitude = Math.toRadians(endingLatitude);

        double a = Math.sin(diffLat/2) * Math.sin(diffLat/2) +
                Math.sin(diffLong/2) * Math.sin(diffLong/2) * Math.cos(startingLatitude) * Math.cos(endingLatitude);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        //Convert to yards and return
        return earthRadius * 1093.613 * c;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
