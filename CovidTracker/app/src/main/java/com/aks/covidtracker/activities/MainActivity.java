package com.aks.covidtracker.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aks.covidtracker.Constants;
import com.aks.covidtracker.FeedQuery;
import com.aks.covidtracker.HistoryallQuery;
import com.aks.covidtracker.adapters.OverviewAdapter;
import com.aks.covidtracker.R;
import com.aks.covidtracker.models.OverviewItem;
import com.aks.covidtracker.utility.QueryUtils;
import com.alespero.expandablecardview.ExpandableCardView;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.cache.http.HttpCachePolicy;
import com.apollographql.apollo.cache.http.ApolloHttpCache;
import com.apollographql.apollo.cache.http.DiskLruHttpCacheStore;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements OverviewAdapter.OnStateClickListener
/*implements LoaderManager.LoaderCallbacks<List<OverviewItem>> */ {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerviewAdapter;
    List<OverviewItem> dataList;
    List<FeedQuery.State> statesList = null;

    private ApolloClient apolloClient = null;
    private static final String BASE_URL = Constants.BASE_URL;
    ProgressBar loadingSpinner = null;
    TextView errorTextView = null;
    ExpandableCardView expandableCardView = null;
    DiskLruHttpCacheStore cacheStore = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.main_activity_title));

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();

        loadingSpinner = findViewById(R.id.loading_spinner);
        errorTextView = findViewById(R.id.empty_view);
        errorTextView.setVisibility(View.GONE);


        /* Placeholder data */
//        for(int i=0;i<10;i++){
//            OverviewItem item = new OverviewItem(
//                    "State "+i,
//                    ""+i*5,
//                    ""+i*4*0.9,
//                    ""+i*2,
//                    ""+i,
//                    "State "+i,
//                    ""+i*5,
//                    ""+i*4*0.9,
//                    ""+i*2
//            );
//
//            dataList.add(item);
//        }
//        loadingSpinner.setVisibility(View.GONE);
//        errorTextView.setVisibility(View.GONE);
        recyclerviewAdapter = new OverviewAdapter(dataList, this, MainActivity.this);
        recyclerView.setAdapter(recyclerviewAdapter);

        // Apollo LRU cache
        File file = new File(getApplicationContext().getFilesDir(), "covid-states");
        long size = 1024*1024;
        cacheStore = new DiskLruHttpCacheStore(file, size);
        OkHttpClient okHttpClient = QueryUtils.initOkHttpClient();

//        apolloClient = ApolloClient.builder()
//                .serverUrl(BASE_URL)
//                .httpCache(new ApolloHttpCache(cacheStore))
//                .okHttpClient(okHttpClient)
//                .build();
        apolloClient = QueryUtils.buildApolloClient(okHttpClient, cacheStore);

        apolloClient.query(FeedQuery.builder()
        .build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(4, TimeUnit.HOURS))
                .enqueue(new ApolloCall.Callback<FeedQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<FeedQuery.Data> response) {
                //Log.w(TAG+ response.getData().country().states().get(0),""+response.getData().country().states().get(0));

               statesList = response.getData().country().states();
                for(FeedQuery.State s: statesList){
                    try {
                        String stateName = s.state();
                        if (stateName.equals("Total") || stateName.equals("total") || stateName.equals("TOTAL"))
                            stateName = "India";
                        Integer cases = s.cases();
                        Integer recovered = s.recovered();
                        Integer deceased = s.deaths();
                        Integer todayCases = s.todayCases();
                        Integer todayRecovered = s.todayRecovered();
                        Integer todayDeceased = s.todayDeaths();

                        Integer active = cases - recovered - deceased;
                        Integer activeToday = todayCases - todayRecovered - todayDeceased;
                        OverviewItem overviewItem = new OverviewItem(stateName, QueryUtils.formatNumbers(cases.toString()),
                                QueryUtils.formatNumbers(active.toString()),
                                QueryUtils.formatNumbers(recovered.toString()),
                                QueryUtils.formatNumbers(deceased.toString()),
                                QueryUtils.formatNumbers(todayCases.toString()),
                                QueryUtils.formatNumbers(activeToday.toString()),
                                QueryUtils.formatNumbers(todayRecovered.toString()),
                                QueryUtils.formatNumbers(todayDeceased.toString()));

                        overviewItem.setRecovery_rate(QueryUtils.calcRecoveryRate(cases, recovered));
                        overviewItem.setMortality_rate(QueryUtils.calcMortalityRate(cases, deceased));

                        dataList.add(overviewItem);
                    }catch (Exception e){
                        Log.e(TAG, "Error in processing a state item", e);
                    }
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingSpinner.setVisibility(View.GONE);
                        errorTextView.setVisibility(View.GONE);
                        recyclerviewAdapter = new OverviewAdapter(dataList, getApplicationContext(), MainActivity.this);
                        recyclerView.setAdapter(recyclerviewAdapter);
                    }
                });
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                if(!QueryUtils.checkInternetConnectivity(getApplicationContext())) {
                    Log.i(TAG, "onCreate: No Network Connection! Finishing");
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadingSpinner.setVisibility(View.GONE);
                            errorTextView.setVisibility(View.VISIBLE);
                            errorTextView.setText("No Internet Connection!");
                            showDialog(MainActivity.this);
                        }
                    });
                }
                Log.e(TAG, "Fail to load data",e);
            }
        });

    }

    @Override
    public void onStateClick(int position) {
        OverviewItem item = dataList.get(position);
        //TODO: when an item on recycler view is clicked
        Log.i(TAG, "onStateClick: "+item.getState()+" clicked.");
        //if(item.getState().equalsIgnoreCase("INDIA")){
            Log.i(TAG, "onStateClick: Opening GraphActivity");
            Intent intent = new Intent(this, GraphActivity.class);
            intent.putExtra("state", item.getState());
            startActivity(intent);
        //}
    }


    public void showDialog(Context context){
        new AlertDialog.Builder(context)
                .setTitle("No Internet Connection!")
                .setMessage("The app needs an active internet connection to fetch latest data. Connect to internet and restart the app.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.info_item) {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
