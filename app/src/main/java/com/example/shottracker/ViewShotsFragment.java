package com.example.shottracker;

import android.content.Context;
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
import java.util.HashMap;

//TODO: Do we really need ShotDatabase class if it only saves the shots on each session (Class is volatile)?
// Or even the shot class for that matter (since we are writing everything to file and thus unable to use methods getNotes, getDistance etc.)?
// Switch to database to solve ^
//TODO: Create Cardview to implement pie chart
//TODO: Can we call readFromFile class from TrackShot class using viewShotsFragment.getContext() as parameter?
// If so, does that nullify a lot of the code from ViewShots class?
//TODO: Implement Database first, then move everything to SlideView fragments (Might be easier to access information then).
//TODO: Splash Screen

public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener, OnChartValueSelectedListener {

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
    private HashMap<String, ArrayList<Float>> shotDistancePerClub;
    private ArrayList<String> notes;
    private ArrayList<Entry> yAxis;

    TrackShotFragment trackShotFragment = new TrackShotFragment();
    ShotDatabase shotDatabase = ShotDatabase.getInstance();
    File dir;
    File file;
    LineChart lineChart;

    private ViewPager viewPager2;
    private PagerAdapter pagerAdapter;

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
        shotDistancePerClub = new HashMap<>();
        notes = new ArrayList<>();

        lineChart = view.findViewById(R.id.lineChart);
        //lineChart.setOnChartGestureListener(this);
        lineChart.setOnChartValueSelectedListener(this);

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

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
                deleteData();
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

    private void deleteData() {
        deleted = file.delete();
        tvShowShot.setText(getString(R.string.noShotsSaved, club));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Reset values to prevent overwriting
        shotDistancePerClub.clear();
        notes.clear();

        club = parent.getItemAtPosition(position).toString();

        onlyGettingClubsUsed = true;
        clubsUsed = readFromFile(getContext());
        onlyGettingClubsUsed = false;

        if(clubsUsed.contains(club)){
            tvShowShot.setText("");
            setAndDisplayLineChart();
        }else {
            if(lineChart.getData() != null)
                lineChart.clearValues();

            if(!spinnerClubs.getSelectedItem().toString().contains("Select Club:"))
                Toast.makeText(getContext(), "No " + club + " shots saved", Toast.LENGTH_SHORT).show();//tvShowShot.setText(getString(R.string.noShotsSaved, club));
        }

        //DB

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAndDisplayLineChart(){
        readFromFile(getContext());
        yAxis = new ArrayList<>();

        for(int i = 0; i < shotDistancePerClub.get(club).size(); i++){
            yAxis.add(new Entry(i+1, shotDistancePerClub.get(club).get(i)));
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

        if(!notes.get(shotNum).isEmpty())
            tvShowShot.setText(notes.get(shotNum));
        else
            tvShowShot.setText("No Notes");
    }

    @Override
    public void onNothingSelected() {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
