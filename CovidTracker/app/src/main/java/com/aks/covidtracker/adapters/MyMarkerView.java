package com.aks.covidtracker.adapters;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.aks.covidtracker.R;
import com.aks.covidtracker.activities.GraphActivity;
import com.aks.covidtracker.utility.QueryUtils;
import com.github.mikephil.charting.components.IMarker;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import static com.aks.covidtracker.activities.GraphActivity.xAxisValues;

public class MyMarkerView extends MarkerView implements IMarker {
    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    private static final String TAG = "MyMarkerView";
    private TextView tvContent;
    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.marker_textview);
    }

    private MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if(mOffset == null)
        mOffset = new MPPointF(-(2*getWidth()), -getHeight());

        return mOffset;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        StringBuilder stringBuilder = new StringBuilder(""+e.getY());
        String s = stringBuilder.substring(0,stringBuilder.indexOf("."));

        String date = xAxisValues.get((int)e.getX());
        String month = QueryUtils.getMonth(Integer.parseInt(date.substring(0, date.indexOf("/"))));
        String day = date.substring(date.indexOf("/")+1, date.lastIndexOf("/"));

        s = s+"\n"+month+","+day;
        //Log.i(TAG, "refreshContent: "+s);
        tvContent.setText(s);
    }
}
