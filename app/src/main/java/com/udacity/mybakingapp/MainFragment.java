package com.udacity.mybakingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.mybakingapp.adapters.RecipesAdapter;
import com.udacity.mybakingapp.model.Recipe;
import com.udacity.mybakingapp.utils.JsonUtils;

import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by stefa on 05/05/2018.
 */

public class MainFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Recipe[]>,
                    RecipesAdapter.RecipeAdapterClickHandler {

    @BindView(R.id.rv_recipes_main)  MyRecyclerView mRecyclerView;
    private Unbinder unbinder;

    private static int ID_LOADER_RECIPES=100;
    Context mContext;
    Recipe[] mRecipesData;

    @Override
    public void onClick(View v,int recipeIndex) {
        Intent intent = new Intent(mContext,RecipeDetailActivity.class);
        intent.putExtra("RECIPE_ID",recipeIndex-1);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        /* set that it has a menu */
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.fragment_main,container,false);
        unbinder=ButterKnife.bind(this,rootView);
        LoaderManager lm = getLoaderManager();
        lm.initLoader(ID_LOADER_RECIPES,null,this);

        RecyclerView.LayoutManager layoutManager;
        if (getResources().getBoolean(R.bool.isTablet))
            layoutManager = new GridLayoutManager(mContext,2);
        else
            layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        RecipesAdapter ra=new RecipesAdapter(this);
        mRecyclerView.setAdapter(ra);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Recipe[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Recipe[]>(mContext) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public Recipe[] loadInBackground() {
                URL RecipesURL;
                RecipesURL = JsonUtils.buildUrl(JsonUtils.RECIPES_BASE_URL,"");
                Recipe[] recipes;
                try {
                    String jsonRecipesResponse = JsonUtils.getResponseFromHttpUrl(RecipesURL);
                    recipes = JsonUtils.parseRecipesJson(jsonRecipesResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return recipes;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Recipe[]> loader, Recipe[] data) {
       mRecipesData=data;
       RecipesAdapter ra=new RecipesAdapter(this);
       ra.setRecipesData(mRecipesData);
       mRecyclerView.setAdapter(ra);

    }

    @Override
    public void onLoaderReset(Loader<Recipe[]> loader) {

    }

}
