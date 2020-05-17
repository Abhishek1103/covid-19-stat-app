package com.aks.covidtracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.aks.covidtracker.Constants;
import com.aks.covidtracker.FeedQuery;
import com.aks.covidtracker.HistoryQuery;
import com.aks.covidtracker.HistoryallQuery;
import com.aks.covidtracker.R;
import com.aks.covidtracker.adapters.MyMarkerView;
import com.aks.covidtracker.adapters.OverviewAdapter;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class GraphActivity extends AppCompatActivity implements OnChartGestureListener,
        OnChartValueSelectedListener {

    private static final String TAG = "GraphActivity";

    ApolloClient apolloClient = null;
    ProgressBar loadingSpinner = null;
    TextView errorTextView = null;
    private File cacheFile = null;
    private DiskLruHttpCacheStore cacheStore = null;
    private LineChart dailyNewChart, casesChart, recoveredChart, deathChart, allChart = null;

    List<Entry> cases, active, recovered, deaths, todayCases, todayRecovered, todayDeaths;
    public static ArrayList<String> xAxisValues = null;
    String stateNameClicked = null;
    ExpandableCardView expandableCardView;
    TextView recoveryRate;
    TextView mortalityRate;

    TextView newCases2W, recovered2W, deceased2W;
    TextView newCases2WRate, recovered2WRate, deceased2WRate;
    TextView doublingRate;

    String recoveryRateTotal, mortalityRateTotal;
    String avgRecovered2w, avgCases2w, avgDeath2w;
    String casesGrowthRate, recoveryGrowthRate, deathGrowyhRate;
    String casesDoublingRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        String stateName;
        stateNameClicked = getIntent().getStringExtra("state");
        stateName = stateNameClicked;
        if(stateNameClicked.equalsIgnoreCase("India"))
            stateNameClicked = "total";
        Log.i(TAG, "onCreate: got Extra"+stateNameClicked);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initLists();
        initCharts(this);
        setYAxisMin();
        setDescriptions();

        setTitle(stateName);

//        cases.add(new Entry(0, 1));
//        cases.add(new Entry(1, 2));
//        cases.add(new Entry(2, 6));
//        cases.add(new Entry(3, 5));
//        cases.add(new Entry(4, 8));
//        cases.add(new Entry(5, 8));
//        cases.add(new Entry(6, 8));
//        cases.add(new Entry(7, 8));
//        ArrayList<String> xAxisVals = new ArrayList<>(Arrays.asList("Jan","Feb","Mar", "Apr","May","Jun","Jul","Aug"));


//        LineDataSet set1 = new LineDataSet(cases, "Confirmed Cases");
//        set1.setFillAlpha(110);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(set1);
//
//        LineData data = new LineData(dataSets);
//        casesChart.setData(data);
//
//        casesChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisVals));



        File file = new File(getApplicationContext().getFilesDir(), "covid-historical");
        long size = 1024*1024;
        final DiskLruHttpCacheStore cacheStore = new DiskLruHttpCacheStore(file, size);

        OkHttpClient okHttpClient = QueryUtils.initOkHttpClient();
        apolloClient = QueryUtils.buildApolloClient(okHttpClient, cacheStore);

                apolloClient.query(HistoryallQuery.builder()
                .build())
                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(24, TimeUnit.HOURS))
                .enqueue(new ApolloCall.Callback<HistoryallQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<HistoryallQuery.Data> response) {
                        List<HistoryallQuery.Historical> historicalList = null;
                        List<HistoryallQuery.State> tempStateList = null;
                        try{
                            //historicalList = response.getData().country().states().get(0).historical();
                            tempStateList = response.getData().country().states();
                            historicalList = getStateHistory(stateNameClicked, tempStateList);
                            int i =0;
                            HistoryallQuery.Historical before2w = null;
                            try {
                                before2w = historicalList.get(historicalList.size() - 8);
                            }catch (Exception e){
                                try {
                                    before2w = historicalList.get(historicalList.size()  - 7);
                                }catch (Exception ex){
                                    Log.e(TAG, "onResponse: ", ex);
                                }
                            }
                            HistoryallQuery.Historical latest = historicalList.get(historicalList.size()-1);
                            Integer casesLatest = historicalList.get(historicalList.size()-1).cases();
                            Integer recoveredLatest = latest.recovered();
                            Integer deceasedLatest = latest.deaths();

                            setOverallRates(casesLatest, recoveredLatest, deceasedLatest);

                            Integer avg_recovered_2w = (recoveredLatest - before2w.recovered())/7;
                            Integer avg_cases_2w = (casesLatest - (before2w.cases()))/7;
                            Integer avg_deaths_2w = (deceasedLatest - before2w.deaths())/7;

                            setAvg2w(avg_cases_2w, avg_recovered_2w, avg_deaths_2w);
                            setGrowthRates(casesLatest, recoveredLatest, deceasedLatest, before2w);


                            for(HistoryallQuery.Historical h : historicalList){
                                cases.add(new Entry(i, h.cases()));
                                recovered.add(new Entry(i, h.recovered()));
                                deaths.add(new Entry(i, h.deaths()));
                                xAxisValues.add(h.date());
                                todayCases.add(new Entry(i, h.todayCases()));
                                active.add(new Entry(i, h.cases() - h.recovered() - h.deaths()));
                                //Log.i(TAG, "onResponse: "+i);
                                i++;
                            }
                        }catch (Exception e){
                            Log.e(TAG, "onResponse: Some error occurred", e);
                        }

                        GraphActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                casesChart.setData(createLineData(cases, "Confirmed Cases"));
                                recoveredChart.setData(createLineData(recovered, "Recovered Cases"));
                                deathChart.setData(createLineData(deaths, "Deceased Cases"));
                                dailyNewChart.setData(createLineData(todayCases, "Daily New Cases"));

                                displayAdditionalInfo();

                                LineDataSet set1 = new LineDataSet(cases, "Confirmed");
                                set1.setFillAlpha(100);
                                set1.setCircleColor(getColor(R.color.confirmed));
                                set1.setColor(getColor(R.color.confirmed));
                                LineDataSet set2 = new LineDataSet(recovered, "Recovered");
                                set2.setFillAlpha(110);
                                set2.setCircleColor(getColor(R.color.recovered));
                                set2.setColor(getColor(R.color.recovered));
                                LineDataSet set3 = new LineDataSet(deaths, "Deceased");
                                set3.setFillAlpha(120);
                                set3.setCircleColor(getColor(R.color.deceased));
                                set3.setColor(getColor(R.color.deceased));

                                LineDataSet set4 = new LineDataSet(active, "Active");
                                set4.setFillAlpha(120);
                                set4.setCircleColor(getColor(R.color.active));
                                set4.setColor(getColor(R.color.active));


                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(set1);
                                dataSets.add(set2);
                                dataSets.add(set3);
                                dataSets.add(set4);
                                allChart.setData(new LineData(dataSets));
                                setxAxisValuesOfCharts();
                                refreshCharts();
                            }
                        });

                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        if(!QueryUtils.checkInternetConnectivity(getApplicationContext())){
                            Toast t = Toast.makeText(getApplicationContext(), "No Network Connection !", Toast.LENGTH_LONG);
                            t.show();
                        }
                        GraphActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingSpinner.setVisibility(View.GONE);
                                errorTextView.setVisibility(View.VISIBLE);
                            }
                        });
                        Log.e(TAG, "Fail to load data",e);
                    }
                });

