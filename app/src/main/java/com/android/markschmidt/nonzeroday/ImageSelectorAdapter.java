package com.android.markschmidt.nonzeroday;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by markschmidt on 5/19/15.
 */
public class ImageSelectorAdapter extends RecyclerView.Adapter<ImageSelectorAdapter.ViewHolder> {
    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView mImageView;
        public ImageClickerListener mListener;

        public ViewHolder(ImageView itemView, ImageClickerListener listener) {
            super(itemView);
            mImageView = itemView;
            mListener = listener;
        }

        public void onClickImage(View v)
        {
            mListener.onClick(v, getPosition());
        }


        public interface ImageClickerListener
        {
            public void onClick(View v, int position);
        }
    }

    private Drawable[] mImages;
    private Context mContext;
    private AddObjectiveFragment mFragment;

    public ImageSelectorAdapter(Context c, Fragment f)
    {
        mContext = c;
        mImages = NonzeroDayLab.get(mContext).getImages();
        mFragment = (AddObjectiveFragment)f;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView v = (ImageView)parent.findViewById(R.id.image_selector_recycle_view_image);
        ViewHolder vh = new ViewHolder(v, new ViewHolder.ImageClickerListener() {
            @Override
            public void onClick(View v, int position) {

            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mImageView.setImageDrawable(mImages[position]);
    }

    @Override
    public int getItemCount() {
        return mImages.length;
    }




}
