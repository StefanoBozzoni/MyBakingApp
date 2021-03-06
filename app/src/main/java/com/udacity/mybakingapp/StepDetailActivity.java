package com.udacity.mybakingapp;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class StepDetailActivity extends AppCompatActivity
             implements StepDetailFragment.OnFragmentInteractionListener {
    int mStepId  =-1;
    int mRecipeId=-1;

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent_Recipe=getIntent();  //From RecipeDetailActivity

        if (intent_Recipe!=null) {
            mStepId = intent_Recipe.getIntExtra("STEP_ID", 0);
            mRecipeId = intent_Recipe.getIntExtra("RECIPE_ID", 0);
        }

        setContentView(R.layout.activity_step_detail);
        if (mRecipeId != -1) {
            FragmentManager fm = getSupportFragmentManager();
            StepDetailFragment stepDetailFragment = (StepDetailFragment) fm.findFragmentById(R.id.fragment_step_detail);
            if (stepDetailFragment!=null) {
                stepDetailFragment.setStepId(mRecipeId, mStepId);
            }

        }
    }
}
