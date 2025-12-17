package com.mayur.personalitydevelopment.adapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private AdapterListener listener;

    public interface AdapterListener {
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(RecyclerView.ViewHolder holder, int position);

        int getItemCount();

        int getItemViewType(int position);

        long getItemId(int position);

    }

    public CustomAdapter(AdapterListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return listener.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        listener.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return listener.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return listener.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return listener.getItemId(position);
    }

}
