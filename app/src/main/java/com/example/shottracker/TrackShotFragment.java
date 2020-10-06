package com.example.shottracker;

import android.database.Cursor;
import android.graphics.Color;
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



public class TrackShotFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Button btnStartStop, btnSaveShot;
    private TextView tvDistance;
    private EditText tvNotes;
    private double startingLatitude = 0.0;
    private double startingLongitude = 0.0;
    private double endingLatitude = 0.0;
    private double endingLongitude = 0.0;
    private String club, ballFlight;
    private double yards;
    private int countingYards = 0;
    private int countingTenthOfAYard = 0;
    private int avgDistance, red, green;

    private DatabaseHelper databaseHelper;

    public TrackShotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_shot, container, false);
        btnStartStop = view.findViewById(R.id.btnStartStop);
        btnSaveShot = view.findViewById(R.id.btnSaveShot);
        tvNotes = view.findViewById(R.id.tvNotes);
        tvDistance = view.findViewById(R.id.tvDistance);
        Spinner spinner = view.findViewById(R.id.spinner_clubs);
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);

        //Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Database
        databaseHelper = new DatabaseHelper(getContext());

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if(club.equals("Select Club:"))
                    Toast.makeText(getContext(), "Please select a club.", Toast.LENGTH_LONG).show();
                else
                    getAndSetGPSLocation();
                /*if(club.equals("Select Club:"))
                    Toast.makeText(getContext(), "Please select a club.", Toast.LENGTH_LONG).show();
                else
                    displayYardage();*/
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

                    addData(club, yards, ballFlight, tvNotes.getText().toString());

                    /*Add shots to test components
                    addData(club, 213.11, "Straight", tvNotes.getText().toString());
                    addData(club, 228.9, "Draw", tvNotes.getText().toString());
                    addData(club, 230.0, "Hook", tvNotes.getText().toString());
                    addData(club, 230.1, "Slice", tvNotes.getText().toString());
                    addData(club, 220.8, "Fade", tvNotes.getText().toString());
                    addData(club, 230.9, "Draw", tvNotes.getText().toString());
                    addData(club, 223.9, "Slice", tvNotes.getText().toString());*/

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

    /**
     * Adds the shot with the specified parameters to the database
     * @param club the club used
     * @param distance how far the ball goes
     * @param ballFlight the flight of the ball
     * @param notes additional notes
     */
    private void addData(String club, Double distance, String ballFlight, String notes){
        boolean insertData = databaseHelper.addData(club, distance, ballFlight, notes);

        if(insertData)
            Toast.makeText(getContext(), "Shot saved!", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Shot not saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Gets the current GPS location of the user
     */
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
                tvDistance.setText("0 yards");
            } else{
                endingLatitude = location.getLatitude();
                endingLongitude = location.getLongitude();
                btnStartStop.setText(R.string.startTracker);
            }

            if(endingLatitude != 0.0 && endingLongitude != 0.0) {
                yards = calculateDistance(startingLatitude, startingLongitude, endingLatitude, endingLongitude);
                //tvDistance.setText(String.format("%.1f \nyards", yards));
                displayYardage();
            }
        }
    }

    /**
     * Calculates the distance between the first and second GPS locations that the user requested and returns the
     * answer as the number of yards the ball has travelled.
     * @param startingLatitude
     * @param startingLongitude
     * @param endingLatitude
     * @param endingLongitude
     * @return
     */
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

    /**
     * Displays the yardage on the screen
     */
    private void displayYardage(){
        yards = 260.3;
        red = 255;
        green = 0;
        String[] decimal = String.valueOf(yards).split("\\.");
        computeAverageDistancePerClub();

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


                //Ask user for avg distance, then get that distance and replace it with yards below
                if(countingYards < avgDistance * .3) {
                    if(green < 64)
                        green++;
                } else if(countingYards >= avgDistance * .3 && countingYards < avgDistance * .5) {
                    if(green < 64)
                        green = 64;

                    if(green < 128)
                        green++;
                } else if(countingYards >= avgDistance * .5 && countingYards < avgDistance * .7) {
                    if(green < 128)
                        green = 128;

                    if(green < 191)
                        green++;

                } else if(countingYards >= avgDistance * .7 && countingYards < avgDistance * .9) {
                    if(green < 255)
                        green++;

                    red -= 1;
                } else if(countingYards >= avgDistance * .9 && countingYards < avgDistance) {
                    if(red > 160)
                        red = 160;
                    else if(red > 1)
                        red -= 1;

                    if(green < 255)
                        green++;
                } else if(countingYards >= avgDistance && countingYards < avgDistance * 1.05) {
                    if(red > 110)
                        red = 110;
                    else if(red > 5)
                        red -= 5;

                    if(green < 255)
                        green++;

                } else if(countingYards >= avgDistance * 1.05) {
                    red = 64;
                    green = 255;
                }

                tvDistance.setTextColor(Color.rgb(red, green, 0));
                tvDistance.setText(countingYards + "." + countingTenthOfAYard + " yards");
            }
        };

        handler.post(runnable);
        countingYards = 0;
        countingTenthOfAYard = 0;
    }

    /**
     * Calculates the average distance the ball has travelled with the selected club.
     */
    public void computeAverageDistancePerClub(){
        avgDistance = 0;
        int num = 0;
        Cursor cursor = databaseHelper.getData();

        while(cursor.moveToNext()){
            if(cursor.getString(1).contains(club)){
                avgDistance += cursor.getFloat(2);
                num++;
            }
        }

        if(avgDistance != 0)
            avgDistance /= num;
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
