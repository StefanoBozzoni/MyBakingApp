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


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    public interface RecipeAdapterClickHandler {
        void onClick(View v, int idx);
    }

    private Recipe[] mRecipesData;
    private Context rcContext;
    private RecipeAdapterClickHandler mClickHandler;
    static private ImageReplacer imageReplacer=null;

    public RecipesAdapter(RecipeAdapterClickHandler clickHandler) {
        mClickHandler=clickHandler;
        imageReplacer=new ImageReplacer();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView  name_tv;
        public final ImageView imgThumbnail;

        public ViewHolder(View view) {
            super(view);
            name_tv      =  view.findViewById(R.id.tv_recipe);
            imgThumbnail =  view.findViewById(R.id.img_recipe);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int mIndexPosition = getAdapterPosition();
            int recipeID=mRecipesData[mIndexPosition].getId();
            mClickHandler.onClick(v,recipeID);
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
            if ((mRecipesData[position]!=null)  && (holder.imgThumbnail!=null)) {
                int numSteps=mRecipesData[position].getSteps().size();
                String imageUrl = mRecipesData[position].getImage();
                int recipeId=mRecipesData[position].getId();
                if (imageUrl.isEmpty() && (recipeId>0))
                    imageUrl=imageReplacer.findThumbnailByRecipeId(recipeId);
                if (!imageUrl.isEmpty()) {
                    Picasso.with(rcContext).load(imageUrl).error(R.drawable.ic_error).into(holder.imgThumbnail);
                }
                else {
                    Bitmap bmp=mRecipesData[position].getThumbnail();
                    Drawable verticalImage = new BitmapDrawable(rcContext.getResources(), bmp);
                    holder.imgThumbnail.setImageDrawable(verticalImage);
                }
            }
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
