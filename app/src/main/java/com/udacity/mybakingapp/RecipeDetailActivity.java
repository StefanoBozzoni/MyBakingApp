package com.udacity.mybakingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.udacity.mybakingapp.model.Ingredient;
import com.udacity.mybakingapp.model.Recipe;
import com.udacity.mybakingapp.model.Step;
import com.udacity.mybakingapp.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;

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
            intent.putExtra(Defines.STEP_ID,stepId);
            intent.putExtra(Defines.RECIPE_ID,mRecipeId);
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
            mRecipeId=savedInstanceState.getInt(Defines.RECIPE_ID);
            mStepId=savedInstanceState.getInt(Defines.STEP_ID);
        } else {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if  ((extras!=null) && intent.hasExtra(Defines.RECIPE_ID)) {
                mRecipeId = extras.getInt(Defines.RECIPE_ID);
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_recipe_detail,menu);
        return true;
    }

    private String getRecipeDescription(Recipe recipe) {
        List<Ingredient> ingredients=recipe.getIngredients();
        String ingredient_descr;
        int numIngr=ingredients.size();
        StringBuilder sb = new StringBuilder();
        if (numIngr > 0) {
            for (int i = 0; i < numIngr; i++) {
                if (i>0) {
                    Ingredient ingredient = ingredients.get(i);
                    ingredient_descr = ingredient.getQuantity() + " " +
                            ingredient.getMeasure() + " " +
                            ingredient.getIngredient();
                    sb.append(ingredient_descr);
                    sb.append("\r\n");
                    //if (i<numIngr-1) sb.append(", ");
                } else {
                    sb.append(recipe.getName().toUpperCase());
                    sb.append("\r\n");
                }
            }
        }
        ingredient_descr=sb.toString();
        return ingredient_descr;
    }

    public void onAddToWidgetMenuClick(MenuItem item) {
        SharedPreferences prefs = getSharedPreferences(Defines.BAKINGAPP_WIDGET, MODE_PRIVATE);
        Recipe recipe=JsonUtils.getRecipe(mRecipeId);
        if (recipe!=null) {
            String ingredients = this.getRecipeDescription(recipe);
            prefs.edit().putInt(Defines.RECIPE_ID, mRecipeId)
                    .putString(Defines.INGREDIENTS, ingredients).apply();

            Toast.makeText(this, "Widget configured", Toast.LENGTH_SHORT).show();
        }
    }
}
