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
import com.mayur.personalitydevelopment.models.CourseCategory;

import java.util.ArrayList;

public class CourseCategoriesAdapter extends RecyclerView.Adapter<CourseCategoriesAdapter.ViewHolder> {

    private ArrayList<CourseCategory> items;

    private Context context;
    private LayoutInflater layoutInflater;
    private AdapterListener listener;

    public CourseCategoriesAdapter(Context context, ArrayList<CourseCategory> items, AdapterListener listener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_course_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CourseCategory course = items.get(position);
        holder.tvTitle.setText(course.getCategoryName());

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.temo).placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context).load(course.getImageUrl()).apply(placeholder_error).into(holder.ivCourseImage);

        if (course.isCourseCompleted()) {
            holder.ivCourseCompletedImage.setImageResource(R.drawable.ic_tick_selected);
        } else {
            holder.ivCourseCompletedImage.setImageResource(R.drawable.ic_tick_unselected);
        }

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

    public interface AdapterListener {
        void onClickEvent(CourseCategory courseCategory);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCourseImage;
        private ImageView ivCourseCompletedImage;
        private TextView tvTitle;

        public ViewHolder(View view) {
            super(view);
            ivCourseImage = view.findViewById(R.id.ivCategoryImage);
            ivCourseCompletedImage = view.findViewById(R.id.isCourseCompleted);
            tvTitle = view.findViewById(R.id.tvTitle);

        }
    }
}