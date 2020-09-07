package com.example.shottracker;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private TextView tvShowShot;
    private Spinner spinnerClubs;
    private String club;
    TrackShotFragment trackShotFragment = new TrackShotFragment();
    ShotDatabase shotDatabase = ShotDatabase.getInstance();

    public ViewShotsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_shots, container, false);
        tvShowShot = view.findViewById(R.id.tvShowShot);
        spinnerClubs = view.findViewById(R.id.spinner_clubs_graph);

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();

        if(ShotDatabase.getValue(club) != null)
            tvShowShot.setText(ShotDatabase.getValue(club).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
