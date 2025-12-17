package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.models.Card;
import com.mayur.personalitydevelopment.models.Note;

import java.util.ArrayList;

public class ScribingListingCategoriesAdapter extends RecyclerView.Adapter<ScribingListingCategoriesAdapter.ViewHolder> {

    private ArrayList<Object> items;

    private Context context;
    private LayoutInflater layoutInflater;
    private AdapterListener listener;

    public ScribingListingCategoriesAdapter(Context context, ArrayList<Object> items, AdapterListener listener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Card) {
            return R.layout.item_scribing_header_listing;
        }
        return R.layout.item_affirmation_listing;
    }

    @NonNull
    @Override
    public ScribingListingCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Object course = items.get(position);
        if (course instanceof Note) {
            Note note = (Note) course;
            holder.checkBox.setText(note.getTitle());
            holder.checkBox.setChecked(note.getIsChecked());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickEvent(course);
                }
            });
            if (note.getIsChecked()) {
                holder.checkBox.setPaintFlags(holder.checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.checkBox.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
            }
        } else if (course instanceof Card) {
            holder.title.setText(((Card) course).getName());
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickEvent(course);
                }
            });
        }
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

    public ArrayList<Object> getItems() {
        return items;
    }

    public interface AdapterListener {
        void onClickEvent(Object courseCategory);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView title;
        private ImageView ivDelete;

        public ViewHolder(View view) {
            super(view);
            ivDelete = view.findViewById(R.id.ivDelete);
            title = view.findViewById(R.id.title);
            checkBox = view.findViewById(R.id.checkBox);
        }
    }
}
