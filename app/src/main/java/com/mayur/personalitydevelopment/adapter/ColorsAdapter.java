package com.mayur.personalitydevelopment.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.activity.CreateArticleActivity;

import java.util.ArrayList;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder> {

    private Context context;
    private int currentPosition = 0;
    private ArrayList<String> colors = new ArrayList<>();
    private int sltPositin = 0;
    public ColorsAdapter(Context context, ArrayList<String> colorsList) {
        this.context = context;
        this.colors = colorsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(colors.get(position)));
        drawable.setShape(GradientDrawable.OVAL);

        if (sltPositin == position){
            drawable.setStroke( 6, ContextCompat.getColor(context,R.color.half_black));
            //holder.imgColors.setBackgroundColor(context.getResources().getColor(R.color.Light_black1));
        }else{

            //drawable.setStroke( 2, ContextCompat.getColor(context,R.color.half_black));

            holder.imgColors.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        drawable.setSize(60,60);
        holder.imgColors.setImageDrawable(drawable);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sltPositin = position;
                Log.e("Color ", colors.get(position)+"");
                ((CreateArticleActivity)context).onColorsClick(position,colors.get(position));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgColors;

        public ViewHolder(View v) {
            super(v);
            imgColors = v.findViewById(R.id.imgColors);
        }
    }

}
