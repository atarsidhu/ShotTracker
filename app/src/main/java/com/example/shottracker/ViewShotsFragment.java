package com.example.shottracker;

import android.database.Cursor;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.ArrayList;

//TODO: Create Cardview to implement pie chart
//TODO: Implement Database first, then move everything to SlideView fragments (Might be easier to access information then).
//TODO: Splash Screen
//TODO: Long notes doesnt fit in popup box AND doesnt fit in homepage

public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    private TextView tvShowShot;
    private Spinner spinnerClubs;
    private String club;
    private Button btnDelete;
    private ArrayList<Entry> shotsDisplayedOnGraph;
    private ArrayList<Integer> shotsPerClub;
    private boolean preventSelectClubToast = false;

    LineChart lineChart;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    DatabaseHelper databaseHelper;
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
        btnDelete = view.findViewById(R.id.btnDelete);
        /*spinnerClubs = view.findViewById(R.id.spinner_clubs_graph);

        lineChart = view.findViewById(R.id.lineChart);
        lineChart.setOnChartValueSelectedListener(this);

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);*/

        //DB
        databaseHelper = new DatabaseHelper(getContext());

        //Charts
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new LineChartDistance());
        fragments.add(new PieChartBallFlight());
        viewPager = view.findViewById(R.id.viewPager2);
        pagerAdapter = new SlidePagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(pagerAdapter);

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
/*        super.setMenuVisibility(menuVisible);
        if(menuVisible) {
            preventSelectClubToast = true;
            spinnerClubs.setSelection(0);
        }*/
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
/*
        club = parent.getItemAtPosition(position).toString();

        if(isMenuVisible()) {
            if(!preventSelectClubToast) {
                Toast.makeText(getContext(), "Please select a club", Toast.LENGTH_SHORT).show();
                preventSelectClubToast = false;
            }
            setAndDisplayLineChart();
        }
*/

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAndDisplayLineChart(){
        /*shotsDisplayedOnGraph = new ArrayList<>();
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
        //lineChart.setScaleEnabled(false);
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
        lineChart.invalidate();*/
    }

    //Chart touch functions
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        /*if(data.getCount() > 0) {
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
        }*/
    }

    @Override
    public void onNothingSelected() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
