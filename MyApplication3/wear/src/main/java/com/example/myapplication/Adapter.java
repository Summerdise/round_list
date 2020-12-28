package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.wear.widget.WearableRecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.HeadViewHolder> {

    private List<Data> dataList;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public Adapter(List<Data> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    static class HeadViewHolder extends WearableRecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        public HeadViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }
    }


    @NonNull
    @Override
    public HeadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new HeadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadViewHolder holder,final int position) {
        Data data = dataList.get(position);
        holder.icon.setImageResource(data.getImage());
        holder.title.setText(data.getName());

        if(mOnItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
