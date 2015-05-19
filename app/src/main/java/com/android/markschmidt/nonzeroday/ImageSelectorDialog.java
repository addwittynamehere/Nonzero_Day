package com.android.markschmidt.nonzeroday;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.UUID;

/**
 * Created by markschmidt on 4/15/15.
 */
public class ImageSelectorDialog extends DialogFragment {

    private ViewPager mViewPager;
    private static final String ARG_ID = "id";
    private UUID mId;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(R.layout.image_selector_dialog, null);

        mId = UUID.fromString(getArguments().getString(ARG_ID));
        mViewPager = (ViewPager)v.findViewById(R.id.viewPager);
        mViewPager.setAdapter(new ImageSelectorAdapter(getActivity()));
        mViewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddObjectiveFragment f = (AddObjectiveFragment)getTargetFragment();
                f.onImageChosen(mViewPager.getCurrentItem());
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Pick an image")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddObjectiveFragment f = (AddObjectiveFragment) getTargetFragment();
                        f.onImageChosen(mViewPager.getCurrentItem());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
    }


    public static ImageSelectorDialog newInstance(UUID id)
    {
        ImageSelectorDialog i = new ImageSelectorDialog();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id.toString());
        i.setArguments(args);
        return i;

    }


    private class ImageSelectorAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;

        private Drawable[] images;

        public ImageSelectorAdapter(Context c) {
            mContext = c;
            mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            images = NonzeroDayLab.get(mContext).getImages();
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView)v.findViewById(R.id.image_selector_image_view);
            imageView.setImageDrawable(images[position]);

            container.addView(imageView);

            return v;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}