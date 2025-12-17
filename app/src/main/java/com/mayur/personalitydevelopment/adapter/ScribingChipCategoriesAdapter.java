package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.models.Card;

import java.util.ArrayList;

public class ScribingChipCategoriesAdapter extends RecyclerView.Adapter<ScribingChipCategoriesAdapter.ViewHolder> {

    private ArrayList<Card> items;

    private Context context;
    private LayoutInflater layoutInflater;
    private AdapterListener listener;

    public ScribingChipCategoriesAdapter(Context context, ArrayList<Card> items, AdapterListener listener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.scribing_chip;
    }

    @NonNull
    @Override
    public ScribingChipCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Card course = items.get(position);
        holder.title.setText(course.getName());
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

    public ArrayList<Card> getItems() {
        return items;
    }

    public interface AdapterListener {
        void onClickEvent(Object courseCategory);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView title;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }
}
