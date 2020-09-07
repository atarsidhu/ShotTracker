package com.example.shottracker;

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
    private RadioButton radioButton;
    private String club;
    private String ballFlight;
    private double yards;
    ShotDatabase shotDatabase = ShotDatabase.getInstance();

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        btnStartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSetGPSLocation();
            }
        });

        btnSaveShot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //((MainActivity)getActivity()).selectFragment(1); // Switch tab to View Shots
                ShotDatabase.addShot(club, new Shot(yards, ballFlight, tvNotes.getText().toString()));
                ShotDatabase.getValue(club);
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
                yards = calculateDistance (startingLongitude, startingLatitude, endingLongitude, endingLatitude);
                //double yards = calculateDistance (49.2827, -123.1207, 51.5074, -0.1278); van and london coordinates
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

/*    private String onRadioButtonClicked(View view){
        //Check which radiobutton is selected
        int radioID = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(radioID);
        return radioButton.toString();
    }*/

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
