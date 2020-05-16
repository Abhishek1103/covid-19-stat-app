package com.aks.covidtracker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aks.covidtracker.R;
import com.aks.covidtracker.models.OverviewItem;
import com.alespero.expandablecardview.ExpandableCardView;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private static final String TAG = "OverviewAdapter";

    private List<OverviewItem> dataList;
    private Context context;
    private OnStateClickListener onStateClickListener;
    private final String DOWN_ARROW = "\u2193";
    private final String UP_ARROW = "\u2191";

    public OverviewAdapter(List<OverviewItem> dataList, Context context, OnStateClickListener onStateClickListener) {
        this.dataList = dataList;
        this.context = context;
        this.onStateClickListener = onStateClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent,false);

        return new ViewHolder(v, onStateClickListener );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OverviewItem item = dataList.get(position);
        holder.stateTextView.setText(item.getState());
        holder.confTextView.setText(item.getConfirmed());
        holder.activeTextView.setText(item.getActive());
        holder.recoveredTextView.setText(item.getRecovered());
        holder.deceasedTextView.setText(item.getDeceased());
        String active = item.getActive_today();
        if(active.charAt(0) == '-') {
            holder.activeTodayTextView.setText(item.getActive_today() + " "+ DOWN_ARROW);

        }
        else holder.activeTodayTextView.setText("+" + active + " "+ UP_ARROW );
        holder.confTodayTextView.setText("+" +  item.getConfirmed_today() + " "+ UP_ARROW );
        holder.recoveredTodayTextView.setText("+" + item.getRecovered_today() + " "+ UP_ARROW );
        holder.deceasedTodayTextView.setText("+" + item.getDeceased_today() + " "+ UP_ARROW );

//        if(item.getRecovery_rate().equalsIgnoreCase("NA"))
//            holder.recoveryRate.setText(item.getRecovery_rate());
//        else holder.recoveryRate.setText(item.getRecovery_rate()+"%");
//
//        if(item.getMortality_rate().equalsIgnoreCase("NA"))
//            holder.mortalityRate.setText(item.getMortality_rate());
//        else holder.mortalityRate.setText(item.getMortality_rate()+"%");
//
//        holder.newCases2W.setText(item.getNew_cases_2w());
//        holder.recovered2W.setText(item.getRecovered_2w());
//        holder.deceased2W.setText(item.getDeath_2w());
//
//        if(item.getNew_cases_rate_2w().equalsIgnoreCase("NA"))
//            holder.newCases2WRate.setText(item.getNew_cases_rate_2w());
//        else holder.newCases2WRate.setText(item.getNew_cases_rate_2w()+"%");
//
//        if(item.getRecovery_rate_2w().equalsIgnoreCase("NA"))
//            holder.recovered2WRate.setText(item.getRecovery_rate_2w());
//        else holder.recovered2WRate.setText(item.getRecovery_rate_2w()+"%");
//
//        if(item.getMortality_rate_2w().equalsIgnoreCase("NA"))
//            holder.deceased2WRate.setText(item.getMortality_rate_2w());
//        else holder.deceased2WRate.setText(item.getMortality_rate_2w()+"%");
//
//        holder.expandableCardView.setOnExpandedListener(new ExpandableCardView.OnExpandedListener() {
//            @Override
//            public void onExpandChanged(View v, boolean isExpanded) {
//                Log.i(TAG, "onExpandChanged: "+isExpanded);
//            }
//        });


//        if(item.getState().trim().equalsIgnoreCase("INDIA")){
//            Log.i(TAG, "onBindViewHolder: Showing Graph Icon");
//        }else holder.graphIconImageView.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        TextView stateTextView;
        TextView confTextView;
        TextView activeTextView;
        TextView recoveredTextView;
        TextView deceasedTextView;
        TextView confTodayTextView;
        TextView activeTodayTextView;
        TextView recoveredTodayTextView;
        TextView deceasedTodayTextView;
        ImageView graphIconImageView;
//        ExpandableCardView expandableCardView;
//        TextView recoveryRate;
//        TextView mortalityRate;
//
//        TextView newCases2W, recovered2W, deceased2W;
//        TextView newCases2WRate, recovered2WRate, deceased2WRate;

        OnStateClickListener onStateClickListener;

        ViewHolder(@NonNull View itemView, OnStateClickListener onStateClickListener) {
            super(itemView);

            stateTextView = itemView.findViewById(R.id.state);
            confTextView = itemView.findViewById(R.id.conf);
            activeTextView = itemView.findViewById(R.id.active);
            recoveredTextView = itemView.findViewById(R.id.recovered);
            deceasedTextView = itemView.findViewById(R.id.deceased);

            confTodayTextView = itemView.findViewById(R.id.conf_today);
            activeTodayTextView = itemView.findViewById(R.id.active_today);
            recoveredTodayTextView = itemView.findViewById(R.id.recovered_today);
            deceasedTodayTextView = itemView.findViewById(R.id.deceased_today);
            graphIconImageView = itemView.findViewById(R.id.graph_icon_view);

//            expandableCardView = itemView.findViewById(R.id.additional_info_card);
//
//            recoveryRate = itemView.findViewById(R.id.recovery_rate);
//            mortalityRate = itemView.findViewById(R.id.death_rate);
//
//            newCases2W = itemView.findViewById(R.id.avg_new_cases_2w);
//            recovered2W = itemView.findViewById(R.id.avg_recovery_2w);
//            deceased2W = itemView.findViewById(R.id.avg_death_2w);
//
//            newCases2WRate = itemView.findViewById(R.id.avg_growth_rate);
//            recovered2WRate = itemView.findViewById(R.id.avg_recovery_rate);
//            deceased2WRate = itemView.findViewById(R.id.avg_mortality_rate);

            this.onStateClickListener = onStateClickListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onStateClickListener.onStateClick(getAdapterPosition());
        }
    }

    public interface OnStateClickListener{
        void onStateClick(int position);
    }
}
