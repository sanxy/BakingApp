package com.sanxynet.bakingapp.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.sanxynet.bakingapp.R;
import com.sanxynet.bakingapp.ui.BaseActivity;
import com.sanxynet.bakingapp.adapter.StepAdapter;
import com.sanxynet.bakingapp.db.BakingAppContract;
import com.sanxynet.bakingapp.utils.Costants;
import com.sanxynet.bakingapp.utils.PrefManager;
import com.sanxynet.bakingapp.utils.Utility;
import timber.log.Timber;

public class StepFragment extends Fragment implements StepAdapter.StepItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    @SuppressWarnings({"WeakerAccess", "CanBeFinal", "unused"})
    @BindView(R.id.rv_step)
    RecyclerView mRecyclerView;

    private StepAdapter mAdapter;
    private FragmentInteractionListener mListener;
    private static WeakReference<Integer> sWeakReference;

    public interface FragmentInteractionListener {
        void onFragmentInteraction(int id, int position);
    }

    public static StepFragment newInstance(int index) {
        StepFragment stepFragment = new StepFragment();

        Bundle args = new Bundle();
        args.putInt(Costants.EXTRA_STEP_ID, index);
        stepFragment.setArguments(args);

        return stepFragment;
    }

    private int getShownIndex() {
        return (getArguments() == null) ? -1 : getArguments().getInt(Costants.EXTRA_STEP_ID, 0);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int mDataId = getShownIndex();

        if (mDataId > 0) {
            sWeakReference = new WeakReference<>(mDataId);
            if (getActivity() != null) {
                getActivity().getSupportLoaderManager().initLoader(Costants.STEP_LOADER_ID, null, this);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_step, container, false);

        ButterKnife.bind(this, view);


        if (Utility.isTablet(getContext())) {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                GridLayoutManager gridLayoutManager;

                if ((getActivity() != null) && (getActivity().findViewById(R.id.exo_detail_step_fragment_player_view) == null)) {
                    gridLayoutManager = new GridLayoutManager(getActivity(), Integer.valueOf(getString(R.string.tablet_landscape_span_count_grid_step)));

                } else {
                    gridLayoutManager = new GridLayoutManager(getActivity(), 1);

                }
                mRecyclerView.setLayoutManager(gridLayoutManager);

            } else {

                GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                        Integer.valueOf(getString(R.string.tablet_portrait_span_count_grid_step)));
                mRecyclerView.setLayoutManager(gridLayoutManager);
            }


        } else {

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                if ((getActivity() != null) && (getActivity().findViewById(R.id.content_detail_tab_fragment) != null)) {
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                            Integer.valueOf(getString(R.string.tablet_portrait_span_count_grid_step)));
                    mRecyclerView.setLayoutManager(gridLayoutManager);
                } else {
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                    mRecyclerView.setLayoutManager(layoutManager);
                }

            } else {

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(layoutManager);

            }
        }

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new StepAdapter(this);

        mRecyclerView.setAdapter(mAdapter);

        if (PrefManager.isGeneralSettings(getActivity(), getActivity().getString(R.string.pref_tab_layout))) {
            LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_right);
            mRecyclerView.setLayoutAnimation(layoutAnimationController);
        }
        return view;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new StepFragmentAsyncTask(getActivity());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (mAdapter != null) {
            mAdapter.swapCursor(data);
        }

        int position = BaseActivity.getPositionStep();
        if (position == RecyclerView.NO_POSITION) position = 0;
        updatePosition(position);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }

    @Override
    public void onStepItemClick(int id, int position) {
        mListener.onFragmentInteraction(id, position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentInteractionListener) getActivity();

        } catch (ClassCastException e) {
            if (getActivity() != null) {
                throw new ClassCastException(getActivity().getLocalClassName() + "must implement OnFragmentStepInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updatePosition(int position) {
        if (mAdapter != null) {
            mRecyclerView.smoothScrollToPosition(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    private static class StepFragmentAsyncTask extends AsyncTaskLoader<Cursor> {

        Cursor stepData = null;

        final String[] mProjection = new String[]{
                BakingAppContract.StepEntry._ID,
                BakingAppContract.StepEntry.COLUMN_NAME_ID,
                BakingAppContract.StepEntry.COLUMN_NAME_SHORT_DESCRIPTION,
                BakingAppContract.StepEntry.COLUMN_NAME_DESCRIPTION,
                BakingAppContract.StepEntry.COLUMN_NAME_THUMBNAIL_URL,
                BakingAppContract.StepEntry.COLUMN_NAME_VIDEO_URL,

        };

        StepFragmentAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected void onStartLoading() {
            if (stepData != null) {
                deliverResult(stepData);
            } else {
                forceLoad();
            }
        }

        @Override
        public Cursor loadInBackground() {
            try {
                if (sWeakReference != null) {
                    return getContext().getContentResolver().query(BakingAppContract.StepEntry.CONTENT_URI,
                            mProjection,
                            BakingAppContract.StepEntry.COLUMN_NAME_RECIPES_ID + " = ?",
                            new String[]{String.valueOf(sWeakReference.get())},
                            BakingAppContract.StepEntry._ID + " ASC");

                } else {
                    return null;
                }

            } catch (Exception e) {
                Timber.e("Failed to asynchronously load data. ");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void deliverResult(Cursor data) {
            stepData = data;
            super.deliverResult(data);
        }
    }
}
