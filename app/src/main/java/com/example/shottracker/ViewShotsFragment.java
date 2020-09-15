package com.example.shottracker;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//TODO: When new shot is added to the already selected club on the spinner, the new shot is not shown.
// The user must choose a different club on the spinner, then return to the club in which the new shot is associated with.
//TODO: Implement graph first, the try and solve that ^.

public class ViewShotsFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private TextView tvShowShot;
    private Spinner spinnerClubs;
    private String club;
    private Button btnDelete;
    private boolean deleted;
    private boolean onlyGettingClubsUsed;
    TrackShotFragment trackShotFragment = new TrackShotFragment();
    ShotDatabase shotDatabase = ShotDatabase.getInstance();
    File dir;
    File file;

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
        dir = getContext().getFilesDir();
        file = new File(dir, "savedShots.txt");

        //Spinner configurations
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.clubs, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClubs.setAdapter(adapter);
        spinnerClubs.setOnItemSelectedListener(this);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

        return view;
    }

    private void deleteData() {
        deleted = file.delete();
        tvShowShot.setText("No " + club + " shots saved");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        club = parent.getItemAtPosition(position).toString();

        onlyGettingClubsUsed = true;
        String clubsUsed = readFromFile(getContext());
        onlyGettingClubsUsed = false;

        if(clubsUsed.contains(club))
            tvShowShot.setText(readFromFile(getContext()));
        else
            tvShowShot.setText("No " + club + " shots saved");
    }

    private ArrayList<Shot> loadShots(String key){
        ArrayList<Shot> temp = new ArrayList<>();
        ArrayList<Object> list = new ArrayList<>();
        File file = new File(getContext().getFilesDir() + "/savedShots");
        try {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream o = new ObjectInputStream(f);
            HashMap<String, ArrayList<Shot>> fileObj2 = (HashMap<String, ArrayList<Shot>>) o.readObject();
            o.close();
            return fileObj2.get(key);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return temp;
    }

    public String readFromFile(Context context) {
        String shotsSavedFromFile = "";

        try {
            InputStream inputStream = context.openFileInput("savedShots.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String shotReadFromFile;
                StringBuilder stringBuilderShotsRead = new StringBuilder();
                StringBuilder previouslyUsedClubs = new StringBuilder();

                while ((shotReadFromFile = bufferedReader.readLine()) != null) {
                    if(onlyGettingClubsUsed) {
                        String[] split = shotReadFromFile.split(" ");
                        if(!previouslyUsedClubs.toString().contains(split[0]))
                            previouslyUsedClubs.append(split[0]).append(" ");
                    } else{
                        if(shotReadFromFile.contains(club))
                            stringBuilderShotsRead.append("\n").append(shotReadFromFile);
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
