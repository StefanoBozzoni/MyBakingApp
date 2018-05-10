package com.udacity.mybakingapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String SELECTED_POSITION="PLAYER_POSITION";
    private static final String RECIPE_ID="RECIPE_ID";
    private static final String STEP_ID="STEP_ID";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
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
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepDetailFragment newInstance(String param1, String param2) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();
        //mStepId=-1;
        //mRecipeId=-1;

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mStartPosition=0;
        if (savedInstanceState!=null) {
            mStartPosition = savedInstanceState.getLong(SELECTED_POSITION);
            mRecipeId      = savedInstanceState.getInt(RECIPE_ID);
            mStepId        = savedInstanceState.getInt(STEP_ID);
        }

        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_step_detail, container, false);
        mRootView =rootView;
        mPlayerView= (PlayerView) rootView.findViewById(R.id.exoplayer);
        //initializePlayer()
        if (mRecipeId!=-1) {
            String step_description = JsonUtils.getRecipe(mRecipeId).getSteps().get(mStepId).getDescription();
            TextView tv_step_descr = (TextView) mRootView.findViewById(R.id.tv_step_description);
            tv_step_descr.setText(step_description);
        }
        return rootView;

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

    private void initializePlayer() {
        if (mRecipeId!=-1) {
            String videoUrl = JsonUtils.getRecipe(mRecipeId).getSteps().get(mStepId).getVideoURL();
            if (!videoUrl.isEmpty()) {
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
                mRootView.findViewById(R.id.exoplayer).setVisibility(View.GONE);
                mRootView.findViewById(R.id.tv_NovVideoAvailable).setVisibility(View.VISIBLE);
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
        if (Util.SDK_INT <= 23) {
            releasePlayer();
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
        /*
        if (Util.SDK_INT > 23) {
            initializePlayer();
        }
        */
    }

    @Override
    public void onResume() {
        super.onResume();

        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();

        if ((Util.SDK_INT <= 23) || (player==null)) {
            initializePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();
        long position=player.getCurrentPosition();
        currentState.putLong(SELECTED_POSITION, position);
        currentState.putLong(RECIPE_ID, mRecipeId);
        currentState.putLong(STEP_ID, mStepId);

        super.onSaveInstanceState(currentState);
    }

    private void releasePlayer() {
        SimpleExoPlayer player= (SimpleExoPlayer) mPlayerView.getPlayer();
        if (mPlayerView.getPlayer() != null) {
            //playbackPosition = player.getCurrentPosition();
            //currentWindow = player.getCurrentWindowIndex();
            //playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }
}
