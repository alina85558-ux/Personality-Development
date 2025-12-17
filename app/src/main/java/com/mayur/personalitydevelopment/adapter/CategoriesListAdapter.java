package com.mayur.personalitydevelopment.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.activity.FilterResultActivity;
import com.mayur.personalitydevelopment.models.CategoriesData;
import com.mayur.personalitydevelopment.viewholder.CategoriesHolder;

import java.util.ArrayList;
import java.util.List;

public class CategoriesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private CategoriesHolder textViewHolder = new CategoriesHolder();
    private List<CategoriesData.CategoriesBean> categoriesDataList = new ArrayList<>();
    private Activity context;

    public CategoriesListAdapter(List<CategoriesData.CategoriesBean> categoriesList, Activity context) {
        this.categoriesDataList = categoriesList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        textViewHolder.setItemBinding(context, parent);
        return textViewHolder.getHolder();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final CategoriesHolder.MyHolder myHolder = textViewHolder.castHolder(holder);
        myHolder.textView.setText(categoriesDataList.get(position).getName());
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/MRegular.ttf");
        myHolder.textView.setTypeface(font);
        myHolder.textView.setTextSize(14);

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilterResultActivity.class);
                intent.putExtra("category_id", categoriesDataList.get(position).getId());
                intent.putExtra("category_name", categoriesDataList.get(position).getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesDataList != null ? categoriesDataList.size() : 0;
    }

}