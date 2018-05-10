package com.udacity.mybakingapp;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class RecipeDetailActivity extends AppCompatActivity
        implements RecipeDetailFragment.OnFragmentInteractionListener,
                   StepDetailFragment.OnFragmentInteractionListener {
    int mRecipeId=-1;

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

    @Override
    public void onItemListSelected(int position) {
        if (getResources().getBoolean(R.bool.isTablet)) {
            FragmentManager fm = getSupportFragmentManager();
            StepDetailFragment frag_step_detail = new StepDetailFragment();
            frag_step_detail.setStepId(mRecipeId,position);
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_playerContainer, frag_step_detail).commit();
        }
        else {
            Intent intent = new Intent(this, StepDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int recipeId=-1;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if  ((extras!=null) && intent.hasExtra("RECIPE_ID")) {
            recipeId = extras.getInt("RECIPE_ID");
            mRecipeId=recipeId;
            setContentView(R.layout.activity_recipe_detail);
            FragmentManager fm = getSupportFragmentManager();
            RecipeDetailFragment recipeDetailFragment = (RecipeDetailFragment) fm.findFragmentById(R.id.fragment_recipe_detail);
            recipeDetailFragment.setRecipeID(recipeId);
        }

        //if it's a Tablet then load the fragment in the FrameLayout Container
        if ((savedInstanceState==null) && (getResources().getBoolean(R.bool.isTablet))) {
            //Fragment frag_step_detail = getFragmentManager().findFragmentById(R.id.fragment_step_detail);
            StepDetailFragment frag_step_detail = new StepDetailFragment();
            //FrameLayout containerView = (FrameLayout) findViewById(R.id.fl_playerContainer);
            getSupportFragmentManager().beginTransaction().add(R.id.fl_playerContainer, frag_step_detail).commit();
        }


    }
}
