package com.example.shottracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//TODO: Implement graph

public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private TextView tvShowShot;
    private Spinner spinnerClubs;
    private String club;
    private Button btnDelete;
    private boolean deleted;
    private boolean onlyGettingClubsUsed;
    private boolean gettingDistancesPerClub;
    private String fileName;
    private String clubsUsed;
    private String yardagesPerClub;
    private ArrayList<Integer> xAxis;
    private ArrayList<Entry> yAxis;
    private HashMap<String, ArrayList<Float>> distance;

    TrackShotFragment trackShotFragment = new TrackShotFragment();
    ShotDatabase shotDatabase = ShotDatabase.getInstance();
    File dir;
    File file;
    LineChart lineChart;

    private static final String TAG = "ViewShotsFragment";

    public ViewShotsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_shots, container, false);
        tvShowShot = view.findViewById(R.id.tvShowShot);
        spinnerClubs = view.findViewById(R.id.spinner_clubs_graph);
        btnDelete = view.findViewById(R.id.btnDelete);
        fileName = "savedShots.txt";
        dir = getContext().getFilesDir();
        file = new File(dir, fileName);
        distance = new HashMap<>();

        lineChart = view.findViewById(R.id.lineChart);
        //lineChart.setOnChartGestureListener(this);
        //lineChart.setOnChartValueSelectedListener(this);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

        //Chart configurations
        xAxis = new ArrayList<>();
        yAxis = new ArrayList<>();


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(isVisible()){
            spinnerClubs.setSelection(0);
        }
    }

    private void deleteData() {
        deleted = file.delete();
        tvShowShot.setText(getString(R.string.noShotsSaved, club));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Reset values to prevent overwriting
        distance.clear();
        // For some reason, changing this to if(!yAxis.isEmpty()) and deleting lineChart.clear()
        // prevents chart from auto displaying after new club selected
        if(lineChart.getData() != null) {
            lineChart.clear();
            yAxis.clear();
        }

        club = parent.getItemAtPosition(position).toString();

        onlyGettingClubsUsed = true;
        clubsUsed = readFromFile(getContext());
        onlyGettingClubsUsed = false;

        /*gettingDistancesPerClub = true;
        yardagesPerClub = readFromFile(getContext());
        gettingDistancesPerClub = false;*/

        if(clubsUsed.contains(club)){
            tvShowShot.setText(readFromFile(getContext()) + "\nDistances for " + club + ": " + distance.get(club));

            //Set Chart
            for(int i = 0; i < distance.get(club).size(); i++){
                yAxis.add(new Entry(i+1, distance.get(club).get(i)));
            }

            LineDataSet set1 = new LineDataSet(yAxis, "Yards");
            set1.setFillAlpha(110);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData lineData = new LineData(dataSets);
            lineChart.setData(lineData);
        }else {
            if(club.contains("Select Club:"))
                tvShowShot.setText(R.string.selectClub);
            else
                tvShowShot.setText(getString(R.string.noShotsSaved, club));
        }

        /*if(clubsUsed.contains(club)) {
            tvShowShot.setText(readFromFile(getContext()) + "\nDistances for " + club + ": " + yardagesPerClub);
            String[] numOfYardagesPerClub = yardagesPerClub.split(" ");
            for(int i = 0; i < numOfYardagesPerClub.length; i++){
                //yAxis.add(new Entry(i, Integer.parseInt(numOfYardagesPerClub[i])));
                //xAxis.add(i);
            }
        } else {
            if(club.contains("Select Club:"))
                tvShowShot.setText(R.string.selectClub);
            else
                tvShowShot.setText(getString(R.string.noShotsSaved, club));
        }*/



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String readFromFile(Context context) {
        String shotsSavedFromFile = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String shotReadFromFile;
                StringBuilder stringBuilderShotsRead = new StringBuilder();
                StringBuilder previouslyUsedClubs = new StringBuilder();

                while ((shotReadFromFile = bufferedReader.readLine()) != null) {
                    String[] split = shotReadFromFile.split(" ");
                    if(onlyGettingClubsUsed) {
                        if(!previouslyUsedClubs.toString().contains(split[0]))
                            previouslyUsedClubs.append(split[0]).append(" ");
                    } else{
                        if(shotReadFromFile.contains(club)) {
                            stringBuilderShotsRead.append("\n").append(shotReadFromFile);
                            distance.putIfAbsent(club, new ArrayList<>());
                            distance.get(club).add(Float.valueOf(split[1]));
                        }
                    }
                }

                if(onlyGettingClubsUsed)
                    return previouslyUsedClubs.toString();

                /*if(gettingDistancesPerClub) {
                    String[] splitClubs = clubsUsed.split(" ");
                    for(String s: splitClubs){
                        if(distance.containsKey(s)) {
                            return distance.get(club).toString();
                        }
                    }
                }*/

                inputStream.close();
                shotsSavedFromFile = stringBuilderShotsRead.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return shotsSavedFromFile;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
