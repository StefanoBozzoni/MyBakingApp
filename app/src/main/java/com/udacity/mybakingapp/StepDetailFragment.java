package com.udacity.mybakingapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.udacity.mybakingapp.data.ImageReplacer;
import com.udacity.mybakingapp.model.Step;
import com.udacity.mybakingapp.utils.JsonUtils;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StepDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepDetailFragment extends Fragment {

    private Context mContext;
    private PlayerView mPlayerView;

    private static final String ARG_PARAM1 = "param1";
    private static final String SELECTED_POSITION="PLAYER_POSITION";
    private static final String RECIPE_ID="RECIPE_ID";
    private static final String STEP_ID="STEP_ID";

    int mStepId=0;
    private int mRecipeId=-1;
    private long mStartPosition;

    private OnFragmentInteractionListener mListener;
    private View mRootView;


    public StepDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipeId id recipe.
     * @return A new instance of fragment StepFragment.
     */
    public static StepDetailFragment newInstance(int recipeId) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();

        if (getArguments() != null) {
            mRecipeId = getArguments().getInt(ARG_PARAM1);
        }
        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mStartPosition=0;
        if (savedInstanceState!=null) {
            mStartPosition = savedInstanceState.getLong(SELECTED_POSITION);
            mRecipeId      = savedInstanceState.getInt(RECIPE_ID);
            mStepId        = savedInstanceState.getInt(STEP_ID);
        }

        View rootView;
        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
           && !getResources().getBoolean(R.bool.isTablet))
            // Inflate the layout for this fragment
            rootView= inflater.inflate(R.layout.fragment_step_detail_land, container, false);
        else
            rootView= inflater.inflate(R.layout.fragment_step_detail, container, false);

        mRootView =rootView;
        mPlayerView= (PlayerView) rootView.findViewById(R.id.exoplayer);
        //initializePlayer()
        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mRecipeId!=-1) {
            Step currStep = JsonUtils.getRecipe(mRecipeId).getSteps().get(mStepId);
            String step_description = currStep.getDescription();
            String step_title="";
            if (mStepId==0)
                step_title = currStep.getShortDescription();
            else
                step_title = Integer.toString(mStepId)+". "+currStep.getShortDescription();
            TextView tv_step_descr  = (TextView) mRootView.findViewById(R.id.tv_step_description);
            TextView tv_step_title  = (TextView) mRootView.findViewById(R.id.tv_step_title);
            tv_step_descr.setText(step_description);
            tv_step_title.setText(step_title);
        }

    }

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

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("exoplayer-codelab")).
                createMediaSource(uri);
    }


    @SuppressLint("InlinedApi")
    private void hideSystemUi() {

        if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            && !getResources().getBoolean(R.bool.isTablet))   {
            mRootView.findViewById(R.id.tv_step_description).setVisibility(View.GONE);

            mPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(mContext, "prova conf", Toast.LENGTH_SHORT).show();
    }


    private void initializePlayer() {
        if ((mRecipeId!=-1) && (mStepId>=0)) {

            Step step=JsonUtils.getRecipe(mRecipeId).getSteps().get(mStepId);
            String videoUrl = step.getVideoURL();
            String imageUrl = step.getThumbnailURL();
            ImageView thumbnailView= (ImageView) mRootView.findViewById(R.id.stepsThumbnail);

            if (!videoUrl.isEmpty()) {

                mPlayerView.setVisibility(View.VISIBLE);
                thumbnailView.setVisibility(View.GONE);
                mRootView.findViewById(R.id.tv_NovVideoAvailable).setVisibility(View.GONE);
                SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(
                        new DefaultRenderersFactory(mContext),
                        new DefaultTrackSelector(), new DefaultLoadControl());
                mPlayerView.setPlayer(player);
                player.setPlayWhenReady(true);
                player.seekTo(player.getCurrentWindowIndex(), mStartPosition);
                Uri uri;
                uri = Uri.parse(videoUrl);
                MediaSource mediaSource = buildMediaSource(uri);
                boolean resetPosition=true;
                if (mStartPosition!=0) resetPosition=false;
                player.prepare(mediaSource, resetPosition, false);

            }
            else
            {
                if (!imageUrl.isEmpty()) {
                    mPlayerView.setVisibility(View.GONE);
                    thumbnailView.setVisibility(View.VISIBLE);
                    mRootView.findViewById(R.id.tv_NovVideoAvailable).setVisibility(View.GONE);
                    Picasso.with(mContext).load(imageUrl).error(R.drawable.ic_error).into(thumbnailView);
                }
                else {
                    mPlayerView.setVisibility(View.INVISIBLE);
                    thumbnailView.setVisibility(View.GONE);
                    mRootView.findViewById(R.id.tv_NovVideoAvailable).setVisibility(View.VISIBLE);
                }

            }
        }
    }

    public void setStepId(int recipeId,int stepId) {
        mStepId=stepId;
        mRecipeId=recipeId;
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onPause() {
        super.onPause();
        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();
        if (player!=null) {
            mStartPosition=player.getCurrentPosition();
            player.setPlayWhenReady(false); //pause the player
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();

        if (Util.SDK_INT > 23) {
            initializePlayer();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();

        if ((Util.SDK_INT <= 23) || (player==null)) {
            initializePlayer();
        }

        hideSystemUi();
        //ExoPlayerActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN || View.SYSTEM_UI_FLAG_IMMERSIVE)
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();
        long position=0;
        if (player!=null)
            position=player.getCurrentPosition();
        currentState.putLong(SELECTED_POSITION, position);
        currentState.putLong(RECIPE_ID, mRecipeId);
        currentState.putLong(STEP_ID, mStepId);

        super.onSaveInstanceState(currentState);
    }

    private void releasePlayer() {
        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();
        if (player != null) {
            //playbackPosition = player.getCurrentPosition();
            //currentWindow = player.getCurrentWindowIndex();
            //playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
}
