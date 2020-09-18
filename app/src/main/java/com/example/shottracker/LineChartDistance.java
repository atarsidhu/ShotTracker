package com.example.shottracker;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.*;
import java.util.ArrayList;

public class LineChartDistance extends Fragment implements OnChartValueSelectedListener {

    private ArrayList<Entry> yAxis;
    LineChart lineChart;

    private String fileName;
    File dir;
    File file;

    private boolean onlyGettingClubsUsed;
    private String club;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.line_chart, container, false);

        lineChart = view.findViewById(R.id.lineChart);
        lineChart.setOnChartValueSelectedListener(this);

        fileName = "savedShots.txt";
        dir = getContext().getFilesDir();
        file = new File(dir, fileName);


        return view;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

/*    @RequiresApi(api = Build.VERSION_CODES.N)
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
                    String[] split = shotReadFromFile.split("#");
                    if(onlyGettingClubsUsed) {
                        if(!previouslyUsedClubs.toString().contains(split[0]))
                            previouslyUsedClubs.append(split[0]).append(" ");
                    } else{
                        if(shotReadFromFile.contains(club)) {
                            stringBuilderShotsRead.append("\n").append(shotReadFromFile);
                            shotDistancePerClub.putIfAbsent(club, new ArrayList<>());
                            shotDistancePerClub.get(club).add(Float.valueOf(split[1]));
                            notes.add(split[3]);
                        }
                    }
                }

                if(onlyGettingClubsUsed)
                    return previouslyUsedClubs.toString();

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
    }*/

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAndDisplayLineChart(){
        //readFromFile(getContext());
        yAxis = new ArrayList<>();

        /*for(int i = 0; i < shotDistancePerClub.get(club).size(); i++){
            yAxis.add(new Entry(i+1, shotDistancePerClub.get(club).get(i)));
        }*/

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
        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}
