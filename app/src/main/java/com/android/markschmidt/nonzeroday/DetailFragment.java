package com.android.markschmidt.nonzeroday;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by markschmidt on 4/28/15.
 */
public class DetailFragment extends Fragment{

    private Objective mObjective;
    private ListView mListView;
    private DayDataAdapter mDayDataAdapter;
    private ImageButton mUpButton;
    private ArrayList<DayData> mDayData;
    private TextView mTitleTextView;
    private ViewPager mViewPager;
    private GraphAdapter mGraphAdapter;
    private CirclePageIndicator mIndicator;

    private static final String ARG_ID = "id";
    private static final String TAG = "DetailFragment";


    public static DetailFragment newInstance(UUID id)
    {
        DetailFragment f = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, id.toString());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        mObjective = NonzeroDayLab.get(getActivity()).getObjective(UUID.fromString(args.getString(ARG_ID)));
        mDayData = mObjective.getDayData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail,container, false);

        mListView = (ListView)v.findViewById(R.id.daydata_listView);
        mDayDataAdapter = new DayDataAdapter(mObjective);
        mListView.setAdapter(mDayDataAdapter);

        mTitleTextView = (TextView)v.findViewById(R.id.detail_toolbar_title);
        mTitleTextView.setText(mObjective.getTitle());

        mUpButton = (ImageButton)v.findViewById(R.id.detail_up_button);
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });


        mGraphAdapter = new GraphAdapter(getActivity(), mDayData, null);
        mViewPager = (ViewPager)v.findViewById(R.id.graph_viewPager);
        mViewPager.setAdapter(mGraphAdapter);

        mIndicator = (CirclePageIndicator)v.findViewById(R.id.graph_indicator);
        final float density = getResources().getDisplayMetrics().density;
        mIndicator.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        mIndicator.setRadius(3 * density);
        mIndicator.setPageColor(getResources().getColor(R.color.colorBackground));
        mIndicator.setFillColor(getResources().getColor(R.color.colorBackground));
        mIndicator.setStrokeColor(getResources().getColor(R.color.colorPrimary));
        mIndicator.setStrokeWidth(2 * density);
        mIndicator.setSnap(true);


        mIndicator.setViewPager(mViewPager);






        return v;
    }




    private class DayDataAdapter extends ArrayAdapter<DayData>
    {
        private Objective mObjective;

        public DayDataAdapter(Objective o)
        {
            super(getActivity(),0, o.getDayData());
            mObjective = o;
        }

        @Override
        public DayData getItem(int position) {
            return super.getItem(getCount()-position-1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getActivity().getLayoutInflater().inflate(R.layout.daydata_list_item, null);

            DayData data = getItem(position);

            TextView dateText = (TextView)convertView.findViewById(R.id.daydata_date_textView);
            Date today = DayData.getActualDate(new Date());
            Date yesterday = DayData.getYesterday(today);
            Date dataDate = data.getDate();
            if(dataDate.after(today))
                dateText.setText("Today");
            else if(dataDate.after(yesterday))
                dateText.setText("Yesterday");
            else
                dateText.setText(DayData.dateAsString(dataDate));

            TextView valueText = (TextView)convertView.findViewById(R.id.daydata_value_textView);
            String valueSourceString;
            if(data.getScore() == 1)
                valueSourceString = "<b>"+ data.getScore() +"</b> " + mObjective.getSingularNoun() + "";
            else
                valueSourceString = "<b>"+ data.getScore() +"</b> " + mObjective.getMultipleNoun() + "";

            valueText.setText(Html.fromHtml(valueSourceString));

            TextView streakText = (TextView)convertView.findViewById(R.id.daydata_streak_textView);
            String streakSourceString = "<b>"+ data.getStreakAtDate() + "</b> streak";
            streakText.setText(Html.fromHtml(streakSourceString));

            TextView totalText = (TextView)convertView.findViewById(R.id.daydata_total_textView);
            String totalSourceString = "<b>"+ data.getTotalAtDate() + "</b> total";
            totalText.setText(Html.fromHtml(totalSourceString));

            return convertView;
        }
    }

    private class GraphAdapter extends PagerAdapter{
        private Context mContext;
        private LayoutInflater mLayoutInflater;
        private Date mAboutAWeekAgo;
        private Date mAboutAMonthAgo;
        private Date mNow;
        //private XYSeries mSeries;
        private int mMax;
        private ArrayList<SimpleXYSeries> mSeriesArrayList;

        public GraphAdapter(Context c, ArrayList<DayData> dayData, LineAndPointFormatter formatter)
        {
            mContext = c;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            /*Number[] dates = new Number[dayData.size()];
            Number[] values = new Number[dayData.size()];
            DayData d;
            mMax = 0;
            int score = 0;
            for(int i = 0; i < dayData.size(); i++)
            {
                d = dayData.get(i);
                dates[i] = DayData.getActualDate(d.getDate()).getTime();
                score = d.getScore();
                if(score > mMax)
                    mMax = score;
                values[i] = score;
            }
            mSeries = new SimpleXYSeries(Arrays.asList(dates), Arrays.asList(values), "DayData");
            */

            mSeriesArrayList = new ArrayList<SimpleXYSeries>();

            int i = 0;

            ArrayList<Number> datesArrayList = new ArrayList<Number>();
            ArrayList<Number> valuesArrayList = new ArrayList<Number>();
            DayData data;
            Date date;

            DayData previousData;
            Date previousDate;

            /*while(i < dayData.size())
            {
                data = dayData.get(i);
                date = DayData.getActualDate(data.getDate());

                if(i != 0) {
                    previousData = dayData.get(i - 1);
                    previousDate = DayData.getActualDate(previousData.getDate());

                    if (DayData.isSameDay(DayData.getYesterday(date), previousDate)) {
                        datesArrayList.add(date.getTime());
                        valuesArrayList.add(data.getScore());

                    } else {

                        mSeriesArrayList.add(new SimpleXYSeries(datesArrayList, valuesArrayList, "DayData"));
                        datesArrayList = new ArrayList<Number>();
                        valuesArrayList = new ArrayList<Number>();

                    }
                }
                else
                {
                    datesArrayList.add(date.getTime());
                    valuesArrayList.add(data.getScore());

                }
                i++;
                if(data.getScore() > mMax)
                    mMax = data.getScore();
            }*/

            while(i < dayData.size())
            {
                data = dayData.get(i);
                if(i-1 >= 0)
                {
                    previousData = dayData.get(i-1);
                    if(previousData.getStreakAtDate() + 1 != data.getStreakAtDate()) // is on a streak
                    {
                        mSeriesArrayList.add(new SimpleXYSeries(datesArrayList, valuesArrayList, "DayData"));
                        datesArrayList = new ArrayList<Number>();
                        valuesArrayList = new ArrayList<Number>();
                    }

                }
                i++;
                datesArrayList.add(DayData.getActualDate(data.getDate()).getTime());
                valuesArrayList.add(data.getScore());
                if(data.getScore() > mMax)
                    mMax = data.getScore();
            }
            mSeriesArrayList.add(new SimpleXYSeries(datesArrayList, valuesArrayList, "DayData"));

            Calendar calendar = Calendar.getInstance();
            mNow = new Date();
            calendar.setTime(mNow);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, day - 6);
            mAboutAWeekAgo = DayData.getActualDate(calendar.getTime());
            calendar.set(Calendar.DAY_OF_MONTH, day - 20);
            mAboutAMonthAgo = DayData.getActualDate(calendar.getTime());
            mNow = DayData.getActualDate(mNow);
        }


        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mLayoutInflater.inflate(R.layout.graph_pager_item, container, false);
            LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
            seriesFormat.configure(getActivity(), R.xml.line_point_formatter_with_plf1);
            seriesFormat.setFillPaint(null);
            final float density = getResources().getDisplayMetrics().density;
            seriesFormat.getVertexPaint().setStrokeWidth(density * 6);
            XYPlot graph = (XYPlot) v.findViewById(R.id.daydata_graph);

            graph.setRangeValueFormat(new DecimalFormat("0"));
            graph.setRangeBoundaries(0, BoundaryMode.FIXED, mMax + 5, BoundaryMode.FIXED);
            graph.setRangeStep(XYStepMode.SUBDIVIDE, 5);
            graph.setDomainValueFormat(new Format() {

                private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d");

                @Override
                public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                    long timestamp = ((Number) obj).longValue();
                    Date date = new Date(timestamp);
                    return dateFormat.format(date, toAppendTo, pos);
                }

                @Override
                public Object parseObject(String source, ParsePosition pos) {
                    return null;
                }
            });


            int xSteps = 0;
            for(XYSeries xy : mSeriesArrayList )
            {
                xSteps+= xy.size();
                graph.addSeries(xy, seriesFormat);
                Log.i(TAG, "Added series");
            }

            xSteps += mSeriesArrayList.size();

            if (xSteps > 5)
                xSteps = 5;

            if(position == 0)
            {
                graph.setTitle("Last Week");
                graph.setDomainBoundaries(mAboutAWeekAgo.getTime(), mNow.getTime(), BoundaryMode.FIXED);
                graph.setDomainStep(XYStepMode.SUBDIVIDE, 7);
            }
            else if(position == 1)
            {
                graph.setTitle("Last Month");
                graph.setDomainBoundaries(mAboutAMonthAgo.getTime(), mNow.getTime(), BoundaryMode.FIXED);
                graph.setDomainStep(XYStepMode.SUBDIVIDE, 5);

            }
            else
            {
                graph.setDomainStep(XYStepMode.SUBDIVIDE, xSteps);
                graph.setTitle("All Data");
            }

            graph.getGraphWidget().getGridBackgroundPaint().setColor(getResources().getColor(R.color.colorBackground));
            graph.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
            graph.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
            graph.getGraphWidget().getBackgroundPaint().setColor(getResources().getColor(R.color.colorBackground));
            graph.getBackgroundPaint().setColor(getResources().getColor(R.color.colorBackground));
            graph.getBorderPaint().setColor(getResources().getColor(R.color.colorBackground));
            graph.getLegendWidget().setVisible(false);
            graph.getTitleWidget().position(0, XLayoutStyle.RELATIVE_TO_CENTER, 0, YLayoutStyle.ABSOLUTE_FROM_BOTTOM, AnchorPosition.BOTTOM_MIDDLE);

            container.addView(v);
            return v;
        }



        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((XYPlot)object);
        }
    }

}
