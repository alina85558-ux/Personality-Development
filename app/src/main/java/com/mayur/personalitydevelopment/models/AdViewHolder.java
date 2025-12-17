package com.mayur.personalitydevelopment.models;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.mayur.personalitydevelopment.R;

public class AdViewHolder extends RecyclerView.ViewHolder {
    public TemplateView template;

    public AdViewHolder(@NonNull View itemView) {
        super(itemView);

        template = itemView.findViewById(R.id.ad_template_view);

        NativeTemplateStyle styles = new
                NativeTemplateStyle.Builder().build();

        template.setStyles(styles);
    }

    public void setNativeAd(NativeAd nativeAd){

        template.setNativeAd(nativeAd);
    }
}
