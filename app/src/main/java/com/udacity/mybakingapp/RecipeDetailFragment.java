package com.udacity.mybakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.udacity.mybakingapp.adapters.StepsAdapter;
import com.udacity.mybakingapp.model.Ingredient;
import com.udacity.mybakingapp.model.Recipe;
import com.udacity.mybakingapp.model.Step;
import com.udacity.mybakingapp.utils.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecipeDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecipeDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailFragment extends Fragment implements StepsAdapter.StepsAdapterClickHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STEP_SHORT_DESCR="step_short_descr";
    private static final String ARG_PARAM1 = "param1";
    private int mRecipeId;
    private Recipe mRecipe;
    private ListView mListIngredients;

    @BindView(R.id.lv_Steps)                 MyRecyclerView mListSteps;
    @BindView(R.id.tv_ingredients_descr)     TextView       mIngredients_tv;
    @BindView(R.id.tv_recipe_title)          TextView       mTitle_tv;
    @BindView(R.id.recipe_detail_scrollView) ScrollView     mScrollView;
    private Unbinder unbinder;

    @Override
    public void onClick(View v, int position) {
        if (mListener!=null)
            mListener.onItemListSelected(position);
    }

    Context mContext;
    View mRootView;

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    public RecipeDetailFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment RecipeDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailFragment newInstance(String param1) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent prova = getActivity().getIntent();
        mRootView=inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        unbinder=ButterKnife.bind(this,mRootView);

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

/*
    worked when was implemented with ListView
    @OnItemClick(R.id.lv_Steps)
    public void onItemClick(View v, int position) {
        if (mListener!=null)
            mListener.onItemListSelected(position);
            //v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }
    */

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntArray("SCROLL_POSITION",  new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
        //Parcelable state = mListSteps.onSaveInstanceState();
        //outState.putParcelable("LIST_STATE",state);
        super.onSaveInstanceState(outState);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onItemListSelected(int position);
    }

    public void RefreshLists() {
        String ingredient_descr="";
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        ingredients = mRecipe.getIngredients();

        int numIngr=ingredients.size();
        StringBuilder sb = new StringBuilder();
        if (numIngr > 0) {
            for (int i = 0; i < numIngr; i++) {
                Ingredient ingredient=ingredients.get(i);
                ingredient_descr=ingredient.getQuantity()+" "+
                                 ingredient.getMeasure()+" "+
                                 ingredient.getIngredient();
                sb.append(ingredient_descr);
                if (i<numIngr-1) sb.append(", ");
            }
            ingredient_descr=sb.toString();
        }
        mIngredients_tv.setText(ingredient_descr);
        mTitle_tv.setText(mRecipe.getName());

        //List<Step> steps = new ArrayList<Step>();
        //steps = mRecipe.getSteps();
        List<Step> steps_list=mRecipe.getSteps();
        Step[] steps= (steps_list).toArray(new Step[steps_list.size()]);

        if (steps.length>0) {
            StepsAdapter sa=new StepsAdapter(this);

            RecyclerView.LayoutManager layoutManager;
            layoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
            mListSteps.setLayoutManager(layoutManager);
            mListSteps.setAdapter(sa);
            sa.setStepsData(steps);
        }
        /* mListSteps was a ListView and worked perfectly but rubric asked for recyclerview
        List<Step> steps = new ArrayList<Step>();
        steps = mRecipe.getSteps();

        if (steps.size() > 0) {

            ArrayList<HashMap<String, String>> ArrayListSteps = new ArrayList<>();
            for (int i = 0; i < steps.size(); i++) {
                HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
                String stepNumber="--";
                if (i>0)
                    stepNumber=Integer.toString(i)+". ";
                hashMap.put(STEP_SHORT_DESCR,  stepNumber+steps.get(i).getShortDescription());
                ArrayListSteps.add(hashMap);//add the hashmap into arrayList
            }

            String[] from = {STEP_SHORT_DESCR};
            int[]    to   = {R.id.tv_step_short_descr};

            SimpleAdapter si = new SimpleAdapter(getContext(), ArrayListSteps, R.layout.rv_steps_layout, from, to);
            mListSteps.setAdapter(si);
            mListSteps.setSelection(-1);
        }
        */

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int[] position ;
        if (savedInstanceState!=null) {
            position = savedInstanceState.getIntArray("SCROLL_POSITION");
            //mListSteps.onRestoreInstanceState(savedInstanceState.getParcelable("LIST_STATE"));
        }
        else
            position =new int[]{0,0};

        final int[] pos=position;
        if (pos!=null) {
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(pos[0], pos[1]);
                }
            });
        }

    }

    public void setRecipeID(int ID) {
        mRecipeId = ID;
        mRecipe   = JsonUtils.getRecipe(mRecipeId);
        RefreshLists();
    }

}
