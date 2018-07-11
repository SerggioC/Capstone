package com.sergiocruz.capstone.repository;

public abstract class NetworkBoundResource<ResultType, RequestType> {
//    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();
//
//    @MainThread
//    NetworkBoundResource() {
//        result.setValue(Resource.loading(null));
//        LiveData<ResultType> dbSource = loadFromDb();
//        result.addSource(dbSource, new Observer<ResultType>() {
//            @Override
//            public void onChanged(@Nullable ResultType data) {
//                result.removeSource(dbSource);
//                if (NetworkBoundResource.this.shouldFetch(data)) {
//                    NetworkBoundResource.this.fetchFromNetwork(dbSource);
//                } else {
//                    result.addSource(dbSource,
//                            new Observer<ResultType>() {
//                                @Override
//                                public void onChanged(@Nullable ResultType newData) {
//                                    result.setValue(Resource.success(newData));
//                                }
//                            });
//                }
//            }
//        });
//    }
//
//    private void fetchFromNetwork(final LiveData<ResultType> dbSource) {
//        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
//        // we re-attach dbSource as a new source,
//        // it will dispatch its latest value quickly
//        result.addSource(dbSource,
//                new Observer<ResultType>() {
//                    @Override
//                    public void onChanged(@Nullable ResultType newData) {
//                        result.setValue(Resource.loading(newData));
//                    }
//                });
//        result.addSource(apiResponse, new Observer<S>() {
//            @Override
//            public void onChanged(@Nullable S response) {
//                result.removeSource(apiResponse);
//                result.removeSource(dbSource);
//                //noinspection ConstantConditions
//                if (response.isSuccessful()) {
//                    NetworkBoundResource.this.saveResultAndReInit(response);
//                } else {
//                    NetworkBoundResource.this.onFetchFailed();
//                    result.addSource(dbSource,
//                            newData -> result.setValue(
//                                    Resource.error(response.errorMessage, newData)));
//                }
//            }
//        });
//    }
//
//    @MainThread
//    private void saveResultAndReInit(ApiResponse<RequestType> response) {
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                saveCallResult(response.body);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                // we specially request a new live data,
//                // otherwise we will get immediately last cached value,
//                // which may not be updated with latest results received from network.
//                result.addSource(loadFromDb(),
//                        newData -> result.setValue(Resource.success(newData)));
//            }
//        }.execute();
//    }
//
//    // Called to save the result of the API response into the database
//    @WorkerThread
//    protected abstract void saveCallResult(@NonNull RequestType item);
//
//    // Called with the data in the database to decide whether it should be
//    // fetched from the network.
//    @MainThread
//    protected abstract boolean shouldFetch(@Nullable ResultType data);
//
//    // Called to get the cached data from the database
//    @NonNull @MainThread
//    protected abstract LiveData<ResultType> loadFromDb();
//
//    // Called to create the API call.
//    @NonNull @MainThread
//    protected abstract LiveData<ApiResponse<RequestType>> createCall();
//
//    // Called when the fetch fails. The child class may want to reset components
//    // like rate limiter.
//    @MainThread
//    protected void onFetchFailed() {
//    }
//
//    // returns a LiveData that represents the resource, implemented
//    // in the base class.
//
//    public final LiveData<Resource<ResultType>> getAsLiveData() {
//        return result;
//    }
}