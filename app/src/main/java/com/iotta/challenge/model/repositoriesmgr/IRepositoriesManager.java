package com.iotta.challenge.model.repositoriesmgr;

import android.support.annotation.NonNull;

import java.util.List;

import com.iotta.challenge.model.pojo.Owner;
import com.iotta.challenge.model.pojo.Repository;

/**
 * Created by Galya on 07/07/2017.
 */

public interface IRepositoriesManager {

    interface IGetRepositoryCallback {

        void onSuccess(@NonNull List<Repository> tasks);

        void onFailed(@NonNull String errorDescription);

    }

    interface IGetRepositoryDetailsCallback {

        void onSuccess(@NonNull Repository repository);

        void onFailed(@NonNull String errorDescription);

    }

    void getRepositoryDetails(final @NonNull String id, @NonNull final IGetRepositoryDetailsCallback callback);

    void getRepositories(@NonNull final IGetRepositoryCallback callback);

    void refreshRepositories(@NonNull final IGetRepositoryCallback callback);

}
