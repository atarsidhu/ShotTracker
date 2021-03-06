package com.example.shottracker;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

public class ShotPopup extends DialogFragment {

    private TextView tvTitle, tvBody;
    private ImageView ivExit;
    private Button btnDeleteShot;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_shot_layout, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvTitle = view.findViewById(R.id.tvPopupTitle);
        tvBody = view.findViewById(R.id.tvPopupBody);
        ivExit = view.findViewById(R.id.imgExit);
        btnDeleteShot = view.findViewById(R.id.btnDeleteShot);
        databaseHelper = new DatabaseHelper(getContext());

        Bundle args = getArguments();
        String idOfShot = args.getString("ID");
        //tvTitle.setText(args.getString("TITLE"));
        tvBody.setText(args.getString("SHOT"));

        ivExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnDeleteShot.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                dismiss();

                DeletePopup deletePopup = new DeletePopup();
                Bundle args = new Bundle();
                args.putString("ALL_OR_ONE", "one");
                args.putString("ID", idOfShot);
                deletePopup.setArguments(args);
                deletePopup.show(getFragmentManager(), "Fragment");
            }
        });

        return view;
    }
}
