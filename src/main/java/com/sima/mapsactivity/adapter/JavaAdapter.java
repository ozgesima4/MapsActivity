package com.sima.mapsactivity.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sima.mapsactivity.databinding.RecyclerRowBinding;
import com.sima.mapsactivity.model.Place;
import com.sima.mapsactivity.view.MainActivity;
import com.sima.mapsactivity.view.MainActivity2;

import java.util.List;

public class JavaAdapter extends RecyclerView.Adapter<JavaAdapter.AdapterHolder> {
    List<Place> placelist;

    public JavaAdapter(List<Place> placelist) {
        this.placelist = placelist;
    }

    @NonNull
    @Override
    public AdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new AdapterHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterHolder holder, int position) {
        holder.recyclerRowBinding.TextViewRecyclerView.setText(placelist.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), MainActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("place",placelist.get(holder.getAdapterPosition()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return placelist.size();
    }

    public class AdapterHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;

        public AdapterHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding=recyclerRowBinding;
        }
    }
}
