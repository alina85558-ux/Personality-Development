package com.riontech.calendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.riontech.calendar.R;
import com.riontech.calendar.Singleton;
import com.riontech.calendar.fragment.dao.CalendarDecoratorDao;
import com.riontech.calendar.listener.UpdateCurrentDateTaskCountListener;
import com.riontech.calendar.utils.CalendarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalenderRecyclerViewAdapter extends RecyclerView.Adapter<CalenderRecyclerViewAdapter.CalendarGridViewHolder> {
    public static int firstDay;
    private final Context mContext;
    private final ItemListener itemListener;
    private final String currentDate;
    private final UpdateCurrentDateTaskCountListener updateCurrentDateTaskCountListener;
    private ArrayList<CalendarDecoratorDao> mEventList;
    private boolean isFromItemClick = false;


    public CalenderRecyclerViewAdapter(Context c, ArrayList<CalendarDecoratorDao> items, GregorianCalendar month, UpdateCurrentDateTaskCountListener updateCurrentDateTaskCountListener, ItemListener itemListener) {
        this.mEventList = items;
        mContext = c;
        this.itemListener = itemListener;
        this.updateCurrentDateTaskCountListener = updateCurrentDateTaskCountListener;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        currentDate = sdf.format(new Date());
        if (month == null) {
            Singleton.getInstance().setMonth((GregorianCalendar) GregorianCalendar.getInstance());
            firstDay = Singleton.getInstance().getMonth().get(GregorianCalendar.DAY_OF_WEEK);
        } else {
            firstDay = month.get(GregorianCalendar.DAY_OF_WEEK);
        }
    }

    @NonNull
    @Override
    public CalenderRecyclerViewAdapter.CalendarGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        CalenderRecyclerViewAdapter.CalendarGridViewHolder holder;

        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = vi.inflate(R.layout.calendar_item, null);
        holder = new CalendarGridViewHolder(convertView);
        int dimen = mContext.getResources().getDimensionPixelSize(R.dimen.common_40_dp);
        GridView.LayoutParams pParams = new GridView.LayoutParams(dimen, dimen);
        convertView.setLayoutParams(pParams);
        // comment here


        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CalenderRecyclerViewAdapter.CalendarGridViewHolder viewHolder, int position) {
        final CalendarDecoratorDao content = mEventList.get(position);
        content.setPosition(position);

        if (content.isSelected()) {
            viewHolder.itemView.setBackgroundResource(R.drawable.circle_shape_selected);

            TextView txt = viewHolder.itemView.findViewById(R.id.date);
            txt.setTextColor(Color.BLACK);
        } else {
            viewHolder.itemView.setBackgroundResource(R.drawable.list_item_background);

            TextView txt = viewHolder.itemView.findViewById(R.id.date);
            txt.setTextColor(Color.BLACK);
        }
        String day = content.getDay();
        boolean isSameMonth = false;
        if (!isFromItemClick && content.getDate().equalsIgnoreCase(currentDate)) {
            isSameMonth = true;
            updateCurrentDateTaskCountListener.onCompletedTaskCountUpdate(content.getCount() + "");
            viewHolder.itemView.setBackgroundResource(R.drawable.circle_shape_selected);
        }
        int resourceId = 0;
        switch (content.getCount()) {
            case 1:
                resourceId = R.drawable.level1;
                break;
            case 2:
                resourceId = R.drawable.level2;
                break;
            case 3:
                resourceId = R.drawable.level3;
                break;
            case 4:
                resourceId = R.drawable.level4;
                break;
            case 5:
                resourceId = R.drawable.level5;
                break;
            case 6:
                resourceId = R.drawable.level6;
                break;
        }
        viewHolder.ivFillPercentage.setImageResource(resourceId);
        viewHolder.dayView.setText(day);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromItemClick = true;
                itemListener.onItemClickEvent(content);
                updateCurrentDateTaskCountListener.onCompletedTaskCountUpdate(content.getCount() + "");
            }
        });
        viewHolder.setDay(viewHolder.itemView, content);
//        viewHolder.setSelectedView(viewHolder.itemView, content);
        viewHolder.bindDate(content);
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }

    public void setData(ArrayList<CalendarDecoratorDao> mEventList) {
        this.mEventList = mEventList;
        notifyDataSetChanged();
    }


    public interface ItemListener {
        void onItemClickEvent(CalendarDecoratorDao dao);
    }

    class CalendarGridViewHolder extends RecyclerView.ViewHolder {
        TextView dayView;
        ImageView ivFillPercentage;

        /**
         * @param v
         */
        public CalendarGridViewHolder(View v) {
            super(v);
            setLayoutParam(v);
            dayView = v.findViewById(R.id.date);
            ivFillPercentage = v.findViewById(R.id.ivFillPercentage);
        }

        /**
         * @param view
         */
        private void setLayoutParam(View view) {
            int dimen = mContext.getResources().getDimensionPixelSize(R.dimen.common_40_dp);
            GridView.LayoutParams pParams = new GridView.LayoutParams(dimen, dimen);
            view.setLayoutParams(pParams);
        }


        /**
         * @param view
         * @param content
         */
        private void setDay(View view, CalendarDecoratorDao content) {

            Calendar calendar = Singleton.getInstance().getMonth();
            int globalMonth = calendar.get(Calendar.MONTH);
            int globalYear = calendar.get(Calendar.YEAR);
            dayView.setTextColor(Color.BLACK);
            view.setVisibility(View.VISIBLE);
            try {
                Date dateTemp = CalendarUtils.getCalendarDBFormat().parse(content.getDate());
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dateTemp);
                int itemMonth = calendar1.get(Calendar.MONTH);
                int itemYear = calendar1.get(Calendar.YEAR);
                if ((itemMonth == globalMonth) && (itemYear == globalYear)) {
                    view.setVisibility(View.VISIBLE);
                    int currentMonth = new Date().getMonth();
                    if (!isFromItemClick) {
                        if (currentMonth != itemMonth) {
                            if (calendar1.get(Calendar.DATE) == 1 || calendar1.get(Calendar.DATE) == Integer.parseInt("01")) {
                                view.setBackgroundResource(R.drawable.circle_shape_selected);
                            }
                        }
                    }
                } else {
                    view.setVisibility(View.INVISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            /*String day = content.getDay();
            view.setVisibility(View.VISIBLE);
            if ((Integer.parseInt(day) > 1) && (content.getPosition() < firstDay)) {
                view.setVisibility(View.INVISIBLE);
            } else if ((Integer.parseInt(day) < 7) && (content.getPosition() > 28)) {
                view.setVisibility(View.INVISIBLE);
            } else {
                dayView.setTextColor(Color.BLACK);
            }*/
        }

        /**
         * @param content
         */
        public void bindDate(CalendarDecoratorDao content) {
            String date = content.getDate();
            if (date.length() == 1) {
                date = "0" + date;
            }

            setDecoratorVisibility(date, content);
        }

        /**
         * @param date
         * @param content
         */
        private void setDecoratorVisibility(String date, CalendarDecoratorDao content) {

        }
    }
}
