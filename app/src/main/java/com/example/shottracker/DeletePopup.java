package com.example.shottracker;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DeletePopup extends DialogFragment {

    private Button btnYes, btnNo;
    private DatabaseHelper databaseHelper;
    private Cursor data;
    private TextView tvMsg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.popup_delete_layout, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnYes = view.findViewById(R.id.btnYes);
        btnNo = view.findViewById(R.id.btnNo);
        tvMsg = view.findViewById(R.id.tvDeleteMessage);

        databaseHelper = new DatabaseHelper(getContext());

        Bundle args = getArguments();
        String allOrOne = args.getString("ALL_OR_ONE");
        String idOfShot = args.getString("ID");

        if(allOrOne.contains("all"))
            tvMsg.setText(R.string.deleteMsgAll);
        else
            tvMsg.setText(R.string.deleteMsgOne);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allOrOne.contains("all")) {
                    if (databaseHelper.deleteData()) {
                        Toast.makeText(getContext(), "Data deleted!", Toast.LENGTH_SHORT).show();
                        //spinnerClubs.setSelection(0);
                    } else
                        Toast.makeText(getContext(), "Data not deleted " + data.getCount(), Toast.LENGTH_SHORT).show();
                } else {
                    Cursor data = databaseHelper.getData();
                    int deletedRow = 0;
                    while (data.moveToNext()) {
                        if(data.getString(0).equals(idOfShot)) {
                            deletedRow = databaseHelper.deleteShot(args.getString("ID"));
                        }
                    }
                    if(deletedRow > 0)
                        Toast.makeText(getContext(), "Shot deleted!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "Shot not deleted!", Toast.LENGTH_SHORT).show();
                }

                dismiss();
            }
        });


        return view;
    }
}
