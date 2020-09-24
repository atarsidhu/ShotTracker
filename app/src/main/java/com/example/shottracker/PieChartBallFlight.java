package com.example.shottracker;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class PieChartBallFlight extends Fragment implements AdapterView.OnItemSelectedListener {

    private PieChart pieChart;
    private Spinner spinnerClubs;
    private String club;
    DatabaseHelper databaseHelper;
    Cursor data;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pie_chart, container, false);
        pieChart = view.findViewById(R.id.pieChart);

        //Spinner configurations
        spinnerClubs = view.findViewById(R.id.spinner_clubs_graph3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

        databaseHelper = new DatabaseHelper(getContext());
        setAndDisplayPieChart();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();
        setAndDisplayPieChart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAndDisplayPieChart(){
        ArrayList<PieEntry> shotFlightsDisplayedOnGraph = new ArrayList<>();
        data = databaseHelper.getData();
        int hook = 0;
        int draw = 0;
        int straight = 0;
        int fade = 0;
        int slice = 0;
        HashMap<String, Integer> ballFlight = new HashMap<>();

        if(isMenuVisible() && data != null) {
            while (data.moveToNext()) {
                if (data.getString(1).contains(club)) {
                    switch (data.getString(3)){
                        case "Hook":
                            hook++;
                            ballFlight.putIfAbsent("Hook", hook);
                            ballFlight.replace("Hook", hook);
                            break;
                        case "Draw":
                            draw++;
                            ballFlight.putIfAbsent("Draw", draw);
                            ballFlight.replace("Draw", draw);
                            break;
                        case "Straight":
                            straight++;
                            ballFlight.putIfAbsent("Straight", straight);
                            ballFlight.replace("Straight", straight);
                            break;
                        case "Fade":
                            fade++;
                            ballFlight.putIfAbsent("Fade", fade);
                            ballFlight.replace("Fade", fade);
                            break;
                        case "Slice":
                            slice++;
                            ballFlight.putIfAbsent("Slice", slice);
                            ballFlight.replace("Slice", slice);
                            break;
                    }
                }
            }

            for(String s: ballFlight.keySet()){
                shotFlightsDisplayedOnGraph.add(new PieEntry(ballFlight.get(s), s));
            }
            //shotFlightsDisplayedOnGraph.add(new PieEntry(hook, data.getString(3)));
        }
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        //pieChart.setExtraOffsets(5, 10, 5, 5);

        PieDataSet dataSet = new PieDataSet(shotFlightsDisplayedOnGraph, "Ball Flight");
        dataSet.setSliceSpace(2);
        dataSet.setSelectionShift(4);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextColor(Color.GREEN);
        pieData.setValueTextSize(10);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }


}
