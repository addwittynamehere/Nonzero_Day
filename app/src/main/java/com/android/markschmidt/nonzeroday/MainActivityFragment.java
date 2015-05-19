package com.android.markschmidt.nonzeroday;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

/**
 * Created by markschmidt on 4/14/15.
 */
public class MainActivityFragment extends Fragment {
    private ArrayList<Objective> mObjectives;
    private User mUser;
    private RecyclerView mRecyclerView;
    private ObjectiveAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageButton mMenuButton;
    private TextView mLevel;
    private FloatingActionButton mAddButton;
    private ProgressBar mProgressBar;
    private TextView mExperienceText;


    private final String TAG = "MainActivityFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);



        mRecyclerView = (RecyclerView)v.findViewById(R.id.objective_recycler_view);
        mUser = NonzeroDayLab.get(getActivity()).getUser();
        mObjectives = mUser.getObjectives();

        mLevel = (TextView)v.findViewById(R.id.levelNumberTextView);


        mAdapter = new ObjectiveAdapter(mObjectives, getActivity(), MainActivityFragment.this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAddButton = (FloatingActionButton)v.findViewById(R.id.add_button);
        mAddButton.setImageResource(R.drawable.add_image);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                AddObjectiveFragment f = AddObjectiveFragment.newInstance(null, true);
                ft.replace(R.id.fragment_container, f);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        mAddButton.attachToRecyclerView(mRecyclerView);


        mMenuButton = (ImageButton)v.findViewById(R.id.menu_button_main);
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(getActivity(), mMenuButton);
                popup.getMenuInflater().inflate(R.menu.main_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.menu_main_settings) {
                            Intent i = new Intent(getActivity(), SettingsActivity.class);
                            startActivity(i);
                        }
                        if(id == R.id.menu_main_reset_user)
                        {
                            mUser.setLevel(1);
                            mUser.setExperience(0);
                            mUser.setNextLevelExperience(mUser.calculateNextLevelExperience(mUser.getLevel()));
                            mUser.setPreviousLevelExperience(0);
                            updateExperience();
                        }

                        return false;
                    }
                });
                popup.show();
            }
        });
        long xp = mUser.getExperience();
        mProgressBar = (ProgressBar)v.findViewById(R.id.levelProgressBar);

        mExperienceText = (TextView)v.findViewById(R.id.experienceTextView);

        updateExperience();

        return v;
    }

    public void updateExperience()
    {
        mLevel.setText("" + mUser.getLevel());

        mProgressBar.setProgress((int) mUser.calcPercentageXP());
        String sourceString = "<b>"+ mUser.calcXPtoLevelUp() + " XP</b> until Level "+ (mUser.getLevel() + 1);
        mExperienceText.setText(Html.fromHtml(sourceString));
    }

    //Callback from TodayIDialog to update streak count
    public void onAddNewStreakData(int value, int position)
    {
        Log.i(TAG, "Added new streak data " + value);
        int experience = mUser.add(value, position);
        Toast.makeText(getActivity(), "You gained " + experience + " experience!", Toast.LENGTH_LONG).show();
        updateExperience();
        mAdapter.notifyDataSetChanged();
        //onResume takes care of it?
    }

    @Override
    public void onPause() {
        super.onPause();

        NonzeroDayLab.get(getActivity()).save(mObjectives);
    }

    @Override
    public void onResume() {
        super.onResume();
        for(Objective o : mObjectives)
        {
            if(o!= null)
                o.checkStreak();
        }
        mAdapter.notifyDataSetChanged();
    }

    public boolean isFirstTimeUser()
    {
        return mObjectives.size() == 0 || mObjectives.get(0) == null;
    }



}
