package com.aks.covidtracker.adapters;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;

import com.aks.covidtracker.R;
import com.aks.covidtracker.activities.GraphActivity;
import com.aks.covidtracker.utility.QueryUtils;
import com.github.mikephil.charting.charts.Chart;
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

    MPPointF mOffset;
    @Override
    public MPPointF getOffset() {
        if(mOffset == null)
            mOffset = new MPPointF((getWidth()), -2*getHeight());
        //Log.i(TAG, "getOffset: width: "+getWidth() + ", height: "+getHeight());
        return mOffset;
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
        MPPointF mOffSet = new MPPointF();
        MPPointF offset = getOffset();
        mOffset.x = offset.x;
        mOffset.y = offset.y;

        Chart chart = getChartView();

        float width = getWidth();
        float height = getHeight();

        if (posX + mOffset.x < 0) {
            mOffset.x = - posX;
        } else if (chart != null && posX + width + mOffset.x > chart.getWidth()) {
            mOffset.x = chart.getWidth() - posX - width;
        }

        if (posY + mOffset.y < 0) {
            mOffset.y = - posY;
        } else if (chart != null && posY + height + mOffset.y > chart.getHeight()) {
            mOffset.y = chart.getHeight() - posY - height;
        }
        mOffSet.x = 100.0f;
        //Log.i(TAG, "getOffsetForDrawingAtPoint: "+mOffSet.getX() + ", "+mOffSet.getY());
        return mOffset;
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        posX -= 100;
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);
        //Log.i(TAG, "draw: "+posX +", "+posY);
        int saveId = canvas.save();
        // translate to the correct position and draw
        canvas.translate(posX + offset.x, posY + offset.y);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        StringBuilder stringBuilder = new StringBuilder(""+e.getY());
        String s = stringBuilder.substring(0,stringBuilder.indexOf("."));
        s = QueryUtils.formatNumbers(s);

        String date = xAxisValues.get((int)e.getX());
        String month = QueryUtils.getMonth(Integer.parseInt(date.substring(0, date.indexOf("/"))));
        String day = date.substring(date.indexOf("/")+1, date.lastIndexOf("/"));

        s = s+"\n"+month+","+day;
        //Log.i(TAG, "refreshContent: "+s);
        tvContent.setText(s);
    }
}
