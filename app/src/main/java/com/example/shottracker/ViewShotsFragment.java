package com.example.shottracker;

import android.app.AlertDialog;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.io.*;
import java.util.ArrayList;

//TODO: Create Cardview to implement pie chart
//TODO: Implement Database first, then move everything to SlideView fragments (Might be easier to access information then).
//TODO: Splash Screen

public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

    private TextView tvShowShot;
    private Spinner spinnerClubs;
    private String club;
    private Button btnDelete;
    private String fileName;
    private ArrayList<Entry> yAxis;
    private ArrayList<Integer> shotsPerClub;


    ShotDatabase shotDatabase = ShotDatabase.getInstance();
    File dir;
    File file;
    LineChart lineChart;

    private ViewPager viewPager2;
    private PagerAdapter pagerAdapter;

    DatabaseHelper databaseHelper;
    Cursor data;

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

        lineChart = view.findViewById(R.id.lineChart);
        //lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartValueSelectedListener(this);

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
        fragments.add(new com.example.shottracker.PieChart());

        viewPager2 = view.findViewById(R.id.viewPager2);
        pagerAdapter = new SlidePagerAdapter(getFragmentManager(), fragments);
        viewPager2.setAdapter(pagerAdapter);*/

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.deleteData();
            }
        });

        return view;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(menuVisible)
            spinnerClubs.setSelection(0);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();

        if(!club.equals("Select Club:"))
            setAndDisplayLineChart();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAndDisplayLineChart(){
        yAxis = new ArrayList<>();
        data = databaseHelper.getData("all");

        shotsPerClub = new ArrayList<>();
        int i = 1;
        if(data != null) {
            while (data.moveToNext()) {
                if(data.getString(1).contains(club)) {
                    yAxis.add(new Entry(i++, data.getFloat(2)));
                    shotsPerClub.add(data.getInt(0));
                }
            }
        }

        if(yAxis.isEmpty()){
            Toast.makeText(getContext(), "No " + club + " shots saved", Toast.LENGTH_SHORT).show();
            lineChart.setNoDataText("No " + club + " shots saved");
            yAxis.add(new Entry(0, 0));
            lineChart.getXAxis().setAxisMinimum(0);
            lineChart.getXAxis().setAxisMaximum(1);
        }

        LineDataSet set1 = new LineDataSet(yAxis, "Yards");
        set1.setFillAlpha(110);
        set1.setColor(getResources().getColor(R.color.colorPrimary));
        set1.setLineWidth(3);
        set1.setValueTextSize(10);
        set1.setValueTextColor(Color.GRAY);
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
        lineChart.getXAxis().resetAxisMaximum();
        lineChart.getXAxis().resetAxisMinimum();
        lineChart.getXAxis().setAxisMaximum(1);
        lineChart.getXAxis().setAxisMaximum(shotsPerClub.size());
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    //Chart touch functions
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int shotNum = (int) e.getX() - 1;
        int i = 0;

        data.moveToPosition(shotsPerClub.get(shotNum) - 1);
        String message = "Club: " + data.getString(1) + "\nDistance: " + data.getFloat(2)
                + "\nBall Flight: " + data.getString(3) + "\nNotes: " + data.getString(4);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(club);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onNothingSelected() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
