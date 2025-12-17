package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.models.AffirmationListing;

import java.util.ArrayList;

public class AffirmationListingCategoriesAdapter extends RecyclerView.Adapter<AffirmationListingCategoriesAdapter.ViewHolder> {

    private ArrayList<AffirmationListing> items;

    private Context context;
    private LayoutInflater layoutInflater;
    private AdapterListener listener;

    public AffirmationListingCategoriesAdapter(Context context, ArrayList<AffirmationListing> items, AdapterListener listener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AffirmationListingCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_affirmation_listing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AffirmationListing course = items.get(position);
        holder.checkBox.setText(course.getText());
        holder.checkBox.setChecked(course.isSelected());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickEvent(course);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    public ArrayList<AffirmationListing> getItems() {
        return items;
    }

    public interface AdapterListener {
        void onClickEvent(AffirmationListing courseCategory);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public ViewHolder(View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }
}
