package com.example.shottracker;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: Long notes doesnt fit in homepage
//TODO: Instead of pieChart for ball flight, have animation of ball actually travelling the flight and show percentages of each flight
//TODO: Add mandatory course and hole input fields and automatically add date for each shot which is displayed on popup
//TODO: Semi-circle animation around the yards textview when calculating the yardage.
// Also make it count up to the yardage instead of displaying it right away
//TODO: Are you sure popup for deleting shots

public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    private TextView tvShowShot, tvPieChartTitle;
    private Spinner spinnerClubs;
    private String club;
    private Button btnDelete;
    private boolean preventSelectClubToast = false;

    private LineChart lineChart;
    private PieChart pieChart;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    private DatabaseHelper databaseHelper;
    Cursor data;

    public ViewShotsFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_shots, container, false);
        tvShowShot = view.findViewById(R.id.tvShowShot);
        tvPieChartTitle = view.findViewById(R.id.tvPieChartTitle);
        btnDelete = view.findViewById(R.id.btnDelete5);
        spinnerClubs = view.findViewById(R.id.spinner_clubs_graph5);

        lineChart = view.findViewById(R.id.lineChart5);
        lineChart.setOnChartValueSelectedListener(this);
        pieChart = view.findViewById(R.id.pieChart5);

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

        //DB
        databaseHelper = new DatabaseHelper(getContext());

        //Charts
        /*ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new LineChartDistance());
        fragments.add(new PieChartBallFlight());
        viewPager = view.findViewById(R.id.viewPager2);
        pagerAdapter = new SlidePagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);*/

        //setAndDisplayLineChart();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(databaseHelper.deleteData()) {
                    Toast.makeText(getContext(), "Data deleted!", Toast.LENGTH_SHORT).show();
                    spinnerClubs.setSelection(0);
                } else
                    Toast.makeText(getContext(), "Data not deleted " + data.getCount(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(isVisible()) { //menuVisible?
            //preventSelectClubToast = true;
            spinnerClubs.setSelection(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();

        /*if(isMenuVisible()) {
            if(!preventSelectClubToast) {
                Toast.makeText(getContext(), "Please select a club", Toast.LENGTH_SHORT).show();
                preventSelectClubToast = false;
            }
            setAndDisplayLineChart();
        }*/

        setAndDisplayLineChart();
        setAndDisplayPieChart();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAndDisplayLineChart(){
        ArrayList<Entry> shotsDisplayedOnGraph = new ArrayList<>();
        data = databaseHelper.getData();
        float max = 0f;
        int i = 0;

        if(isVisible() && data != null) {
            while (data.moveToNext()) {
                if (data.getString(1).contains(club)) {
                    shotsDisplayedOnGraph.add(new Entry(++i, data.getFloat(2)));

                    if(data.getFloat(2) > max)
                        max = data.getFloat(2);

                }
            }
        }

        if(shotsDisplayedOnGraph.isEmpty()){
            if(isVisible() && !club.contains("Select Club:")) {
                Toast.makeText(getContext(), "No " + club + " shots saved", Toast.LENGTH_SHORT).show();
            }
            shotsDisplayedOnGraph.add(new Entry(0, 0));
        }

        LineDataSet set1 = new LineDataSet(shotsDisplayedOnGraph, "Yards");
        set1.setFillAlpha(110);
        set1.setColor(getResources().getColor(R.color.colorPrimary));
        set1.setLineWidth(3);
        set1.setValueTextSize(10);
        set1.setValueTextColor(Color.GRAY);
        //set1.setDrawValues(false);
        set1.setDrawCircleHole(false);
        set1.setCircleRadius(5);
        set1.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData lineData = new LineData(dataSets);

        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(true);
        lineChart.setMaxHighlightDistance(30);
        lineChart.setPinchZoom(true);
        lineChart.setDoubleTapToZoomEnabled(true);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getAxisLeft().setAxisMaximum(max + 10);
        lineChart.getXAxis().setAxisMinimum(1);
        lineChart.getXAxis().setAxisMaximum(i);
        if(shotsDisplayedOnGraph.get(0).getX() == 0){
            lineChart.getXAxis().setAxisMinimum(0);
            lineChart.getXAxis().setAxisMaximum(1);
        }
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getDescription().setEnabled(false);

        lineChart.setData(lineData);
        lineChart.invalidate();
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

        if(isVisible() && data != null) {
            while (data.moveToNext()) {
                if (data.getString(1).contains(club) || club.contains("Select Club:")) {
                    switch (data.getString(3)){
                        case "Hook":
                            ballFlight.putIfAbsent("Hook", ++hook);
                            ballFlight.replace("Hook", ++hook);
                            break;
                        case "Draw":
                            ballFlight.putIfAbsent("Draw", ++draw);
                            ballFlight.replace("Draw", ++draw);
                            break;
                        case "Straight":
                            ballFlight.putIfAbsent("Straight", ++straight);
                            ballFlight.replace("Straight", ++straight);
                            break;
                        case "Fade":
                            ballFlight.putIfAbsent("Fade", ++fade);
                            ballFlight.replace("Fade", ++fade);
                            break;
                        case "Slice":
                            ballFlight.putIfAbsent("Slice", ++slice);
                            ballFlight.replace("Slice", ++slice);
                            break;
                    }
                }
            }

            for(String s: ballFlight.keySet()){
                shotFlightsDisplayedOnGraph.add(new PieEntry(ballFlight.get(s), s));
            }
            //shotFlightsDisplayedOnGraph.add(new PieEntry(hook, data.getString(3)));
        }

        if(ballFlight.isEmpty()){
            if(isVisible() && !club.contains("Select Club:"))
                Toast.makeText(getContext(), "No " + club + " shots saved", Toast.LENGTH_SHORT).show();

            //shotFlightsDisplayedOnGraph.add(new PieEntry(1, "No " + club + " shots saved"));
        }

        if(club.contains("Select Club:")){
            tvPieChartTitle.setText("\nBall Flights for All Clubs\n");
        } else{
            tvPieChartTitle.setText("\nBall Flights\n");
        }

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(0);
        pieChart.setTransparentCircleRadius(0);
        pieChart.setEntryLabelColor(Color.BLACK);
        //pieChart.setExtraOffsets(5, 10, 5, 5); // Moves piechart

        PieDataSet dataSet = new PieDataSet(shotFlightsDisplayedOnGraph, "Ball Flight");
        dataSet.setSliceSpace(1);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueTextSize(10);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    //Chart
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if(data != null) {
            if (data.getCount() > 0) {
                String message = "";
                String id = "";
                data.moveToFirst();

                do {
                    float distInDatabase = data.getFloat(2);
                    if (e.getY() == distInDatabase) {
                        id = data.getString(0);
                        message = "ID: " + id + "\nClub: " + data.getString(1) + "\nDistance: " + String.format("%.1f yards", data.getFloat(2))
                                + "\nBall Flight: " + data.getString(3) + "\nNotes: " + data.getString(4);

                    }
                } while (data.moveToNext());

                ShotPopup shotPopup = new ShotPopup();
                Bundle args = new Bundle();
                args.putString("TITLE", club);
                args.putString("SHOT", message);
                args.putString("ID", id);
                shotPopup.setArguments(args);
                shotPopup.show(getFragmentManager(), "Fragment");
            }
        }
    }

    @Override
    public void onNothingSelected() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
