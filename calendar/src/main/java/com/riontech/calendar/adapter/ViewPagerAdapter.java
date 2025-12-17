package com.riontech.calendar.adapter;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.riontech.calendar.CustomCalendar;
import com.riontech.calendar.fragment.CalendarFragment;

/**
 * Created by Dhaval Soneji on 31/3/16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private int mCount;
    private CustomCalendar mCalendar;
    private CalendarFragment.onPrevNextFunction function;


    public ViewPagerAdapter(FragmentManager fm, int count, CustomCalendar calendar, CalendarFragment.onPrevNextFunction function) {
        super(fm);
        this.mCount = count;
        this.mCalendar = calendar;
        this.function = function;
    }

    @Override
    public Fragment getItem(int position) {
        return CalendarFragment.newInstance(function, position == 0, position == mCount);
    }

    @Override
    public int getCount() {
        return this.mCount;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
