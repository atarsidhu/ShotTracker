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

public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    private TextView tvPieChartTitle;
    private Spinner spinnerClubs;
    private String club;

    private LineChart lineChart;
    private PieChart pieChart;

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
        tvPieChartTitle = view.findViewById(R.id.tvPieChartTitle);
        Button btnDelete = view.findViewById(R.id.btnDelete5);
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

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeletePopup deletePopup = new DeletePopup();
                Bundle args = new Bundle();
                args.putString("ALL_OR_ONE", "all");
                deletePopup.setArguments(args);
                deletePopup.show(getFragmentManager(), "Fragment");
            }
        });

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(isVisible()) {
            spinnerClubs.setSelection(0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();

        setAndDisplayLineChart();
        setAndDisplayPieChart();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    /**
     * Creates and populates the Line Chart depending on the club selected.
     */
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
        set1.setDrawValues(false);
        set1.setDrawCircleHole(false);
        set1.setCircleRadius(5);
        set1.setCircleColor(getResources().getColor(R.color.colorPrimaryDark));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        LineData lineData = new LineData(dataSets);

        lineChart.setDragEnabled(true);
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

    /**
     * Creates and populates the Pie Chart based on the club selected.
     */
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

            for(String s: ballFlight.keySet())
                shotFlightsDisplayedOnGraph.add(new PieEntry(ballFlight.get(s), s));
        }

        if(ballFlight.isEmpty()){
            if(isVisible() && !club.contains("Select Club:"))
                Toast.makeText(getContext(), "No " + club + " shots saved", Toast.LENGTH_SHORT).show();
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
        pieChart.getLegend().setEnabled(false);

        PieDataSet dataSet = new PieDataSet(shotFlightsDisplayedOnGraph, "Ball Flight");
        dataSet.setSliceSpace(1);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(dataSet);
        pieData.setValueTextColor(Color.BLACK);
        pieData.setValueTextSize(10);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

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
                        message = getString(R.string.shotSelectedInformation, data.getString(1),
                                String.format("%.1f yards", data.getFloat(2)), data.getString(3), data.getString(4));

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
