package com.udacity.mybakingapp.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.mybakingapp.R;
import com.udacity.mybakingapp.model.Recipe;
import com.udacity.mybakingapp.model.TrailerItem;
import com.udacity.mybakingapp.utils.JsonUtils;


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private Recipe[] mRecipesData;
    private Context rcContext;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView  name_tv;
        public final ImageView imgThumbnail;

        public ViewHolder(View view) {
            super(view);
            name_tv      =  view.findViewById(R.id.tv_recipe);
            imgThumbnail =  view.findViewById(R.id.img_recipe);
            //view.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            /*
            String url = "https://www.youtube.com/watch?v=".concat(mRecipesData[position].getKey());
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            ActivityCompat.startActivity(v.getContext(),i,null);
            */
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rcContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(rcContext);
        View thisView = inflater.inflate(R.layout.rv_recipes_layout, parent, false);
        return new ViewHolder(thisView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if ((holder != null) && (getItemCount() != 0)) {
            String content=mRecipesData[position].getName();
            if (content.length()>90) content=content.substring(0,90);
            holder.name_tv.setText(String.format("%s...", content));
            //Set the thumbnail image
            String thumbnailURL = JsonUtils.makeThumbnailURL(mRecipesData[position].getImage());
            if (holder.imgThumbnail!=null)
                Picasso.with(rcContext).load(thumbnailURL).error(R.drawable.ic_error).into(holder.imgThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        int len = 0;
        if (mRecipesData != null)
            len = mRecipesData.length;
        return len;
    }

    public void setRecipesData(Recipe[] recipes) {
        mRecipesData = recipes;
        notifyDataSetChanged();
    }

}
