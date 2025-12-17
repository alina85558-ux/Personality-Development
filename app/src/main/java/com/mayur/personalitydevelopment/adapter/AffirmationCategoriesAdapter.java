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
import com.mayur.personalitydevelopment.models.Affirmation;

import java.util.ArrayList;

public class AffirmationCategoriesAdapter extends RecyclerView.Adapter<AffirmationCategoriesAdapter.ViewHolder> {

    private ArrayList<Affirmation> items;

    private Context context;
    private LayoutInflater layoutInflater;
    private AdapterListener listener;

    public AffirmationCategoriesAdapter(Context context, ArrayList<Affirmation> items, AdapterListener listener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AffirmationCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_course_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Affirmation course = items.get(position);

        holder.ivCourseCompletedImage.setVisibility(View.GONE);

        holder.tvTitle.setText(course.getCategoryName());

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.temo)
                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context).load(course.getImageUrl()).apply(placeholder_error)
                .into(holder.ivCourseImage);

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
        void onClickEvent(Affirmation courseCategory);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCourseImage;
        private TextView tvTitle;
        private ImageView ivCourseCompletedImage;

        public ViewHolder(View view) {
            super(view);
            ivCourseImage = view.findViewById(R.id.ivCategoryImage);
            tvTitle = view.findViewById(R.id.tvTitle);
            ivCourseCompletedImage = view.findViewById(R.id.isCourseCompleted);
        }
    }
}