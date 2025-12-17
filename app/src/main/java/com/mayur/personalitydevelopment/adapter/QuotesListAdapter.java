package com.mayur.personalitydevelopment.adapter;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Utils;
import com.mayur.personalitydevelopment.models.Quotes;
import com.mayur.personalitydevelopment.viewholder.QuotesHolder;

import java.util.ArrayList;
import java.util.List;

public class QuotesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EXTERNAL_STORAGE_PERMISSION_Constants = 100;
    final QuotesHolder holderInstance = new QuotesHolder();
    private List<Quotes.QuotesBean> quotesList = new ArrayList<>();
    private Activity context;

    public QuotesListAdapter(List<Quotes.QuotesBean> quotesList, Activity context) {
        this.quotesList = quotesList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        holderInstance.setItemBinding(context, parent);
        return holderInstance.getHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final QuotesHolder.MyHolder myHolder = holderInstance.castHolder(holder);
        final Quotes.QuotesBean bean = quotesList.get(position);

        myHolder.d_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_Constants);
                } else {
                    Utils.downloadFile(bean.getImage_url(),context);
                }
            }
        });

        RequestOptions options = new RequestOptions();

        final RequestOptions placeholder_error = options.error(R.drawable.temo)
                .placeholder(R.drawable.temo).diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context).load(bean.getImage_url()).apply(placeholder_error)
                .into(myHolder.img2);

    }

    @Override
    public int getItemCount() {
        return quotesList != null ? quotesList.size() : 0;
    }


}