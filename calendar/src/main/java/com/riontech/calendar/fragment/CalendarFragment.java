package com.riontech.calendar.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.riontech.calendar.R;
import com.riontech.calendar.Singleton;
import com.riontech.calendar.adapter.CalenderRecyclerViewAdapter;
import com.riontech.calendar.fragment.dao.CalendarDecoratorDao;
import com.riontech.calendar.fragment.dao.CalendarResponse;
import com.riontech.calendar.fragment.dao.Event;
import com.riontech.calendar.listener.UpdateCurrentDateTaskCountListener;
import com.riontech.calendar.utils.CalendarUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Dhaval Soneji on 31/3/16.
 */
public class CalendarFragment extends Fragment implements UpdateCurrentDateTaskCountListener {
    private static final String TAG = CalendarFragment.class.getSimpleName();
    public static String currentDateSelected;
    public Calendar mCalendar;
    Boolean nextNotAvailable = false, prevNotAvailable = false;
    private RecyclerView mGridview;
    private LinearLayout mLlDayList;
    private GregorianCalendar month;
    private CalenderRecyclerViewAdapter mCalendarGridviewAdapter;
    private boolean flagMaxMin = false;
    private DateFormat mDateFormat;
    private GregorianCalendar mPMonth;
    private int mMonthLength;
    private GregorianCalendar mPMonthMaxSet;
    private ArrayList<CalendarDecoratorDao> mEventList = new ArrayList<>();
    private ViewGroup rootView;
    private onPrevNextFunction listener;
    private TextView tvDate;
    private Date showDataForSelectedDate;
    private TextView completedDaysTextView, completedTaskTextView;
    private int totalCompletedDays;

    /**
     * create CalendarFragment object and call setCalendar().
     *
     * @return
     */
    public static CalendarFragment newInstance(CalendarFragment.onPrevNextFunction function, Boolean prevNotAvailable, Boolean nextNotAvailable) {
        CalendarFragment fragment = new CalendarFragment();
        fragment.setListener(function);

        fragment.prevNotAvailable = prevNotAvailable;
        fragment.nextNotAvailable = nextNotAvailable;
        if (fragment.month == null) {
            Singleton.getInstance().setMonth((GregorianCalendar) GregorianCalendar.getInstance());
            fragment.month = Singleton.getInstance().getMonth();
            fragment.mCalendar = fragment.month;
            Singleton.getInstance().setCurrentDate(
                    CalendarUtils.getCalendarDBFormat().format(Calendar.getInstance().getTime()));
            Singleton.getInstance().setTodayDate(
                    CalendarUtils.getCalendarDBFormat().format(Calendar.getInstance().getTime()));
            Singleton.getInstance().setEventManager(new ArrayList<Event>());
        }
        return fragment;
    }

    public void setListener(onPrevNextFunction listener) {
        this.listener = listener;
    }

    public void setData(ArrayList<CalendarDecoratorDao> mEventList) {
        this.mEventList = mEventList;
        mCalendarGridviewAdapter.setData(mEventList);
    }

