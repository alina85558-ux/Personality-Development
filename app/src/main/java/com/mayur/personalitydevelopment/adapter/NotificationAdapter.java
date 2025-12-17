package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.listener.NotificationClickListener;
import com.mayur.personalitydevelopment.models.NotificationDataRes;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    final RequestOptions placeholder_error = new RequestOptions().error(R.drawable.temo).placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);
    private final ArrayList<NotificationDataRes> notificationList;
    private final Context mContext;
    private final NotificationClickListener notificationClickListener;
    private boolean isDarkTheme = false;

    public NotificationAdapter(Context mContext, boolean isDarkTheme, ArrayList<NotificationDataRes> notificationList, NotificationClickListener notificationClickListener) {
        this.notificationList = notificationList;
        this.mContext = mContext;
        this.notificationClickListener = notificationClickListener;
        this.isDarkTheme = isDarkTheme;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_notification, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NotificationDataRes itemData = notificationList.get(position);
        changeReadingMode(holder);
        if (itemData.getRedirectTo().equals("post")) {
            holder.postCard.setVisibility(View.VISIBLE);
            holder.articleCard.setVisibility(View.GONE);
            holder.postTitle.setText(itemData.getTitle());
            holder.postDesc.setText(Html.fromHtml(itemData.getDetail()));
            holder.postTime.setReferenceTime(Long.parseLong(itemData.getCreatedAt()));
        } else {
            holder.postCard.setVisibility(View.GONE);
            holder.articleCard.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(itemData.getImageUrl()).apply(placeholder_error).into(holder.articleImgView);
            holder.articleTitle.setText(itemData.getTitle());
            holder.articleTime.setReferenceTime(Long.parseLong(itemData.getCreatedAt()));
        }

        holder.postCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationClickListener.onNotificationClick(position, itemData);
            }
        });

        holder.articleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationClickListener.onNotificationClick(position, itemData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void changeReadingMode(ViewHolder holder) {
        if (isDarkTheme) {
            holder.articleCard.setCardBackgroundColor(Color.parseColor("#464646"));
            holder.postCard.setCardBackgroundColor(Color.parseColor("#464646"));
            holder.postTime.setTextColor(Color.parseColor("#ffffff"));
            holder.articleTime.setTextColor(Color.parseColor("#ffffff"));
            holder.postTitle.setTextColor(Color.parseColor("#ffffff"));
            holder.postDesc.setTextColor(Color.parseColor("#ffffff"));
            holder.articleTitle.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.articleCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.postCard.setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.postTime.setTextColor(Color.parseColor("#838383"));
            holder.articleTime.setTextColor(Color.parseColor("#838383"));
            holder.postTitle.setTextColor(Color.parseColor("#000000"));
            holder.postDesc.setTextColor(Color.parseColor("#000000"));
            holder.articleTitle.setTextColor(Color.parseColor("#000000"));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView articleCard;
        public ImageView articleImgView;
        public TextView articleTitle;
        public RelativeTimeTextView articleTime;
        public CardView postCard;
        public TextView postTitle;
        public TextView postDesc;
        public RelativeTimeTextView postTime;

        public ViewHolder(View itemView) {
            super(itemView);
            this.articleCard = itemView.findViewById(R.id.articleCard);
            this.articleImgView = itemView.findViewById(R.id.articleImageView);
            this.articleTitle = itemView.findViewById(R.id.articleTitleTextView);
            this.articleTime = itemView.findViewById(R.id.articleTime);
            this.postCard = itemView.findViewById(R.id.postCard);
            this.postTitle = itemView.findViewById(R.id.postTitleTextView);
            this.postDesc = itemView.findViewById(R.id.postDescTextView);
            this.postTime = itemView.findViewById(R.id.postTime);
        }
    }

}