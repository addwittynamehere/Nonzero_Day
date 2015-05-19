package com.android.markschmidt.nonzeroday;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by markschmidt on 4/13/15.
 */
public class NonzeroDayLab {

    private User mUser;

    private static NonzeroDayLab sObjectiveLab;
    private Context mContext;
    private static final String TAG = "NonzeroDayLab";
    private static final String mObjectiveFileName = "objectives.json";
    private static final String mUserFilename = "user.json";
    private NonzeroDayJSONSerializer mSerializer;

    private Drawable[] mImages;

    private NonzeroDayLab(Context c)
    {
        mContext = c;
        mSerializer = new NonzeroDayJSONSerializer(mContext, mObjectiveFileName, mUserFilename);

        try{
            mUser = mSerializer.loadUser();
            mUser.setObjectives(mSerializer.loadObjectives());
            Log.d(TAG, "Loaded objectives");
        } catch (Exception e) {
            mUser = new User();
            mUser.setObjectives(new ArrayList<Objective>());
            Log.e(TAG, "Error loading objectives", e);
        }


    }

    public static NonzeroDayLab get(Context c)
    {
        if(sObjectiveLab == null)
        {
            sObjectiveLab = new NonzeroDayLab((c.getApplicationContext()));
        }
        return sObjectiveLab;
    }

    public User getUser()
    {
        return mUser;
    }

    public ArrayList<Objective> getObjectives() {return mUser.getObjectives();}

    public Objective getObjective(UUID id)
    {
        ArrayList<Objective> list = mUser.getObjectives();
        for(Objective o : list)
        {
            if(o.getId().equals(id)) {
                return o;
            }
        }
        return null;
    }

    public Drawable getImage(int i)
    {
        if(mImages == null)
        {
            mImages = new Drawable[]{
                    mContext.getResources().getDrawable(R.drawable.reading),
                    mContext.getResources().getDrawable(R.drawable.computer),
                    mContext.getResources().getDrawable(R.drawable.running),
                    mContext.getResources().getDrawable(R.drawable.music),
                    mContext.getResources().getDrawable(R.drawable.writing),
                    mContext.getResources().getDrawable(R.drawable.photo),
                    mContext.getResources().getDrawable(R.drawable.logo)

            };
        }
        return mImages[i];
    }

    public void add(Objective obj)
    {
        mUser.getObjectives().add(obj);
    }

    public Drawable[] getImages()
    {
        return mImages;
    }


    public boolean save(ArrayList<Objective> objectives) {
        mUser.setObjectives(objectives);
        try{
            mSerializer.saveUser(mUser);
            Log.d(TAG, "User saved");
            return true;
        } catch (Exception e)
        {
            Log.e(TAG, "Error saving user", e);
            return false;
        }
    }

}
