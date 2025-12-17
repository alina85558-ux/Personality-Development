package com.mayur.personalitydevelopment.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.models.LikeUserListResponse;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final Activity mContext;
    private final LayoutInflater inflater;
    private final ArrayList<LikeUserListResponse.UserDetail> mList;
    private final SharedPreferences sp;

    public UserAdapter(Activity activity, ArrayList<LikeUserListResponse.UserDetail> userList) {
        this.mContext = activity;
        this.mList = userList;
        this.inflater = LayoutInflater.from(activity);
        sp = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LikeUserListResponse.UserDetail model = mList.get(position);
        holder.txtUsername.setText(String.format("%s %s", model.getFirstName(), model.getLastName()));

        RequestOptions options = new RequestOptions();
        final RequestOptions placeholder_error = options.error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(mContext)
                .load(model.getProfileImgUrl())
                .apply(placeholder_error).into(holder.imgProfile);

        if (sp.getBoolean("light", false)) {
            holder.cardUser.setCardBackgroundColor(Color.parseColor("#464646"));
            holder.txtUsername.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.cardUser.setCardBackgroundColor(Color.parseColor("#ffffff"));
            holder.txtUsername.setTextColor(Color.parseColor("#000000"));
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardUser;
        private TextView txtUsername;
        private ImageView imgProfile;

        public ViewHolder(View view) {
            super(view);

            cardUser = view.findViewById(R.id.cardUser);
            txtUsername = view.findViewById(R.id.txtUsername);
            imgProfile = view.findViewById(R.id.imgProfile);
        }
    }
}
