package com.android.markschmidt.nonzeroday;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by markschmidt on 5/11/15.
 */
public class SettingsActivity extends AppCompatActivity {
    private ImageButton mUpButton;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        mUpButton = (ImageButton)findViewById(R.id.settings_up_button);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateUp();
            }
        });

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

    public void navigateUp()
    {
        NavUtils.navigateUpFromSameTask(this);
    }
}
