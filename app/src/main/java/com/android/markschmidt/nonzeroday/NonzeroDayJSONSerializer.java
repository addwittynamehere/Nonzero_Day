package com.android.markschmidt.nonzeroday;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by markschmidt on 4/14/15.
 */
public class NonzeroDayJSONSerializer {
    private Context mContext;
    private String mObjectiveFilename;
    private String mUserFilename;

    public NonzeroDayJSONSerializer(Context c, String o, String u)
    {
        mContext = c;
        mObjectiveFilename = o;
        mUserFilename = u;
    }
    //TODO: Implement saving and loading user file

    public void saveUser(User user) throws JSONException, IOException
    {
        JSONObject jsonUser = user.toJSON();
        Writer writer = null;
        try
        {
            OutputStream out = mContext.openFileOutput(mUserFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jsonUser.toString());
        } finally {
            if(writer != null)
                writer.close();
        }
        if(user.getObjectives().size() == 0 || user.getObjectives().get(0) != null)
            saveObjectives(user.getObjectives());
    }

    public void saveObjectives(ArrayList<Objective> objectives) throws JSONException, IOException
    {
        JSONArray jsonArray = new JSONArray();
        for(Objective o : objectives)
            jsonArray.put(o.toJSON());

        Writer writer = null;
        try
        {
            OutputStream out = mContext.openFileOutput(mObjectiveFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(jsonArray.toString());
        } finally {
            if(writer != null)
                writer.close();
        }
    }

    public User loadUser() throws IOException, JSONException {
        User user = null;
        BufferedReader reader = null;
        try{
            InputStream in = mContext.openFileInput(mUserFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONObject jsonObject = (JSONObject) new JSONTokener(jsonString.toString()).nextValue();
            user = new User(jsonObject);
        } catch (FileNotFoundException e)
        {
            // no file, don't do anything
        } finally {
            if(reader != null)
                reader.close();
        }
        return user;
    }

    public ArrayList<Objective> loadObjectives() throws IOException, JSONException {
        ArrayList<Objective> objectives = new ArrayList<Objective>();
        BufferedReader reader = null;
        try{
            InputStream in = mContext.openFileInput(mObjectiveFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            //Parse JSON
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for(int i = 0; i <array.length(); i++)
            {
                objectives.add(new Objective(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // no file present, ignore it
        } finally {
            if(reader != null)
                reader.close();
        }
        return objectives;
    }

}

