package com.mayur.personalitydevelopment.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mayur.personalitydevelopment.fragment.Tab1;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;
    private Tab1 tab1;

    public PagerAdapter(FragmentManager fm, int tabCount, Tab1 tab1) {
        super(fm);
        this.tabCount = tabCount;
        this.tab1 = tab1;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return tab1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
