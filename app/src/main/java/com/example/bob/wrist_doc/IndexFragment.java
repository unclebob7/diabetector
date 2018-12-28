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

public class IndexFragment extends Fragment {
    private View view;
    private ExpandableCardView glucose_card;
    private ExpandableCardView sodium_card;
    private ExpandableCardView potassium_card;
    private ExpandableCardView ph_card;
    private ExpandableCardView lactate_card;
    private ExpandableCardView calcium_card;
    private CircleProgressView glucose_mCircleView;
    private CircleProgressView potassium_mCircleView;
    private CircleProgressView sodium_mCircleView;
    private CircleProgressView ph_mCircleView;
    private CircleProgressView lactate_mCircleView;
    private CircleProgressView calcium_mCircleView;

    private TextView glucose_assessment_tv;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.index, container, false);
        init();
        element_thread_initializer();
        return view;
    }

    // initialize all components of index
    public void init() {
        // element card indicator
        element_card_para();

        // setting up parameters of circle progress bar
        circle_progress_para();
    }

    // element card indicator
    private void element_card_para() {
        glucose_card = (ExpandableCardView) view.findViewById(R.id.glucose);
        sodium_card = (ExpandableCardView) view.findViewById(R.id.sodium);
        potassium_card = (ExpandableCardView) view.findViewById(R.id.potassium);
        ph_card = (ExpandableCardView) view.findViewById(R.id.ph);
        lactate_card = (ExpandableCardView) view.findViewById(R.id.lactate);
        calcium_card = (ExpandableCardView) view.findViewById(R.id.calcium);
    }
    // setting up parameters of circle progress bar
    private void circle_progress_para() {
        // glucose graphic progress-circle
        glucose_mCircleView = (CircleProgressView) view.findViewById(R.id.glucose_circleView);
        potassium_mCircleView = (CircleProgressView) view.findViewById(R.id.potassium_circleView);
        ph_mCircleView = (CircleProgressView) view.findViewById(R.id.ph_circleView);
        lactate_mCircleView = (CircleProgressView) view.findViewById(R.id.lactate_circleView);
        sodium_mCircleView = (CircleProgressView) view.findViewById(R.id.sodium_circleView);
        calcium_mCircleView = (CircleProgressView) view.findViewById(R.id.calcium_circleView);

        // setting up parameters for each circle progress-bar of each elements
        glucose_mCircleView_set();
        potassium_mCircleView_set();
        ph_mCircleView_set();
        lactate_mCircleView_set();
        sodium_mCircleView_set();
        calcium_mCircleView_set();
    }

    public void glucose_mCircleView_set() {

        glucose_mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d("IndexActivity:", "Progress Changed: " + value);
            }
        });

        //value setting
        glucose_mCircleView.setMaxValue(100);
        glucose_mCircleView.setValue(0);
        glucose_mCircleView.setValueAnimated(75f);

        // growing/rotating counter-clockwise
        glucose_mCircleView.setDirection(Direction.CCW);

        //show unit
        glucose_mCircleView.setUnit("*10^-1 mol/L");
        glucose_mCircleView.setUnitVisible(true);

        glucose_mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        glucose_mCircleView.setRimColor(getResources().getColor(R.color.glucose));
        glucose_mCircleView.setBarColor(getResources().getColor(R.color.glucose));
        glucose_mCircleView.setInnerContourColor(getResources().getColor(R.color.glucose));
        glucose_mCircleView.setInnerContourSize(0);
        glucose_mCircleView.setOuterContourSize(0);
        glucose_mCircleView.setRimWidth(100);
        glucose_mCircleView.setBarWidth(60);

        // gradient color
        glucose_mCircleView.setTextColorAuto(true); //previous set values are ignored

        glucose_mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                glucose_mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                glucose_mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                glucose_mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                glucose_mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    public void potassium_mCircleView_set() {

        potassium_mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d("IndexActivity:", "Progress Changed: " + value);
            }
        });

        //value setting
        potassium_mCircleView.setMaxValue(100);
        potassium_mCircleView.setValue(0);
        potassium_mCircleView.setValueAnimated(75f);

        // growing/rotating counter-clockwise
        potassium_mCircleView.setDirection(Direction.CCW);

        //show unit
        potassium_mCircleView.setUnit("*10^-1 mol/L");
        potassium_mCircleView.setUnitVisible(true);

        potassium_mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        potassium_mCircleView.setRimColor(getResources().getColor(R.color.potassium));
        potassium_mCircleView.setBarColor(getResources().getColor(R.color.potassium));
        potassium_mCircleView.setInnerContourColor(getResources().getColor(R.color.potassium));
        potassium_mCircleView.setInnerContourSize(0);
        potassium_mCircleView.setOuterContourSize(0);
        potassium_mCircleView.setRimWidth(100);
        potassium_mCircleView.setBarWidth(60);

        // gradient color
        potassium_mCircleView.setTextColorAuto(true); //previous set values are ignored

        potassium_mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                potassium_mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                potassium_mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                potassium_mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                potassium_mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    public void sodium_mCircleView_set() {

        sodium_mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d("IndexActivity:", "Progress Changed: " + value);
            }
        });

        //value setting
        sodium_mCircleView.setMaxValue(100);
        sodium_mCircleView.setValue(0);
        sodium_mCircleView.setValueAnimated(75f);

        // growing/rotating counter-clockwise
        sodium_mCircleView.setDirection(Direction.CCW);

        //show unit
        sodium_mCircleView.setUnit("*10^-1 mol/L");
        sodium_mCircleView.setUnitVisible(true);

        sodium_mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        sodium_mCircleView.setRimColor(getResources().getColor(R.color.sodium));
        sodium_mCircleView.setBarColor(getResources().getColor(R.color.sodium));
        sodium_mCircleView.setInnerContourColor(getResources().getColor(R.color.sodium));
        sodium_mCircleView.setInnerContourSize(0);
        sodium_mCircleView.setOuterContourSize(0);
        sodium_mCircleView.setRimWidth(100);
        sodium_mCircleView.setBarWidth(60);

        // gradient color
        sodium_mCircleView.setTextColorAuto(true); //previous set values are ignored

        sodium_mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                sodium_mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                sodium_mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                sodium_mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                sodium_mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    public void ph_mCircleView_set() {

        ph_mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d("IndexActivity:", "Progress Changed: " + value);
            }
        });

        //value setting
        ph_mCircleView.setMaxValue(100);
        ph_mCircleView.setValue(0);
        ph_mCircleView.setValueAnimated(75f);

        // growing/rotating counter-clockwise
        ph_mCircleView.setDirection(Direction.CCW);

        //show unit
        ph_mCircleView.setUnit("*10^-1 mol/L");
        ph_mCircleView.setUnitVisible(true);

        ph_mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        ph_mCircleView.setRimColor(getResources().getColor(R.color.ph));
        ph_mCircleView.setBarColor(getResources().getColor(R.color.ph));
        ph_mCircleView.setInnerContourColor(getResources().getColor(R.color.ph));
        ph_mCircleView.setInnerContourSize(0);
        ph_mCircleView.setOuterContourSize(0);
        ph_mCircleView.setRimWidth(100);
        ph_mCircleView.setBarWidth(60);

        // gradient color
        ph_mCircleView.setTextColorAuto(true); //previous set values are ignored

        ph_mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                ph_mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                ph_mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                ph_mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                ph_mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    public void lactate_mCircleView_set() {

        lactate_mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d("IndexActivity:", "Progress Changed: " + value);
            }
        });

        //value setting
        lactate_mCircleView.setMaxValue(100);
        lactate_mCircleView.setValue(0);
        lactate_mCircleView.setValueAnimated(75f);

        // growing/rotating counter-clockwise
        lactate_mCircleView.setDirection(Direction.CCW);

        //show unit
        lactate_mCircleView.setUnit("*10^-1 mol/L");
        lactate_mCircleView.setUnitVisible(true);

        lactate_mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        lactate_mCircleView.setRimColor(getResources().getColor(R.color.lactate));
        lactate_mCircleView.setBarColor(getResources().getColor(R.color.lactate));
        lactate_mCircleView.setInnerContourColor(getResources().getColor(R.color.lactate));
        lactate_mCircleView.setInnerContourSize(0);
        lactate_mCircleView.setOuterContourSize(0);
        lactate_mCircleView.setRimWidth(100);
        lactate_mCircleView.setBarWidth(60);

        // gradient color
        lactate_mCircleView.setTextColorAuto(true); //previous set values are ignored

        lactate_mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                lactate_mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                lactate_mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                lactate_mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                lactate_mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    public void calcium_mCircleView_set() {

        calcium_mCircleView.setOnProgressChangedListener(new CircleProgressView.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(float value) {
                Log.d("IndexActivity:", "Progress Changed: " + value);
            }
        });

        //value setting
        calcium_mCircleView.setMaxValue(100);
        calcium_mCircleView.setValue(0);
        calcium_mCircleView.setValueAnimated(75f);

        // growing/rotating counter-clockwise
        calcium_mCircleView.setDirection(Direction.CCW);

        //show unit
        calcium_mCircleView.setUnit("*10^-1 mol/L");
        calcium_mCircleView.setUnitVisible(true);

        calcium_mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
        calcium_mCircleView.setRimColor(getResources().getColor(R.color.calcium));
        calcium_mCircleView.setBarColor(getResources().getColor(R.color.calcium));
        calcium_mCircleView.setInnerContourColor(getResources().getColor(R.color.calcium));
        calcium_mCircleView.setInnerContourSize(0);
        calcium_mCircleView.setOuterContourSize(0);
        calcium_mCircleView.setRimWidth(100);
        calcium_mCircleView.setBarWidth(60);

        // gradient color
        calcium_mCircleView.setTextColorAuto(true); //previous set values are ignored

        calcium_mCircleView.setOnAnimationStateChangedListener(
                new AnimationStateChangedListener() {
                    @Override
                    public void onAnimationStateChanged(AnimationState _animationState) {
                        switch (_animationState) {
                            case IDLE:
                            case ANIMATING:
                            case START_ANIMATING_AFTER_SPINNING:
                                calcium_mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                calcium_mCircleView.setUnitVisible(true);
                                break;
                            case SPINNING:
                                calcium_mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                calcium_mCircleView.setUnitVisible(false);
                            case END_SPINNING:
                                break;
                            case END_SPINNING_START_ANIMATING:
                                break;

                        }
                    }
                }
        );
    }

    // presently simulating the process of BLE communication
    private void element_thread_initializer() {
        new Thread(new thread_glucose()).start();
        new Thread(new thread_sodium()).start();
        new Thread(new thread_potassium()).start();
        new Thread(new thread_lactate()).start();
        new Thread(new thread_ph()).start();
    }

    // glucose-thread
    public class thread_glucose implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    glucose_mCircleView.setValue(random_index);
                    Thread.sleep(2000);     // sleep 1000ms
                    Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }

    }

    // sodium-thread
    public class thread_sodium implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    sodium_mCircleView.setValue(random_index);
                    Thread.sleep(2000);     // sleep 1000ms
                    Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }

    }

    // thread-potassium
    public class thread_potassium implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    potassium_mCircleView.setValue(random_index);
                    Thread.sleep(2000);     // sleep 1000ms
                    Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }
    }

    // thread-pH
    public class thread_ph implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    ph_mCircleView.setValue(random_index);
                    Thread.sleep(2000);     // sleep 1000ms
                    Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }
    }

    // thread-lactate
    public class thread_lactate implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    lactate_mCircleView.setValue(random_index);
                    Thread.sleep(2000);     // sleep 1000ms
                    Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }
    }

    // thread-calcium
    public class thread_calcium implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    calcium_mCircleView.setValue(random_index);
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




