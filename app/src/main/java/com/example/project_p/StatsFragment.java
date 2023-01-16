package com.example.project_p;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private PieChart pieChart;
    private TextView date1_TextView;
    private TextView date2_TextView;
    private CalendarView calendar1;
    private CalendarView calendar2;
    private LinearLayout details;
    private Statistics stats = new Statistics();


    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();
       // SharedPreferences settings = mainActivity.getSharedPreferences("settings",mainActivity.MODE_PRIVATE);

        pieChart = view.findViewById(R.id.piechart);
        date1_TextView = view.findViewById(R.id.date1);
        date2_TextView = view.findViewById(R.id.date2);
        calendar1 = view.findViewById(R.id.calendar1);
        calendar2 = view.findViewById(R.id.calendar2);
        details = view.findViewById(R.id.detailsLayout);
        date1_TextView.setText(firstDay(new Date()));
        date2_TextView.setText(textFromDate(new Date()));
        calendar1.setVisibility(View.GONE);
        calendar1.setDate(dateFromText(firstDay(new Date())));
        calendar2.setVisibility(View.GONE);
        pieChart.startAnimation();

        new UpdateChart().execute();
        ///
        date1_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendar1.getVisibility() == View.VISIBLE){
                    calendar1.setVisibility(View.GONE);
                }
                else {
                    calendar1.setVisibility(View.VISIBLE);
                }
            }
        });
        date2_TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendar2.getVisibility() == View.VISIBLE){
                    calendar2.setVisibility(View.GONE);
                }
                else {
                    calendar2.setVisibility(View.VISIBLE);
                }
            }
        });
        calendar1.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                date1_TextView.setText(String.format("%d.%d.%d",day,month+1,year));
                calendar1.setVisibility(View.GONE);
                calendar2.setVisibility(View.VISIBLE);
                new UpdateChart().execute();

            }
        });
        calendar2.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                date2_TextView.setText(String.format("%d.%d.%d",day,month+1,year));
                calendar2.setVisibility(View.GONE);
                new UpdateChart().execute();
                pieChart.startAnimation();
            }
        });
        return view;
    }

    private void setData(){
        pieChart.clearChart();
        int s=0;
        for(Statistics.GoodsType type : stats.sums.keySet()){
            pieChart.addPieSlice(new PieModel(
                    type.getRus(),
                    stats.sums.get(type),
                    getChartColor(type.ordinal())
                    ));
            s++;
        }
    }
    private void setDetails(){
        details.removeAllViews();
        int s=0;
        for(Statistics.GoodsType type : stats.sums.keySet()){
            CardView cardView = new CardView((MainActivity)getActivity());
            cardView.setCardBackgroundColor(getChartColor(type.ordinal()));
            TextView textView = new TextView((MainActivity)getActivity());
            textView.setText(type.getRus()+" "+stats.sums.get(type));
            textView.setTextColor(getResources().getColor(R.color.white));
            cardView.addView(textView);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6,3,3,6);
            cardView.setLayoutParams(lp);
            details.addView(cardView);
            s++;
        }
    }

    public int getChartColor(int seed) {
        switch (seed){
            case 1: return Color.rgb(237,220,100);
            case 2: return Color.rgb(68,85,194);
            case 3: return Color.rgb(255,22,32);
            case 4: return Color.rgb(246,209,127);
            case 5: return Color.rgb(0,160,180);
            case 6: return Color.rgb(191,27,146);
            case 7: return Color.rgb(241,164,94);
            case 8: return Color.rgb(0,162,103);
            case 9: return Color.rgb(100,43,151);
            case 10: return Color.rgb(255,137,81);
            case 11: return Color.rgb(117,196,79);
            case 12: return Color.rgb(89,59,158);
            case 13: return Color.rgb(240,214,0);
            case 14: return Color.rgb(20,45,186);
            case 15: return Color.rgb(215,13,18);
            default: return Color.rgb(181,181,181);
        }
    }
    private String textFromDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.format("%d.%d.%d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH)+1,
                calendar.get(Calendar.YEAR));
    }
    private String firstDay(Date date){
        return "1" + textFromDate(date).substring(2);
    }
    private long dateFromText(String date){
        try {
            return new SimpleDateFormat("dd.MM.yyyy").parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date().getTime();
    }

    class UpdateChart extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            MainActivity mainActivity = (MainActivity)getActivity();
            stats.readMail(mainActivity.MAILBOX,
                    dateFromText(String.valueOf(date1_TextView.getText())),
                    dateFromText(String.valueOf(date2_TextView.getText())));
            Log.i("prop",new Gson().toJson(stats.sums));
            setData();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setDetails();
        }
    }
}