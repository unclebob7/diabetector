package com.example.bob.wrist_doc;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

/**
 * Created by Belal on 1/23/2018.
 */

public class AdviceFragment extends Fragment {
    private View view;
    private LineChart mChart;
    private Pusher pusher;

    // PUSHER setup
    private static final String PUSHER_API_KEY = "fd6a8dc6ba53bbb0ee19";
    private static final String CHANNEL_NAME = "my-channel";
    private static final String PUSHER_APP_CLUSTER = "ap3";
    private static final String EVENT_NAME = "my-event";

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.advice, container, false);
        init();
        new Thread(new thread_dataRx()).start();
        return view;
    }

    private void init() {
        // correlating xml file
        mChart = (LineChart) view.findViewById(R.id.chart);

        //real-time chart config
        setupChart();
        setupAxes();
        setupData();
        setupLegend();

        // setting up PUSHER implementation
        PusherOptions options = new PusherOptions();
        options.setCluster(PUSHER_APP_CLUSTER);
        Pusher pusher = new Pusher(PUSHER_API_KEY, options);
        Channel channel = pusher.subscribe(CHANNEL_NAME);

        SubscriptionEventListener eventListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channel, String event, String data) {
                new Thread(new thread_dataRx()).start();
            }
        };
//        channel.bind(EVENT_NAME, new SubscriptionEventListener() {
//            @Override
//            public void onEvent(String channelName, String eventName, final String data) {
//                System.out.println(data);
//                Log.d("GCP", data);
//            }
//    });

        pusher.connect();
    }

    private void setupChart() {
        // enable descriptionn text
        mChart.getDescription().setEnabled(false);
        // enable touch gestures
        mChart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        // enable scaling
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        // set an alternative background color
        mChart.setBackgroundColor(Color.DKGRAY);
    }

    private void setupAxes() {
        // setup x-axis
        XAxis x1 = mChart.getXAxis();
        x1.setTextColor(Color.WHITE);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(true);

        // setup y-axis(left)
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        // setup y-axis(right)
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Add an alert up-limit (glucose)
        LimitLine l1 = new LimitLine(70f, "Glucose Upper Limit");
        l1.setLineWidth(2f);
        l1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        l1.setTextSize(10f);
        l1.setTextColor(Color.WHITE);

        // reset all limit lines to avoid overlapping lines

        // limit lines are drawn underneath data
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);
    }

    private void setupLegend() {
        // get the legend (only possible after setting data)
        Legend lg = mChart.getLegend();

        //modify the legend
        lg.setForm(Legend.LegendForm.CIRCLE);
        lg.setTextColor(Color.WHITE);
    }

    // if no data-entry receive, use createSet() as the default method
    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Glucose index");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(ColorTemplate.VORDIPLOM_COLORS[0]);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        // To show values of each point
        set.setDrawValues(true);

        return set;
    }

    private void addEntry(int timing, float input_variable) {
        LineData data = mChart.getData();

        if(data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if(set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(timing, input_variable), 0);

            data.notifyDataChanged();
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(10);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    public class thread_dataRx implements Runnable {
        // 方法内变量不能定义 OR 初始化
        int i = 0;
        public void run() {
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random() * 40 + 10);
                    addEntry(i, random_index);
                    i++;
                    Thread.sleep(1000);     // sleep 1000ms
                } catch (Exception e) {

                }
            }
        }
    }

}
