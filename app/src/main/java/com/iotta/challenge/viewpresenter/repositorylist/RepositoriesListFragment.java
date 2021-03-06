package com.iotta.challenge.viewpresenter.repositorylist;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.iotta.challenge.R;
import com.iotta.challenge.model.pojo.Repository;
import com.iotta.challenge.utils.Logger;
import com.iotta.challenge.viewpresenter.repositorydetails.DetailsActivity;

/**
 * A fragment representing a list of Items.
 */
public class RepositoriesListFragment extends Fragment implements RepositoriesContract.IView {

    private Handler mHandler;
    private RepositoriesContract.IPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyRepositoryLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Repository> mRepositories;
    private Spinner mSortSpinner;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RepositoriesListFragment() {
    }

    public static RepositoriesListFragment newInstance() {
        return new RepositoriesListFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_repositories, container, false);
        mSwipeRefreshLayout = ((SwipeRefreshLayout) root);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadRepositories(false);
            }
        });

        mSortSpinner = (Spinner) root.findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.sort_options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(adapter);
        mSortSpinner.setVisibility(View.GONE);

        mSortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mPresenter.setFiltering(RepositoryFilterType.UPDATE_DATE_ASC);
                } else {
                    mPresenter.setFiltering(RepositoryFilterType.UPDATE_DATE_DSC);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRecyclerView = (RecyclerView) root.findViewById(R.id.repositories_list);
        mRecyclerView.setVisibility(View.GONE);

        mEmptyRepositoryLayout = (LinearLayout) root.findViewById(R.id.emptyRepositoryLayout);
        mEmptyRepositoryLayout.setVisibility(View.GONE);
        mHandler = new Handler(Looper.getMainLooper());
        return root;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }



    @Override
    public void showRepositories(List<Repository> repositories) {
        assert mRecyclerView != null;
        mRepositories = (ArrayList<Repository>) repositories;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                // use a linear layout manager
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(RepositoriesListFragment.this.getContext());
                mRecyclerView.setLayoutManager(mLayoutManager);
                mEmptyRepositoryLayout.setVisibility(View.GONE);
                mSortSpinner.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);

                mRecyclerView.setAdapter(new RepositoriesListRecyclerViewAdapter(mRepositories, new RepositoriesListRecyclerViewAdapter.IRepositoryItemListener() {
                    @Override
                    public void onRepositoryClick(String repositoryId) {
                        Logger.debug("Repository "+repositoryId+" was selected");
                        mPresenter.openRepositoryDetails(repositoryId);
                    }
                }));
            }
        });

    }


    @Override
    public void showRepositoryDetailsUi(@NonNull String repositoryId) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_REPOSITORY_ID, repositoryId);
        //Start activity internally executes in the UI thread, no need to switch manually
        startActivity(intent);
    }

    @Override
    public void showLoadingError(final String errorMessage) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(RepositoriesListFragment.this.getContext(), errorMessage, Toast.LENGTH_SHORT).show();


                if(mRecyclerView == null || mRecyclerView.getVisibility() == View.GONE){
                    mEmptyRepositoryLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void showNoRepositories() {
        mEmptyRepositoryLayout.setVisibility(View.VISIBLE);
        if(mRecyclerView != null){
            mRecyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void setPresenter(@NonNull RepositoriesContract.IPresenter IPresenter) {
        mPresenter = IPresenter;
    }

}
