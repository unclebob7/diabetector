package com.example.bob.wrist_doc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.Direction;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;
import com.alespero.expandablecardview.ExpandableCardView;

import java.util.Random;

public class IndexFragment extends Fragment implements View.OnClickListener{
    private View view;
    private ExpandableCardView glucose_card;
    private ExpandableCardView sodium_card;
    private ExpandableCardView potassium_card;
    private ExpandableCardView ph_card;
    private ExpandableCardView lactate_card;
    private ExpandableCardView calcium_card;
    private CircleProgressView mCircleView;
    private Button cb_test;
    private TextView glucose_assessment_tv;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.index, container, false);
        init();
        return view;
    }

    // initialize all components of index
    public void init() {

        // element indicator
        glucose_card = (ExpandableCardView) view.findViewById(R.id.glucose);
        sodium_card = (ExpandableCardView) view.findViewById(R.id.sodium);
        potassium_card = (ExpandableCardView) view.findViewById(R.id.potassium);
        ph_card = (ExpandableCardView) view.findViewById(R.id.ph);
        lactate_card = (ExpandableCardView) view.findViewById(R.id.lactate);
        calcium_card = (ExpandableCardView) view.findViewById(R.id.calcium);

        glucose_assessment_tv = (TextView) view.findViewById(R.id.glucose_assessment_tv);

        // glucose-thread trigger
        cb_test = (Button) view.findViewById(R.id.cb_test);
        cb_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "11111111111111", Toast.LENGTH_SHORT).show();
                new Thread(new thread_glucose()).start();
            }
        });

        // glucose graphic progress-circle
        mCircleView = (CircleProgressView) view.findViewById(R.id.circleView);
        mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d("IndexActivity:", "Progress Changed: " + value);
            }
        });

        //value setting
        mCircleView.setMaxValue(100);
        mCircleView.setValue(0);
        mCircleView.setValueAnimated(75f);

        // growing/rotating counter-clockwise
        mCircleView.setDirection(Direction.CCW);

        //show unit
        mCircleView.setUnit("*10^-1 mol/L");
        mCircleView.setUnitVisible(true);

        mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        mCircleView.setRimColor(getResources().getColor(R.color.colorNavText));
        mCircleView.setBarColor(getResources().getColor(R.color.colorPrimary));
        mCircleView.setInnerContourColor(getResources().getColor(R.color.colorNavText));
        mCircleView.setInnerContourSize(0);
        mCircleView.setOuterContourSize(0);
        mCircleView.setRimWidth(100);
        mCircleView.setBarWidth(60);

        // gradient color
       mCircleView.setTextColorAuto(true); //previous set values are ignored

        mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.glucose:
                new Thread(new thread_glucose()).start();
                break;

            case R.id.cb_test:
//                new Thread(new thread_glucose()).start();
                Toast.makeText(getContext(), "11111111111111", Toast.LENGTH_SHORT).show();
                mCircleView.spin();
                break;

        }
    }


    public class thread_glucose implements Runnable {
        private int i = 0;
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (i++ < 100) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    mCircleView.setValue(random_index);
                    Thread.sleep(2000);     // sleep 1000ms
                    Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }

    }
}




