package com.udacity.mybakingapp.adapters;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.mybakingapp.R;
import com.udacity.mybakingapp.data.ImageReplacer;
import com.udacity.mybakingapp.model.Recipe;
import com.udacity.mybakingapp.model.Step;


public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.ViewHolder> {

    public interface StepsAdapterClickHandler {
        void onClick(View v, int idx);
    }

    private Step[] mStepsData;
    private Context rcContext;
    private StepsAdapterClickHandler mClickHandler;

    public StepsAdapter(StepsAdapterClickHandler clickHandler) {
        mClickHandler=clickHandler;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView tv_short_descr;

        private ViewHolder(View view) {
            super(view);
            tv_short_descr =  view.findViewById(R.id.tv_step_short_descr);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int mIndexPosition = getAdapterPosition();
            int recipeID=mStepsData[mIndexPosition].getId();
            mClickHandler.onClick(v,recipeID);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rcContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(rcContext);
        View thisView = inflater.inflate(R.layout.rv_steps_layout, parent, false);
        return new ViewHolder(thisView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if ((holder != null) && (getItemCount() != 0)) {
            String content;
            if (mStepsData[position]!=null) {
                content=mStepsData[position].getShortDescription();
                holder.tv_short_descr.setText(content);
            }
        }
    }

    @Override
    public int getItemCount() {
        int len = 0;
        if (mStepsData != null)
            len = mStepsData.length;
        return len;
    }

    public void setStepsData(Step[] steps) {
        mStepsData = steps;
        notifyDataSetChanged();
    }

}
