package com.udacity.mybakingapp;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnFragmentInteractionListener {

    @Override
    public void onFragmentInteraction(Uri uri) {
        //
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent intent   = getIntent();
        Bundle extras   = intent.getExtras();

        if ((extras!=null) && intent.hasExtra("RECIPE_ID")) {
            int RecipeId;
            RecipeId = extras.getInt("RECIPE_ID");
            FragmentManager fm =getSupportFragmentManager();
            RecipeDetailFragment recipeDetailFragment = (RecipeDetailFragment) fm.findFragmentById(R.id.fragment_recipe_detail);
            recipeDetailFragment.setRecipeID(RecipeId);
        }

    }
}