    /**
     * initialize Calendar and for the first time load Current Month data.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (ViewGroup) inflater.inflate(R.layout.fragement, container, false);

        initCurrentMonthInGridview();

        if (Singleton.getInstance().getIsSwipeViewPager() == 2)
            refreshDays();

        return rootView;
    }

    /**
     * initialize DaysName(Sun,Mon,...) Layout,
     * initialize Current MonthHeader(June 2016) Layout,
     * initialize Gridview(Current month) With click event
     */
    private void initCurrentMonthInGridview() {

        mLlDayList = rootView.findViewById(R.id.llDayList);
        completedDaysTextView = rootView.findViewById(R.id.completedDaysTextV);
        completedTaskTextView = rootView.findViewById(R.id.completedTaskTextV);

        mCalendarGridviewAdapter = new CalenderRecyclerViewAdapter(getActivity(), mEventList, month, this, new CalenderRecyclerViewAdapter.ItemListener() {
            @Override
            public void onItemClickEvent(CalendarDecoratorDao dao) {
                for (int index = 0; index < mEventList.size(); index++) {
                    CalendarDecoratorDao item = mEventList.get(index);
                    item.setSelected(false);
                }

                String selectedDate = dao.getDate();
                dao.setSelected(true);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CalendarUtils.CALENDAR_DB_FORMAT);
                try {
                    showDataForSelectedDate = simpleDateFormat.parse(selectedDate);
                    tvDate.setText(android.text.format.DateFormat.format(CalendarUtils.getCalendarDateTitleFormat(), showDataForSelectedDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mCalendarGridviewAdapter.notifyDataSetChanged();
//                mCalendarGridviewAdapter.setSelected(v, selectedDate);
//                showDataForSelectedDate(position);
            }
        });

        mCalendar = month;
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mCalendar.setFirstDayOfWeek(Calendar.SUNDAY);
        mDateFormat = CalendarUtils.getCalendarDBFormat();

        mGridview = rootView.findViewById(R.id.gvCurrentMonthDayList);
        mGridview.setLayoutManager(new GridLayoutManager(getContext(), 7));
//        mGridview.setVerticalSpacing(30);
        mGridview.setAdapter(mCalendarGridviewAdapter);

        tvDate = rootView.findViewById(R.id.tvDate);
        showDataForSelectedDate = new Date();
        tvDate.setText(android.text.format.DateFormat.format(CalendarUtils.getCalendarDateTitleFormat(), showDataForSelectedDate));

        showDataForSelectedDate(0);

        rootView.findViewById(R.id.ivPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnButtonClickToggle(false);
                mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
                onPrevCalenderEventClick();
            }
        });
        rootView.findViewById(R.id.ivNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnButtonClickToggle(false);
                mCalendar.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
                onNextCalenderEventClick();
            }
        });
    }

    private void turnButtonClickToggle(boolean isOn) {
        rootView.findViewById(R.id.ivPrev).setClickable(isOn);
        rootView.findViewById(R.id.ivNext).setClickable(isOn);
    }

    private void showDataForSelectedDate(int position) {
        if (mEventList.size() == 0)
            return;
        CalendarDecoratorDao dao = mEventList.get(position);

        completedDaysTextView.setText(dao.getCount() + " Days");
    }

    private void onNextCalenderEventClick() {
        listener.onNext();
    }

    private void onPrevCalenderEventClick() {
        listener.onPrev();
    }

    /**
     * refresh current month
     */
    public void refreshCalendar() {
        refreshDays();
        showDataForSelectedDate = mCalendar.getTime();
        tvDate.setText(android.text.format.DateFormat.format(CalendarUtils.getCalendarDateTitleFormat(), showDataForSelectedDate));
        turnButtonClickToggle(true);
    }

    public void setTotalCompletedDays(int totalCompletedDays) {
        this.totalCompletedDays = totalCompletedDays;
        completedDaysTextView.setText(totalCompletedDays + " Days");
    }

    /**
     * refresh current month days
     */
    public void refreshDays() {

        //clear List
        mEventList.clear();
        //create clone
//        mPMonth = (GregorianCalendar) mCalendar.clone();
//
//        CalendarGridviewAdapter.firstDay = mCalendar.get(GregorianCalendar.DAY_OF_WEEK);
//
//        int mMaxWeekNumber = mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
//
//        mMonthLength = mMaxWeekNumber * 7;
//        int mMaxP = getmMaxP();
//        int mCalMaxP = mMaxP - (CalendarGridviewAdapter.firstDay - 1);
//
//        mPMonthMaxSet = (GregorianCalendar) mPMonth.clone();
//
//        mPMonthMaxSet.set(GregorianCalendar.DAY_OF_MONTH, mCalMaxP + 1);

        setData(getCalendarData());

    }

    /**
     * @return
     */
    private CalendarResponse getCalendarData() {
        CalendarResponse calendarResponse = new CalendarResponse();
        calendarResponse.setStartmonth(Singleton.getInstance().getStartMonth());
        calendarResponse.setEndmonth(Singleton.getInstance().getEndMonth());
        calendarResponse.setMonthdata(Singleton.getInstance().getEventManager());
        return calendarResponse;
    }

    /**
     * @param calendarResponse
     */
    private void setData(CalendarResponse calendarResponse) {

        mLlDayList.setVisibility(View.VISIBLE);
        mGridview.setVisibility(View.VISIBLE);

        Calendar dateCalender = (Calendar) mCalendar.clone();
        dateCalender.set(Calendar.DAY_OF_MONTH, 1);
        int addExtraDays = dateCalender.get(GregorianCalendar.DAY_OF_WEEK) - 1;
        if (addExtraDays >= 1) {
            dateCalender.set(Calendar.DAY_OF_MONTH, 1 - addExtraDays);
            for (int index = 0; index < addExtraDays; index++) {
                String mItemValue = mDateFormat.format(dateCalender.getTime());
                CalendarDecoratorDao eventDao = new CalendarDecoratorDao(mItemValue, 0);
                mEventList.add(eventDao);
                dateCalender.set(Calendar.DAY_OF_MONTH, dateCalender.get(Calendar.DAY_OF_MONTH) + 1);
            }
        }
        int monthDays = dateCalender.getActualMaximum(Calendar.DAY_OF_MONTH);
        ArrayList<Event> monthDataList = calendarResponse.getMonthdata();
        for (int index = 0; index < monthDays; index++) {
            String mItemValue = mDateFormat.format(dateCalender.getTime());
            int count = 0;
            for (int monethDataIndex = 0; monethDataIndex < monthDataList.size(); monethDataIndex++) {
                if (mItemValue.equalsIgnoreCase(monthDataList.get(monethDataIndex).getDate())) {
                    count = Integer.parseInt(monthDataList.get(monethDataIndex).getCount());
                }
            }

            CalendarDecoratorDao eventDao = new CalendarDecoratorDao(mItemValue, count);
            mEventList.add(eventDao);
            dateCalender.set(Calendar.DAY_OF_MONTH, dateCalender.get(Calendar.DAY_OF_MONTH) + 1);
        }
        mCalendarGridviewAdapter.notifyDataSetChanged();
    }

    /**
     * setup next month and check for calendar range
     */

    public GregorianCalendar setNextMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMaximum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) + 1), month.getActualMinimum(GregorianCalendar.MONTH), 1);
            Singleton.getInstance().setMonth(month);
        } else {
            month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) + 1);
            Singleton.getInstance().setMonth(month);
        }
        return month;
    }

    public GregorianCalendar setPreviousMonth() {
        if (month.get(GregorianCalendar.MONTH) == month.getActualMinimum(GregorianCalendar.MONTH)) {
            month.set((month.get(GregorianCalendar.YEAR) - 1), month.getActualMaximum(GregorianCalendar.MONTH), 1);
            Singleton.getInstance().setMonth(month);
        } else {
            month.set(GregorianCalendar.MONTH, month.get(GregorianCalendar.MONTH) - 1);
            Singleton.getInstance().setMonth(month);
        }
        return month;
    }

    /**
     * @return
     */
    private int getmMaxP() {
        int maxP = 0;
        /*if (mCalendar.get(GregorianCalendar.MONTH) == mCalendar
                .getActualMinimum(GregorianCalendar.MONTH)) {
            mPMonth.set((mCalendar.get(GregorianCalendar.YEAR) - 1),
                    mCalendar.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            mPMonth.set(GregorianCalendar.MONTH,
                    mCalendar.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = mPMonth.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);*/

        return maxP;
    }

    @Override
    public void onCompletedTaskCountUpdate(String count) {
        completedTaskTextView.setText(count + " / 6");
    }

    public interface onPrevNextFunction {
        void onPrev();

        void onNext();
    }

}
