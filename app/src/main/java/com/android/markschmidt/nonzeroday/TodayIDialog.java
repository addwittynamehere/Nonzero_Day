package com.android.markschmidt.nonzeroday;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.NumberPicker;

import java.util.UUID;

/**
 * Created by markschmidt on 4/18/15.
 */
public class TodayIDialog extends DialogFragment{

    private static String ARG_ID = "id";
    private static String ARG_POSITION = "position";

    private UUID mId;
    private NumberPicker mNumberPicker;
    private Objective mObjective;
    private int mPosition;

    public static TodayIDialog newInstance(UUID id, int position)
    {
        TodayIDialog i = new TodayIDialog();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id.toString());
        args.putInt(ARG_POSITION, position);
        i.setArguments(args);
        return i;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mId = UUID.fromString(getArguments().getString(ARG_ID));
        mPosition = getArguments().getInt(ARG_POSITION);

        mObjective = NonzeroDayLab.get(getActivity()).getObjective(mId);
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.today_dialog, null);

        mNumberPicker = (NumberPicker)v.findViewById(R.id.todayI_numberPicker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(500);
        mNumberPicker.setWrapSelectorWheel(false);
        mNumberPicker.setValue(10);
        //TODO:Decide what looks best here
        String title =  "<font color=@color/colorPrimary> How many " + mObjective.getMultipleNoun() + " have you " + mObjective.getPastVerb() + " today?</font>";
        //String title = mObjective.getMultipleNoun().substring(0,1).toUpperCase() + mObjective.getMultipleNoun().substring(1)+ " " + mObjective.getPastVerb() + " today?";
        adb.setView(v);
        adb.setTitle(Html.fromHtml(title));
        adb.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivityFragment f = (MainActivityFragment) getTargetFragment();
                f.onAddNewStreakData(mNumberPicker.getValue(), mPosition);
            }
        });

        adb.setNegativeButton("Cancel", null);
        return adb.create();
    }
}
