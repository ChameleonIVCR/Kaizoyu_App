package com.chame.kaizoyu.gui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizoyu.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    final private List<Result> results = new ArrayList<>();
    final private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private ItemClickListener mClickListener;

    public RecyclerAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        this.recyclerView = recyclerView;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder vHolder, int position) {
        Result result = results.get(position);
        vHolder.textTitle.setText(result.getFilename());
        vHolder.textBot.setText(result.getBot());
        vHolder.textQuality.setText(result.getQuality());
        vHolder.textFormat.setText(result.getFormat());
        vHolder.textSize.setText(result.getSize());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return results.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textTitle;
        TextView textBot;
        TextView textQuality;
        TextView textFormat;
        TextView textSize;

        ViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textBot = itemView.findViewById(R.id.text_bot);
            textQuality = itemView.findViewById(R.id.text_quality);
            textFormat = itemView.findViewById(R.id.text_format);
            textSize = itemView.findViewById(R.id.text_size);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener == null) return;
            mClickListener.onItemClick(results.get(getAdapterPosition()));
        }
    }

    public void replaceData(List<Result> newDataSet){
        results.clear();
        results.addAll(newDataSet);
    }

    public void clearData(){
        results.clear();
    }

    // convenience method for getting data at click position
    public Result getItem(int id) {
        return results.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public RecyclerView getRecyclerView(){
        return this.recyclerView;
    }

    public interface ItemClickListener {
        void onItemClick(Result result);
    }
}