package com.udacity.mybakingapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeDetailFragment.OnFragmentInteractionListener,
                   StepDetailFragment.OnFragmentInteractionListener {
    int mRecipeId=-1;
    int mStepId  =-1;
    StepDetailFragment mStepDetailFragment;

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

    @Override
    public void onItemListSelected(int stepId) {  //called when an item is selected in a fragment
        mStepId=stepId;
        if (getResources().getBoolean(R.bool.isTablet)) { //if tablet...
            FragmentManager fm = getSupportFragmentManager();
            StepDetailFragment frag_step_detail = new StepDetailFragment();
            frag_step_detail.setStepId(mRecipeId,stepId);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_playerContainer, frag_step_detail).commit();
        }
        else { //if phone...
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("STEP_ID",stepId);
            intent.putExtra("RECIPE_ID",mRecipeId);
            startActivity(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("RECIPE_ID",mRecipeId);
        outState.putInt("STEP_ID"  ,mStepId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState!=null) {
            mRecipeId=savedInstanceState.getInt("RECIPE_ID");
            mStepId=savedInstanceState.getInt("STEP_ID");
        } else {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if  ((extras!=null) && intent.hasExtra("RECIPE_ID")) {
                mRecipeId = extras.getInt("RECIPE_ID");
            }
        }

        if  (mRecipeId>=0) {
            setContentView(R.layout.activity_recipe_detail);
            FragmentManager fm = getSupportFragmentManager();
            RecipeDetailFragment recipeDetailFragment = (RecipeDetailFragment) fm.findFragmentById(R.id.fragment_recipe_detail);
            recipeDetailFragment.setRecipeID(mRecipeId);

            //if it's a Tablet then load the fragment in the FrameLayout Container
            if (getResources().getBoolean(R.bool.isTablet)) {
                Fragment frag_step_detail = getSupportFragmentManager().findFragmentById(R.id.fl_playerContainer);
                if ((frag_step_detail==null) && (savedInstanceState==null)) {
                    StepDetailFragment new_frag_step_detail = StepDetailFragment.newInstance(mRecipeId);
                    getSupportFragmentManager().beginTransaction().add(R.id.fl_playerContainer, new_frag_step_detail).commit();
                }
            };
        }

    }
}
