package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.models.Course;

import java.util.ArrayList;

public class ItemCourseListAdapter extends RecyclerView.Adapter<ItemCourseListAdapter.ViewHolder> {

    private ArrayList<Course> items;

    private Context context;
    private LayoutInflater layoutInflater;
    private AdapterListerner listerner;

    public ItemCourseListAdapter(Context context, ArrayList<Course> items, AdapterListerner listerner) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.listerner = listerner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_course_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Course course = items.get(position);
        holder.tvTitle.setText(course.getCourseName());

        if (course.getAccessmessage().equals("")) {
            holder.accessNoteTextView.setVisibility(View.GONE);
        } else {
            holder.accessNoteTextView.setVisibility(View.VISIBLE);
            if (Utils.isSubscribed(context)) {
                holder.accessNoteTextView.setText(course.getRemainTaskMsg());
            } else {
                holder.accessNoteTextView.setText(course.getAccessmessage());
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listerner.onClickEvent(course);
            }
        });

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.temo)
                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context).load(course.getImageUrl()).apply(placeholder_error)
                .into(holder.ivCourseImage);
    }

    @Override
    public long getItemId(int position) {
        if (items == null)
            return 0;
        return position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface AdapterListerner {
        void onClickEvent(Course item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCourseImage;
        private TextView tvTitle;
        private TextView accessNoteTextView;

        public ViewHolder(View view) {
            super(view);
            ivCourseImage = view.findViewById(R.id.ivCourseImage);
            tvTitle = view.findViewById(R.id.tvTitle);
            accessNoteTextView = view.findViewById(R.id.accessNoteTextView);
        }
    }
}