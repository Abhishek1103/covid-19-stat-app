package com.aks.covidtracker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aks.covidtracker.R;
import com.aks.covidtracker.models.OverviewItem;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private static final String TAG = "OverviewAdapter";

    private List<OverviewItem> dataList;
    private Context context;
    private OnStateClickListener onStateClickListener;

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

        holder.confTodayTextView.setText("+" + item.getConfirmed_today());
        holder.activeTodayTextView.setText("+" + item.getActive_today());
        holder.recoveredTodayTextView.setText("+" + item.getRecovered_today());
        holder.deceasedTodayTextView.setText("+" + item.getDeceased_today());
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
