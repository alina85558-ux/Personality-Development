package com.mayur.personalitydevelopment.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.base.BaseActivity;
import com.mayur.personalitydevelopment.databinding.ActivityAboutAppBinding;

public class AboutAppActivity extends BaseActivity {

    private String email="personalitydevelopmentapp@gmail.com";
    private ActivityAboutAppBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(AboutAppActivity.this, R.layout.activity_about_app);
        setColorData();
    }

    void setColorData(){
        if(sp.getBoolean("light",false)){
            binding.t1.setTextColor(Color.parseColor("#ffffff"));
            binding.t2.setTextColor(Color.parseColor("#ffffff"));
            binding.t3.setTextColor(Color.parseColor("#ffffff"));
            binding.t4.setTextColor(Color.parseColor("#ffffff"));
            binding.t5.setTextColor(Color.parseColor("#ffffff"));
            binding.tv.setTextColor(Color.parseColor("#ffffff"));
            binding.linear.setBackgroundColor(Color.parseColor("#464646"));
            binding.scroll.setBackgroundColor(Color.parseColor("#464646"));
            binding.card.setCardBackgroundColor(Color.parseColor("#363636"));
            binding.card2.setCardBackgroundColor(Color.parseColor("#363636"));
        }else{
            binding.tv.setTextColor(Color.parseColor("#000000"));
            binding.linear.setBackgroundColor(getResources().getColor(R.color.white_pressed));
            binding.t1.setTextColor(Color.parseColor("#000000"));
            binding.t2.setTextColor(Color.parseColor("#000000"));
            binding.t3.setTextColor(Color.parseColor("#000000"));
            binding.t4.setTextColor(Color.parseColor("#000000"));
            binding.t5.setTextColor(Color.parseColor("#000000"));
            binding.scroll.setBackgroundColor(Color.parseColor("#ffffff"));
            binding.card.setCardBackgroundColor(Color.parseColor("#ffffff"));
            binding.card2.setCardBackgroundColor(Color.parseColor("#ffffff"));     }
    }

    public void Email(View v) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Personality Development App");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Mayur!\nHere are few suggestions/complaints/feature request about the BestifyMe app:");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public void rate_app(View v){
        Uri uri = Uri.parse("market://details?id=com.mayur.personalitydevelopment");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.mayur.personalitydevelopment")));
        }
    }
}
