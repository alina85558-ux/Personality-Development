package com.mayur.personalitydevelopment.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.databinding.ProfileCellPostListBinding;

public class ProfilePostHolder {

    private ProfileCellPostListBinding itemBinding;

    public ProfilePostHolder() {
    }

    public void setItemBinding(Context context, ViewGroup parent) {
        this.itemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.profile_cell_post_list, parent, false);
    }

    public MyPostHolder castHolder(RecyclerView.ViewHolder holder) {
        return (MyPostHolder) holder;
    }

    public MyPostHolder getHolder() {
        return new MyPostHolder(itemBinding.getRoot());
    }

    public class MyPostHolder extends RecyclerView.ViewHolder {

        public CardView cardViewPost;
        public TextView txtPostName;
        public TextView txtLikes;
        public TextView txtComments;
        public TextView txtPostDescription;
        public RelativeTimeTextView txtPostTime;
        public ImageView ivUserPic;
        public ImageView ivOptions;
        public LinearLayout linearLike;
        public CheckedTextView likeIcon;
        public ImageView commentImageV;

        public MyPostHolder(View view) {
            super(view);
            cardViewPost = itemBinding.cardViewPost;
            txtPostName = itemBinding.postName;
            txtLikes = itemBinding.txtLikes;
            txtComments = itemBinding.txtComments;
            txtPostDescription = itemBinding.postDetails;
            txtPostTime = itemBinding.postDate;
            ivUserPic = itemBinding.imgProfilePic;
            ivOptions = itemBinding.imgOption;
            linearLike = itemBinding.linearLike;
            likeIcon = itemBinding.likeIcon;
            commentImageV = itemBinding.commentImageV;
        }

    }
}
