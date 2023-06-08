package com.rcm.calculator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.rcm.calculator.R;
import com.rcm.calculator.models.FieldData;
import com.rcm.calculator.utils.Tools;

import java.util.List;


public class DownLineAdapters extends RecyclerView.Adapter<DownLineAdapters.ViewHolder>{
    private Context context;
    private List<FieldData> fieldDataList;
    private double totalPercent;


    public DownLineAdapters(Context context, List<FieldData> fieldDataList, double totalPercent) {
        this.context = context;
        this.fieldDataList = fieldDataList;
        this.totalPercent = totalPercent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.recycler_item2, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int p) {
        int position = p;
        holder.legpercent.setText(String.valueOf(Tools.getPercent(Double.parseDouble(fieldDataList.get(position).getUserPV()))) +"%");
        holder.legamount.setText(String.valueOf(fieldDataList.get(position).getUserPV()));

        Double nn = Double.parseDouble(fieldDataList.get(position).getUserPV());


        if (Tools.getPercent(Double.parseDouble(fieldDataList.get(position).getUserPV()))==0.0)
        {
            Double totalpercent = totalPercent / 100;
            holder.pb.setText(String.valueOf(round(nn*totalpercent,2)));
        }
        else {
            Double percent = Tools.getPercent(Double.parseDouble(fieldDataList.get(position).getUserPV()));
            Double totalpercent = percent / 100;
            holder.pb.setText(String.valueOf(round(nn*totalpercent,2)));
        }
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    public int getItemCount() {
        return fieldDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView legpercent, legamount, pb;
        public ViewHolder(View itemView) {
            super(itemView);
            this.legpercent = itemView.findViewById(R.id.legpercent);
            this.legamount = itemView.findViewById(R.id.legamount);
            this.pb = itemView.findViewById(R.id.pb);
        }
    }
}