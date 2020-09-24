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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;

public class LineChartDistance extends Fragment implements OnChartValueSelectedListener, AdapterView.OnItemSelectedListener {

    private ArrayList<Entry> yAxis;
    private Spinner spinnerClubs;
    LineChart lineChart;
    private ArrayList<Entry> shotsDisplayedOnGraph;
    private ArrayList<Integer> shotsPerClub;
    private String club;
    private boolean preventSelectClubToast = false;

    DatabaseHelper databaseHelper;
    Cursor data;

    public LineChartDistance(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.line_chart, container, false);
        spinnerClubs = view.findViewById(R.id.spinner_clubs_graph2);

        lineChart = view.findViewById(R.id.lineChart2);
        lineChart.setOnChartValueSelectedListener(this);

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

        databaseHelper = new DatabaseHelper(getContext());

        setMenuVisibility(false);
        setAndDisplayLineChart();
        setMenuVisibility(true);

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible) {
            preventSelectClubToast = true;
            //spinnerClubs.setSelection(0); // Gets called before onCreateView therefore causes a NPE
        }
    }

    //Spinner
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();

        if(isMenuVisible()) {
            if(!preventSelectClubToast) {
                Toast.makeText(getContext(), "Please select a club", Toast.LENGTH_SHORT).show();
                preventSelectClubToast = false;
            }
            setAndDisplayLineChart();
        }

        /*if(club.contains("Select Club:"))
            Toast.makeText(getContext(), "Please select a club", Toast.LENGTH_SHORT).show();*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAndDisplayLineChart(){
        shotsDisplayedOnGraph = new ArrayList<>();
        shotsPerClub = new ArrayList<>();
        data = databaseHelper.getData();

        int i = 1;
        if(isMenuVisible() && data != null) {
            while (data.moveToNext()) {
                if (data.getString(1).contains(club)) {
                    shotsDisplayedOnGraph.add(new Entry(i++, data.getFloat(2)));
                    shotsPerClub.add(data.getInt(0));
                }
            }
        }

        if(shotsDisplayedOnGraph.isEmpty()){
            if(isMenuVisible() && !club.contains("Select Club:")) {
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
        lineChart.getXAxis().setAxisMinimum(1);
        lineChart.getXAxis().setAxisMaximum(shotsPerClub.size());
        if(shotsDisplayedOnGraph.get(0).getX() == 0){
            lineChart.getXAxis().setAxisMinimum(0);
            lineChart.getXAxis().setAxisMaximum(1);
        }
        lineChart.getXAxis().setDrawGridLines(false);
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    //Chart
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if(data != null) {
            if (data.getCount() > 0) {
                int shotNum = (int) e.getX() - 1;

                String message = "";
                String id = "";
                data.moveToFirst();

                do {
                    float distInDatabase = data.getFloat(2);
                    if (e.getY() == distInDatabase) {
                        //Format distance to 1 decimal place here.
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
}
