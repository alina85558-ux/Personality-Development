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
import com.mayur.personalitydevelopment.models.MusicItem;

import java.util.ArrayList;

public class MusicCategoryAdapter extends RecyclerView.Adapter<MusicCategoryAdapter.ViewHolder> {

    private ArrayList<MusicItem> items;

    private Context context;
    private LayoutInflater layoutInflater;
    private String course;
    private AdapterListener listener;

    public MusicCategoryAdapter(Context context, ArrayList<MusicItem> items, String course, AdapterListener listener) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.items = items;
        this.course = course;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusicCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_music_listing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MusicItem course = items.get(position);
        holder.tvTitle.setText(course.getFileName());
        holder.tvTime.setText(course.getTimeDuration());

        if (!course.getImage_url().isEmpty()) {
            RequestOptions options = new RequestOptions();
            final RequestOptions placeholder_error = options.error(R.drawable.temo)
                    .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(context).load(course.getImage_url()).apply(placeholder_error)
                    .into(holder.ivCourseImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(course);
            }
        });
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

    public interface AdapterListener {
        void onClick(MusicItem musicItem);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvTime;
        private ImageView ivCourseImage;

        public ViewHolder(View view) {
            super(view);
            ivCourseImage = view.findViewById(R.id.imageView);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvTime = view.findViewById(R.id.tvTime);
        }
    }
}