//        File file = new File(getApplicationContext().getFilesDir(), "covid-historical");
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
//                .serverUrl(Constants.BASE_URL)
//                .httpCache(new ApolloHttpCache(cacheStore))
//                .okHttpClient(okHttpClient)
//                .build();
//
//        apolloClient.query(HistoryQuery.builder()
//                .build())
//                .httpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(4, TimeUnit.HOURS))
//                .enqueue(new ApolloCall.Callback<HistoryQuery.Data>() {
//                    @Override
//                    public void onResponse(@NotNull Response<HistoryQuery.Data> response) {
//                        //Log.w(TAG+ response.getData().country().states().get(0),""+response.getData().country().states().get(0));
//
//
//
////                        statesList = response.getData().country().states();
////                        for(FeedQuery.State s: statesList){
////                            try {
////                                String stateName = s.state();
////                                if (stateName.equals("Total") || stateName.equals("total") || stateName.equals("TOTAL"))
////                                    stateName = "India";
////                                Integer cases = s.cases();
////                                Integer recovered = s.recovered();
////                                Integer deceased = s.deaths();
////                                Integer todayCases = s.todayCases();
////                                Integer todayRecovered = s.todayRecovered();
////                                Integer todayDeceased = s.todayDeaths();
////
////                                Integer active = cases - recovered - deceased;
////                                Integer activeToday = todayCases - todayRecovered - todayDeceased;
////                                dataList.add(new OverviewItem(stateName, QueryUtils.formatNumbers(cases.toString()),
////                                        QueryUtils.formatNumbers(active.toString()),
////                                        QueryUtils.formatNumbers(recovered.toString()),
////                                        QueryUtils.formatNumbers(deceased.toString()),
////                                        QueryUtils.formatNumbers(todayCases.toString()),
////                                        QueryUtils.formatNumbers(activeToday.toString()),
////                                        QueryUtils.formatNumbers(todayRecovered.toString()),
////                                        QueryUtils.formatNumbers(todayDeceased.toString())));
////                            }catch (Exception e){
////                                Log.e(TAG, "Error in processing a state item", e);
////                            }
////                        }
////
////                        MainActivity.this.runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                loadingSpinner.setVisibility(View.GONE);
////                                errorTextView.setVisibility(View.GONE);
////                                recyclerviewAdapter = new OverviewAdapter(dataList, getApplicationContext(), MainActivity.this);
////                                recyclerView.setAdapter(recyclerviewAdapter);
////                            }
////                        });
//                    }
//
//                    @Override
//                    public void onFailure(@NotNull ApolloException e) {
//                        if(!QueryUtils.checkInternetConnectivity(getApplicationContext())){
//                            Toast t = Toast.makeText(getApplicationContext(), "No Network Connection !", Toast.LENGTH_LONG);
//                            t.show();
//                        }
//                        GraphActivity.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                loadingSpinner.setVisibility(View.GONE);
//                                errorTextView.setVisibility(View.VISIBLE);
//                            }
//                        });
//                        Log.e(TAG, "Fail to load data",e);
//                    }
//                });

    }


    public void initLists(){
        cases = new ArrayList<>();
        active = new ArrayList<>();
        recovered = new ArrayList<>();
        deaths = new ArrayList<>();
        todayCases = new ArrayList<>();
        todayRecovered = new ArrayList<>();
        todayDeaths = new ArrayList<>();
        xAxisValues = new ArrayList<>();
    }

    public void initCharts(Context context){
        casesChart = findViewById(R.id.graph_conf);
        recoveredChart = findViewById(R.id.graph_recovered);
        deathChart = findViewById(R.id.graph_deceased);
        allChart = findViewById(R.id.graph_all);
        dailyNewChart = findViewById(R.id.graph_daily_new);

        casesChart.setOnChartGestureListener(this);
        casesChart.setOnChartValueSelectedListener(this);
        casesChart.setDragEnabled(true);
        casesChart.setScaleEnabled(false);
        casesChart.setDrawGridBackground(false);
        casesChart.setTouchEnabled(true);
        casesChart.getAxisRight().setEnabled(false);

        recoveredChart.setOnChartGestureListener(this);
        recoveredChart.setOnChartValueSelectedListener(this);
        recoveredChart.setDragEnabled(true);
        recoveredChart.setScaleEnabled(false);
        recoveredChart.setDrawGridBackground(false);
        recoveredChart.setTouchEnabled(true);
        recoveredChart.getAxisRight().setEnabled(false);

        deathChart.setOnChartGestureListener(this);
        deathChart.setOnChartValueSelectedListener(this);
        deathChart.setDragEnabled(true);
        deathChart.setScaleEnabled(false);
        deathChart.setDrawGridBackground(false);
        deathChart.getAxisRight().setEnabled(false);
        deathChart.setTouchEnabled(true);

        allChart.setOnChartGestureListener(this);
        allChart.setOnChartValueSelectedListener(this);
        allChart.setDragEnabled(true);
        allChart.setScaleEnabled(false);
        allChart.setDrawGridBackground(false);
        allChart.getAxisRight().setEnabled(false);
        allChart.setTouchEnabled(true);

        dailyNewChart.setOnChartGestureListener(this);
        dailyNewChart.setOnChartValueSelectedListener(this);
        dailyNewChart.setDragEnabled(true);
        dailyNewChart.setScaleEnabled(false);
        dailyNewChart.setDrawGridBackground(false);
        dailyNewChart.getAxisRight().setEnabled(false);
        dailyNewChart.setTouchEnabled(true);

        Description d = new Description();
        d.setText("");
        casesChart.setDescription(d);
        recoveredChart.setDescription(d);
        deathChart.setDescription(d);
        allChart.setDescription(d);

        MyMarkerView markerView = new MyMarkerView(context, R.layout.my_marker_view);
        casesChart.setMarker(markerView);
        recoveredChart.setMarker(markerView);
        deathChart.setMarker(markerView);
        allChart.setMarker(markerView);
        dailyNewChart.setMarker(markerView);

        expandableCardView = findViewById(R.id.additional_info_card);

        recoveryRate = findViewById(R.id.recovery_rate);
        mortalityRate = findViewById(R.id.death_rate);

        newCases2W = findViewById(R.id.avg_new_cases_2w);
        recovered2W = findViewById(R.id.avg_recovery_2w);
        deceased2W = findViewById(R.id.avg_death_2w);

        newCases2WRate = findViewById(R.id.avg_growth_rate);
        recovered2WRate = findViewById(R.id.avg_recovery_rate);
        deceased2WRate = findViewById(R.id.avg_mortality_rate);
        doublingRate = findViewById(R.id.doubling_rate);
    }

    public void setDescriptions(){
        casesChart.setDescription(createDescription("cases"));
        recoveredChart.setDescription(createDescription("recovered"));
        deathChart.setDescription(createDescription("death"));
        dailyNewChart.setDescription(createDescription("daily_new"));
    }

    public LineData createLineData(List<Entry> list, String label){
        LineDataSet set = new LineDataSet(list, label);
        set.setFillAlpha(100);
        switch(label){
            case "Daily New Cases":
            case "Confirmed Cases": set.setCircleColor(getColor(R.color.confirmed));
                set.setColor(getColor(R.color.confirmed));
                break;
            case "Recovered Cases": set.setCircleColor(getColor(R.color.recovered));
                set.setColor(getColor(R.color.recovered));
                break;
            case "Deceased Cases": set.setCircleColor(getColor(R.color.deceased));
                set.setColor(getColor(R.color.deceased));
                break;
            default:set.setCircleColor(getColor(R.color.active));
        }
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        return new LineData(dataSets);
    }

    public void setxAxisValuesOfCharts(){
        casesChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        recoveredChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        deathChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        allChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
        dailyNewChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
    }

    public void setYAxisMin(){
        casesChart.getAxisRight().setAxisMinimum(0.0f);
        recoveredChart.getAxisRight().setAxisMinimum(0.0f);
        deathChart.getAxisRight().setAxisMinimum(0.0f);
        allChart.getAxisRight().setAxisMinimum(0.0f);
        dailyNewChart.getAxisRight().setAxisMinimum(0.0f);

        casesChart.getAxisLeft().setAxisMinimum(0.0f);
        recoveredChart.getAxisLeft().setAxisMinimum(0.0f);
        deathChart.getAxisLeft().setAxisMinimum(0.0f);
        allChart.getAxisLeft().setAxisMinimum(0.0f);
        dailyNewChart.getAxisLeft().setAxisMinimum(0.0f);
    }

    public Description createDescription(String chart){
        Description d = new Description();
        switch(chart){
            case "cases": d.setText("Total Confirmed cases");
            break;
            case "recovered": d.setText("Total Recovered Cases");
            break;
            case "death": d.setText("Total Deceased");
            break;
            case "daily_new": d.setText("Daily new cases");
            break;
        }
        return d;
    }

    public void refreshCharts(){
        casesChart.invalidate();
        deathChart.invalidate();
        recoveredChart.invalidate();
        allChart.invalidate();
        dailyNewChart.invalidate();
    }

    public List<HistoryallQuery.Historical> getStateHistory(String stateName, List<HistoryallQuery.State> list){
        try {
            for (HistoryallQuery.State s : list) {
                if (s.state().equalsIgnoreCase(stateName)){

                    return  s.historical();
                }
            }
        }catch (Exception e){
            Log.e(TAG, "getStateHistory: Exception occured, cannot process state historical data",e );
        }
        return null;
    }

    public void setOverallRates(Integer cases, Integer recovered, Integer deceased){
        recoveryRateTotal = QueryUtils.calcRecoveryRate(cases, recovered);
        mortalityRateTotal = QueryUtils.calcMortalityRate(cases, deceased);
    }

    public void setAvg2w(Integer cases, Integer recovered, Integer deaths){
        avgCases2w = QueryUtils.formatNumbers(cases.toString());
        avgRecovered2w = QueryUtils.formatNumbers(recovered.toString());
        avgDeath2w = QueryUtils.formatNumbers(deaths.toString());
    }

    public void setGrowthRates(Integer cases, Integer recovered, Integer deaths, HistoryallQuery.Historical before2w){
        casesGrowthRate = QueryUtils.calcRate(before2w.cases(), cases, 7);
        recoveryGrowthRate = QueryUtils.calcRate(before2w.recovered(),recovered, 7);
        deathGrowyhRate = QueryUtils.calcRate(before2w.deaths(), deaths, 7);
        casesDoublingRate = QueryUtils.calcDoublingRate(before2w.cases(), cases, 7 );
    }

    public void displayAdditionalInfo(){
        if(recoveryRateTotal.equalsIgnoreCase("NA"))
            recoveryRate.setText(recoveryRateTotal);
        else recoveryRate.setText(recoveryRateTotal+"%");

        if(mortalityRateTotal.equalsIgnoreCase("NA"))
            mortalityRate.setText(mortalityRateTotal);
        else mortalityRate.setText(mortalityRateTotal+"%");

        newCases2W.setText(avgCases2w);
        recovered2W.setText(avgRecovered2w);
        deceased2W.setText(avgDeath2w);

        if(casesGrowthRate.equalsIgnoreCase("NA"))
            newCases2WRate.setText(casesGrowthRate);
        else newCases2WRate.setText(casesGrowthRate+"%");

        if(recoveryGrowthRate.equalsIgnoreCase("NA"))
            recovered2WRate.setText(recoveryGrowthRate);
        else recovered2WRate.setText(recoveryGrowthRate+"%");

        if(deathGrowyhRate.equalsIgnoreCase("NA"))
            deceased2WRate.setText(deathGrowyhRate);
        else deceased2WRate.setText(deathGrowyhRate+"%");

        if(casesDoublingRate.equalsIgnoreCase("NA"))
            doublingRate.setText(casesDoublingRate);
        else doublingRate.setText(casesDoublingRate+" days");
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }


    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
