package com.example.shottracker;

import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private String club, ballFlight;
    private double yards;
    private int countingYards = 0;
    private int countingTenthOfAYard = 0;

    private DatabaseHelper databaseHelper;
    private Cursor cursor;
    //private TextView tvDB;

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

        //Database
        databaseHelper = new DatabaseHelper(getContext());
        //tvDB = view.findViewById(R.id.tvDB);

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //getAndSetGPSLocation();
                displayYardage();

                /*//Below is just for debugging purposes
                Cursor data = databaseHelper.getData();
                HashMap<String, ArrayList<Shot>> shots = new HashMap<>();
                tvDB.setText("");

                if(data.getCount() == 0)
                    Toast.makeText(getContext(), "No shots saved!", Toast.LENGTH_SHORT).show();
                else{
                    while(data.moveToNext()){
                        shots.putIfAbsent(data.getString(1), new ArrayList<>());
                        shots.get(data.getString(1)).add(new Shot(data.getString(1), data.getFloat(2), data.getString(3), data.getString(4)));
                        tvDB.append("\nID: " + data.getString(0));
                    }
                }
                tvDB.append(shots.toString());
                //Debugging purposes above*/
            }
        });

        btnSaveShot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(club.equals("Select Club:"))
                    Toast.makeText(getContext(), "Please select a club.", Toast.LENGTH_LONG).show();
                else {
                    if(ballFlight == null)
                        ballFlight = "Straight";

                    addData(club, 213.11, "Straight", tvNotes.getText().toString());
                    addData(club, 228.9, "Draw", tvNotes.getText().toString());
                    addData(club, 230.0, "Hook", tvNotes.getText().toString());
                    addData(club, 230.1, "Slice", tvNotes.getText().toString());
                    addData(club, 220.8, "Fade", tvNotes.getText().toString());
                    addData(club, 230.9, "Draw", tvNotes.getText().toString());
                    addData(club, 223.9, "Slice", tvNotes.getText().toString());

                }
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
                tvDistance.setText("0 yards");
            } else{
                endingLatitude = location.getLatitude();
                endingLongitude = location.getLongitude();
                btnStartStop.setText(R.string.startTracker);
                tvCoordinates.append(endingLatitude + " " + endingLongitude + "\n");
            }

            if(endingLatitude != 0.0 && endingLongitude != 0.0) {
                yards = calculateDistance(startingLatitude, startingLongitude, endingLatitude, endingLongitude);
                tvDistance.setText(String.format("%.1f \nyards", yards));
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

    private void displayYardage(){
        yards = 200.8;
        String[] decimal = String.valueOf(yards).split("\\.");
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(countingYards < (yards * .97)) {
                    countingYards++;

                    if(countingTenthOfAYard <= 8)
                        countingTenthOfAYard++;
                    else
                        countingTenthOfAYard = 0;

                    handler.postDelayed(this, 10);
                } else if(countingYards < yards - 1){
                    if(countingTenthOfAYard <= 8)
                        countingTenthOfAYard++;
                    else {
                        countingYards++;
                        countingTenthOfAYard = 0;
                    }

                    handler.postDelayed(this, 100);
                } else if(countingYards == Math.floor(yards)){
                    handler.postDelayed(this, 250);
                    int firstDecimalNumber = Integer.parseInt(String.valueOf(decimal[1].charAt(0)));

                    if(countingTenthOfAYard < firstDecimalNumber)
                        countingTenthOfAYard++;
                    else {
                        handler.removeCallbacksAndMessages(null);
                    }

                } else
                    handler.removeCallbacksAndMessages(null);

                tvDistance.setText(countingYards + "." + countingTenthOfAYard + "\nyards");
            }
        };

        handler.post(runnable);
        countingYards = 0;
        countingTenthOfAYard = 0;
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
