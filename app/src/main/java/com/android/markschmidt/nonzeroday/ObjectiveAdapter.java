package com.android.markschmidt.nonzeroday;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by markschmidt on 4/13/15.
 */
public class ObjectiveAdapter extends RecyclerView.Adapter<ObjectiveAdapter.ViewHolder>
{
    private Context mContext;
    private ArrayList<Objective> mObjectives;
    public MainActivityFragment mFragment;
    private static final String DIALOG_TODAY = "today";

    //TODO: Properly implement first time user trait



    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mImageView;
        public TextView mStreakTextView;
        public TextView mExpirationTextView;
        public TextView mNextDayTextView;
        public TextView mTotalTextView;
        public TextView mTitleTextView;
        public TextView mIntroText;
        public Button mAddButton;
        public Button mDetailsButton;
        public CardView mCardView;
        public ObjectiveClickerListener mListener;


        public ViewHolder(CardView cardView, ObjectiveClickerListener listener)
        {
            super(cardView);
            mCardView = cardView;
            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickObjective(v);
                }
            });
            mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onLongClickObjective(v);
                }
            });
            mListener = listener;
            mImageView = (ImageView)cardView.findViewById(R.id.objective_imageView);
            mStreakTextView = (TextView)cardView.findViewById(R.id.streak_text_view);
            mExpirationTextView = (TextView)cardView.findViewById(R.id.expiration_text_view);
            mNextDayTextView = (TextView)cardView.findViewById(R.id.nextday_text_view);
            mTotalTextView = (TextView)cardView.findViewById(R.id.total_text_view);
            mTitleTextView = (TextView)cardView.findViewById(R.id.objective_title_textView);
            mAddButton = (Button)cardView.findViewById(R.id.objective_add_button);
            mDetailsButton = (Button)cardView.findViewById(R.id.objective_details_button);
        }

        public void onClickObjective(View v) {
            Log.i(TAG, "Clicked on item");
            mListener.onClick(v, getPosition());

        }


        public boolean onLongClickObjective(View v) {
            Log.i(TAG, "Long clicked on an item");
            mListener.onLongClick(v, getPosition());
            return true;
        }

        public interface ObjectiveClickerListener
        {
            public void onClick(View v, int position);
            public void onLongClick(View v, int position);
        }
    }

    public static final String TAG = "ObjectiveAdapter";

    public ObjectiveAdapter(ArrayList<Objective> objs, Context c, MainActivityFragment f)
    {
        mContext = c;
        mObjectives = objs;
        mFragment = f;
        if(mFragment.isFirstTimeUser())
            if(mObjectives.size() == 0)
                mObjectives.add(0, null);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Objective objective = mObjectives.get(i);

        viewHolder.mCardView.setCardBackgroundColor(Color.WHITE);
        viewHolder.mCardView.setUseCompatPadding(true);
        viewHolder.mCardView.setPreventCornerOverlap(false);

        if(!mFragment.isFirstTimeUser()){
            viewHolder.mImageView.setImageDrawable(NonzeroDayLab.get(mContext).getImage(objective.getImage()));

            viewHolder.mAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getPosition();
                    Objective o = mObjectives.get(position);
                    FragmentManager fm = mFragment.getFragmentManager();
                    TodayIDialog dialog = TodayIDialog.newInstance(o.getId(), position);
                    dialog.setTargetFragment(mFragment, 0);
                    dialog.show(fm, DIALOG_TODAY);

                }
            });

            viewHolder.mDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = viewHolder.getPosition();
                    Objective o = mObjectives.get(position);
                    if(o.getDayData().size() > 0){
                        FragmentManager fm = mFragment.getFragmentManager();
                        DetailFragment detailFragment = DetailFragment.newInstance(o.getId());
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.fragment_container, detailFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    else
                    {
                        Toast toast = Toast.makeText(mContext, "Start adding some Streak Data in order to access the Details page!", Toast.LENGTH_LONG);
                        toast.show();
                    }


                }
            });


            String sourceStreakText;
            if(!objective.isOnAStreak()) {
                sourceStreakText = "On a streak for <b><font color = #1976D2> 0 days</font></b>";
            } else {
                if (objective.getDayData().size() == 1)
                    sourceStreakText = "On a streak for <b><font color = #1976D2>1 day</font></b>";
                else
                    sourceStreakText = "On a streak for <b><font color = #1976D2>" + objective.getStreakCount()+ " days</font></b>";
            }

            viewHolder.mStreakTextView.setText(Html.fromHtml(sourceStreakText));

            ArrayList<DayData> dayDataArrayList = objective.getDayData();
            objective.checkStreak();
            if(objective.isOnAStreak())
            {
                if(dayDataArrayList.size() == 0)
                    viewHolder.mExpirationTextView.setText(Html.fromHtml("You are not on a <b><font color = #1976D2>streak</font></b>"));
                else {
                    Date date = dayDataArrayList.get(dayDataArrayList.size()-1).getDate();
                    Date expirationDate = DayData.getExpirationDate(date);
                    objective.setExpirationDate(expirationDate);

                    String expirationString = DayData.timeAsString(DayData.calculateTimeUntilExpiration(expirationDate));
                    viewHolder.mExpirationTextView.setText(Html.fromHtml("Streak will expire in <b><font color = #1976D2>" + expirationString + "</font></b>"));
                    Log.i(TAG, "Expiration date is" + objective.getExpirationDate());

                }
            } else {
                viewHolder.mExpirationTextView.setText(Html.fromHtml("You are not on a <b><font color = #1976D2>streak</font></b>"));
                objective.setExpirationDate(null);
            }

            Date nextDay = DayData.getNextDay();
            String nextDayString = DayData.timeAsString(DayData.calculateTimeUntilExpiration(nextDay));
            viewHolder.mNextDayTextView.setText(Html.fromHtml("Next day will begin in <b><font color = #1976D2>" + nextDayString + "</font></b>"));

            viewHolder.mTotalTextView.setText(Html.fromHtml("I've " + objective.getPastVerb() + " <b><font color = #1976D2>"
                    + objective.getTotal() + " " + objective.getMultipleNoun() + "</b></font>"));

            viewHolder.mTitleTextView.setText(objective.getTitle());
        }
        else
        {
            viewHolder.mIntroText.setText(Html.fromHtml("This app is designed to help you get motivated and stay motivated." +
                    " There are some simple rules I suggest you follow if you intend to use this app to its fullest extent:" +
                    "<br><br><b>Rule 1: No more zero days</b><br>No matter what, you will put effort towards your " +
                    "goals every single day. It doesn’t have to be much. If you’ve wasted your whole day away, take five minutes " +
                    "before you to go sleep to do some actual work that interests you. Keeping this momentum going is the key to " +
                    "staying motivated.<br><br><b>Rule 2: You will share your success</b><br>Take pride in your work. Let your" +
                    " friends and family know what you’ve been working on. If you wrote something or made something, show it off. " +
                    "If you read something or learned something, tell the world all about it.<br><br><b>Rule 3: You will forgive " +
                    "yourself</b><br>If you miss a day or make mistakes, don’t let it be the end. Get back up, dust yourself off, " +
                    "and start over again. If you let one failure derail you, you will never stay motivated.<br><br>Click the ‘+’ " +
                    "button to start your first Objective, earn XP by adding new streak data each day, and track your progress in " +
                    "the Objective Detail view!"));
        }



    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        CardView v;
        ViewHolder vh;
        if(!mFragment.isFirstTimeUser())
        {
            Log.i(TAG, "Building objectives");
            v = (CardView)LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.objective_list_item, viewGroup, false);
            vh = new ViewHolder(v, new ViewHolder.ObjectiveClickerListener() {
            @Override
            public void onClick(View v, int position) {
            }

            @Override
            public void onLongClick(View v, int position) {
                AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                adb.setTitle("Objective Options");
                adb.setMessage("What would you like to do with this objective?");

                final int positionFinal = position;
                adb.setNeutralButton("Cancel", null);
                adb.setNegativeButton("Delete", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        removeItem(positionFinal);
                    }
                });
                adb.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Objective objective = mObjectives.get(positionFinal);

                        FragmentManager fm = mFragment.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        AddObjectiveFragment f = AddObjectiveFragment
                                .newInstance(objective.getId(), false);
                        ft.replace(R.id.fragment_container, f);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
                adb.show();
            }
        });
        } else {
            Log.i(TAG, "Building intro card");
            v = (CardView)LayoutInflater.from(mContext).inflate(R.layout.intro_card, viewGroup, false);
            vh = new ViewHolder(v, new ViewHolder.ObjectiveClickerListener() {
                @Override
                public void onClick(View v, int position) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(mContext);
                    adb.setTitle("Intro Card Options");
                    adb.setMessage("What would you like to do with this card?");

                    final int positionFinal = position;
                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Delete", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            removeItem(positionFinal);
                        }
                    });
                    adb.show();
                }

                @Override
                public void onLongClick(View v, int position) {

                }
            });

            vh.mIntroText = (TextView)v.findViewById(R.id.intro_card_description);

        }

        return vh;
    }

    @Override
    public int getItemCount() {
        return mObjectives.size();
    }

    public void removeItem(int position) {
        mObjectives.remove(position);
        notifyItemRemoved(position);
    }
}