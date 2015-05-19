package com.android.markschmidt.nonzeroday;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements AddObjectiveFragment.OnFragmentInteractionListener
{
    private static final String TAG = "MainActivity";
    private ArrayList<Objective> mObjectives;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        // set morning alarm
        Calendar morningCalendar = Calendar.getInstance();
        morningCalendar.setTime(new Date());

        long currentTime = morningCalendar.getTimeInMillis();

        morningCalendar.set(Calendar.HOUR_OF_DAY, 9);
        morningCalendar.set(Calendar.MINUTE, 0);
        morningCalendar.set(Calendar.SECOND, 0);
        morningCalendar.set(Calendar.MILLISECOND, 0);

        long morningReminderTime = morningCalendar.getTimeInMillis();
        if(currentTime > morningReminderTime)
        {
            morningCalendar.add(Calendar.DAY_OF_MONTH, 1);
            morningReminderTime = morningCalendar.getTimeInMillis();
        }

        Calendar nightCalendar = Calendar.getInstance();

        nightCalendar.set(Calendar.HOUR_OF_DAY, 9);
        nightCalendar.set(Calendar.MINUTE, 0);
        nightCalendar.set(Calendar.SECOND, 0);
        nightCalendar.set(Calendar.MILLISECOND, 0);

        long nightReminderTime = nightCalendar.getTimeInMillis();
        if(currentTime > nightReminderTime)
        {
            nightCalendar.add(Calendar.DAY_OF_MONTH, 1);
            nightReminderTime = nightCalendar.getTimeInMillis();
        }


        mObjectives = NonzeroDayLab.get(this).getObjectives();
        for (Objective objective : mObjectives) {
            Intent nightIntent = new Intent(this, AlarmReceiver.class)
                    .setAction(AlarmReceiver.ACTION_NOTIFY_NIGHT)
                    .putExtra(AlarmReceiver.EXTRA_TITLE, objective.getTitle());
            PendingIntent nightPendingIntent = PendingIntent.getBroadcast(
                    this, 0, nightIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            am.setRepeating(AlarmManager.RTC_WAKEUP, nightReminderTime, AlarmManager.INTERVAL_DAY, nightPendingIntent);

        }

        Log.i(TAG, "Set morning intent for " + morningCalendar.get(Calendar.HOUR_OF_DAY) + " AM "
                + morningCalendar.get(Calendar.MONTH) + " " + morningCalendar.get(Calendar.DAY_OF_MONTH));
        Intent morningIntent = new Intent(this, AlarmReceiver.class)
                .setAction(AlarmReceiver.ACTION_NOTIFY_MORNING);
        PendingIntent morningPendingIntent = PendingIntent.getBroadcast(
                this, 0, morningIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, morningReminderTime, AlarmManager.INTERVAL_DAY, morningPendingIntent);




        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            // Create a new Fragment to be placed in the activity layout
            MainActivityFragment firstFragment = new MainActivityFragment();

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }

    }


    @Override
    public void onBackPressed()
    {
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getFragmentManager().popBackStack();
        }

    }


    public void onAddNewObjective(Objective objective) {
        NonzeroDayLab nonzeroDayLab = NonzeroDayLab.get(this);
        nonzeroDayLab.add(objective);
        mObjectives = nonzeroDayLab.getObjectives();
        if(mObjectives.size() > 0 && mObjectives.get(0) == null)
            mObjectives.remove(0);
        updateUI();
    }

    public void onEditObjective(UUID id, Objective updatedObjective) {
        Objective oldObjective = NonzeroDayLab.get(this).getObjective(id);
        oldObjective.setTitle(updatedObjective.getTitle());
        oldObjective.setImage(updatedObjective.getImage());
        oldObjective.setVerb(updatedObjective.getVerb());
        oldObjective.setPastVerb(updatedObjective.getPastVerb());
        oldObjective.setSingularNoun(updatedObjective.getSingularNoun());
        oldObjective.setMultipleNoun(updatedObjective.getMultipleNoun());

        updateUI();
    }



    public void updateUI()
    {
        RecyclerView v = (RecyclerView)findViewById(R.id.objective_recycler_view);
        if(v != null){
            ObjectiveAdapter adapter = (ObjectiveAdapter)v.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }
}
