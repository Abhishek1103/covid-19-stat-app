//package com.aks.covidtracker;
//
//import android.content.Context;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//import androidx.loader.content.AsyncTaskLoader;
//
//import com.apollographql.apollo.ApolloCall;
//import com.apollographql.apollo.ApolloClient;
//import com.apollographql.apollo.api.Response;
//import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
//import com.apollographql.apollo.cache.http.ApolloHttpCache;
//import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
//import com.apollographql.apollo.exception.ApolloException;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.io.File;
//import java.util.List;
//
//import okhttp3.OkHttpClient;
//
//public class OverviewLoader extends AsyncTaskLoader<List<OverviewItem>> {
//
//    private static final String LOG_TAG = OverviewLoader.class.getName();
//    private static final String BASE_URL = "https://covidstat.info/graphql";
//
//    Context context = null;
//    private ApolloClient apolloClient = null;
//    List<FeedQuery.State> statesList = null;
//
//    public OverviewLoader(Context context){
//        super(context);
//        this.context = context;
//    }
//
//    @Nullable
//    @Override
//    public List<OverviewItem> loadInBackground() {
//
//        final List<OverviewItem> resultList = null;
//
//        File file = new File(context.getFilesDir(), "covid-states");
//        long size = 1024*1024;
//        DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file, size);
//
//        OkHttpClient okHttpClient = null;
//        try {
//            okHttpClient = new OkHttpClient.Builder().build();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//
//        apolloClient = ApolloClient.builder()
//                .serverUrl(BASE_URL)
//                .httpCache(new ApolloHttpCache(cacheStore))
//                .okHttpClient(okHttpClient)
//                .build();
//
//        apolloClient.query(FeedQuery.builder()
//
//                .build())
//                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
//                .enqueue(new ApolloCall.Callback<FeedQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<FeedQuery.Data> response) {
//                        Log.w("MainActivity "+ response.getData().country.states.get(0),""+response.getData().country.states.get(0));
//                        statesList = response.getData().country.states();
//                        for(FeedQuery.State s: statesList){
//                            String stateName = s.state;
//                            if(stateName.equals("Total")||stateName.equals("total")||stateName.equals("TOTAL"))
//                                stateName = "India";
//                            Integer cases = s.cases;
//                            Integer recovered = s.recovered;
//                            Integer deceased = s.deaths;
//                            Integer active = cases - recovered - deceased;
//                            resultList.add(new OverviewItem(stateName, cases.toString(),
//                                    active.toString(),
//                                    recovered.toString(),
//                                    deceased.toString()));
//                        }
//                        System.out.println("****************Response Received***************************");
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//                        Log.e("Fail to do", "Fail fail fail.............");
//                    }
//                });
//
//        System.out.println("************************In Background returning****************************");
//        return resultList;
//    }
//}
