package com.example.bob.wrist_doc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import Remote.IUploadAPI;
import Remote.RetrofitClient;
import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.Direction;
import at.grabner.circleprogress.TextMode;
import at.grabner.circleprogress.UnitPosition;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.alespero.expandablecardview.ExpandableCardView;
import com.example.bob.wrist_doc.Utils.ProgressRequestBody;
import com.example.bob.wrist_doc.Utils.UploadCallBacks;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static android.content.Context.MODE_APPEND;

public class IndexFragment extends Fragment implements UploadCallBacks {
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
    private LineChart chart_glucose;
    private LineChart chart_potassium;
    private LineChart chart_ph;
    private LineChart chart_lactate;
    private LineChart chart_sodium;
    private LineChart chart_calcium;

    private ProgressDialog dialog;
    private static final int REQUEST_PERMISSION = 1000;
    private static final int PICK_FILE_REQUEST = 1001;
    public static final String BASE_URL = "http://10.0.2.2/";
    private IUploadAPI mService;
    private IUploadAPI getAPIUpload() {
        return RetrofitClient.getClient(BASE_URL).create(IUploadAPI.class);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.index, container, false);
        init();

        //Service
        mService = getAPIUpload();

        // check permission
        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }

        element_thread_initializer();

        return view;
    }

    private void uploadFile(String file_name) {
            dialog = new ProgressDialog(getContext());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(false);

            // 封装成File类型的object
            //File file = com.ipaulpro.afilechooser.utils.FileUtils.getFile(getContext(), selectFileUri);
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+file_name);
            // 封装成请求报文
            ProgressRequestBody requestFile = new ProgressRequestBody(file, this);
            // 报文添加文件描述(来自endpoint-php中的 $name)
            final MultipartBody.Part body = MultipartBody.Part.createFormData("uploaded_file", file.getName(), requestFile);

            // 此处Runnable为interface（抽象）
            // 亦可封装成内部类
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mService.uploadFile(body)
                            .enqueue(new Callback<String>() {   // Asynchronously send the request and notify {@code callback} of its response
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    dialog.dismiss();
                                    //Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }).start();
        }

    // initialize all components of index
    public void init() {
        // element card indicator
        element_card_para();
        chart_setup();
        // setting up parameters of circle progress bar
        circle_progress_para();
    }

    private void chart_setup() {
        chart_glucose = (LineChart) view.findViewById(R.id.chart_glucose);
        chart_potassium = (LineChart) view.findViewById(R.id.chart_potassium);
        chart_ph = (LineChart) view.findViewById(R.id.chart_ph);
        chart_lactate = (LineChart) view.findViewById(R.id.chart_lactate);
        chart_calcium = (LineChart) view.findViewById(R.id.chart_calcium);
        chart_sodium = (LineChart) view.findViewById(R.id.chart_sodium);

        // real-time chart config

        // chart_glucose config
        setupChart(chart_glucose);
        setupAxes(chart_glucose);
        setupData(chart_glucose);
        setupLegend(chart_glucose);

        // chart_potassium config
        setupChart(chart_potassium);
        setupAxes(chart_potassium);
        setupData(chart_potassium);
        setupLegend(chart_potassium);

        // chart_sodium config
        setupChart(chart_sodium);
        setupAxes(chart_sodium);
        setupData(chart_sodium);
        setupLegend(chart_sodium);

        // chart_lactate config
        setupChart(chart_lactate);
        setupAxes(chart_lactate);
        setupData(chart_lactate);
        setupLegend(chart_lactate);

        // chart_ph config
        setupChart(chart_ph);
        setupAxes(chart_ph);
        setupData(chart_ph);
        setupLegend(chart_ph);

        // chart_calcium config
        setupChart(chart_calcium);
        setupAxes(chart_calcium);
        setupData(chart_calcium);
        setupLegend(chart_calcium);
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

    public void basicReadWrite() {
        // [START write_message]
        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        myRef.setValue("Hello, World!");
        // [END write_message]

        // [START read_message]
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("IndexFragment", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("IndexFragment", "Failed to read value.", error.toException());
            }
        });
        // [END read_message]
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
        new Thread(new thread_calcium()).start();
    }

    @Override
    public void onProgressUpdate(int percentage) {
        dialog.setProgress(percentage);
    }

    // glucose-thread
    public class thread_glucose implements Runnable {
        int i = 0;
        int j = 0;
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    glucose_mCircleView.setValue(random_index);
                    addEntry(chart_glucose, "glucose", i, random_index);
                    index_append("/glucose_index.txt", random_index);
                    i++;
                    j++;
                    Thread.sleep(1000);     // sleep 1000ms
                    Message message = new Message();
                    message.what = 1;
                    if(j == 10) {
                        handler.sendMessage(message);
                        j = 0;
                    }
                } catch (Exception e) {
                }
            }
        }

    }

    // sodium-thread
    public class thread_sodium implements Runnable {
        // 方法内变量不能定义 OR 初始化
        int i = 0;
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    sodium_mCircleView.setValue(random_index);
                    addEntry(chart_sodium, "sodium", i, random_index);
                    index_append("/sodium_index.txt", random_index);
                    i++;
                    Thread.sleep(1000);     // sleep 1000ms
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
        // 方法内变量不能定义 OR 初始化
        int i = 0;
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    potassium_mCircleView.setValue(random_index);
                    addEntry(chart_potassium, "potassium", i, random_index);
                    index_append("/potassium_index.txt", random_index);
                    i++;
                    Thread.sleep(1000);     // sleep 1000ms
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
        // 方法内变量不能定义 OR 初始化
        int i = 0;
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    ph_mCircleView.setValue(random_index);
                    addEntry(chart_ph, "pH", i, random_index);
                    index_append("/ph_index.txt", random_index);
                    i++;
                    Thread.sleep(1000);     // sleep 1000ms
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
        // 方法内变量不能定义 OR 初始化
        int i = 0;
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    lactate_mCircleView.setValue(random_index);
                    addEntry(chart_lactate, "lactate", i, random_index);
                    index_append("/lactate_index.txt", random_index);
                    i++;
                    Thread.sleep(1000);     // sleep 1000ms
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
        // 方法内变量不能定义 OR 初始化
        int i = 0;
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                try {
                    // generate random index from 0 to 100
                    float random_index = (float) (Math.random()*99 + 1);
                    calcium_mCircleView.setValue(random_index);
                    addEntry(chart_calcium, "calcium", i, random_index);
                    index_append("/calcium_index.txt", random_index);
                    i++;
                    Thread.sleep(1000);     // sleep 1000ms
                    Message message = new Message();
//                message.what = 1;
//                handler.sendMessage(message);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     *  upload 6 index file after 10 seconds
     * */
//    public class thread_upload implements Runnable {
//        @Override
//        public void run() {
//            while(true) {
//                try {
//                    Thread.sleep(10000);
//                    uploadFile("glucose_index.txt");
//                    uploadFile("sodium_index.txt");
//                    uploadFile("potassium_index.txt");
//                    uploadFile("lactate_index.txt");
//                    uploadFile("ph_index.txt");
//                    uploadFile("calcium_index.txt");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private void setupChart(LineChart lineChart) {
        // enable descriptionn text
        lineChart.getDescription().setEnabled(false);
        // enable touch gestures
        lineChart.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
        // enable scaling
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);
    }

    private void setupAxes(LineChart lineChart) {
        // setup x-axis
        XAxis x1 = lineChart.getXAxis();
        x1.setTextColor(Color.DKGRAY);
        x1.setDrawGridLines(false);
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(true);

        // setup y-axis(left)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.DKGRAY);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        // setup y-axis(right)
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);

        // limit lines are drawn underneath data
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    private void setupData(LineChart lineChart) {
        LineData data = new LineData();
        data.setValueTextColor(Color.DKGRAY);
        data.setValueTextColor(Color.DKGRAY);

        // add empty data
        lineChart.setData(data);
    }

    private void setupLegend(LineChart lineChart) {
        // get the legend (only possible after setting data)
        Legend lg = lineChart.getLegend();

        //modify the legend
        lg.setForm(Legend.LegendForm.CIRCLE);
        lg.setTextColor(Color.DKGRAY);
    }

    // if no data-entry receive, use createSet() as the default method
    private LineDataSet createSet(String set_name) {
        LineDataSet set = new LineDataSet(null, set_name);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
//        set.setColors(ColorTemplate.VORDIPLOM_COLORS[3]);
        set.setColors(Color.RED);
        set.setCircleColor(Color.DKGRAY);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setValueTextColor(Color.DKGRAY);
        set.setValueTextSize(10f);
        // To show values of each point
        set.setDrawValues(true);

        return set;
    }

    private void addEntry(LineChart lineChart, String set_name, int timing, float input_variable) {
        LineData data = lineChart.getData();

        if(data != null) {
            ILineDataSet set = data.getDataSetByIndex(0);

            if(set == null) {
                set = createSet(set_name);
                data.addDataSet(set);
            }

            data.addEntry(new Entry(timing, input_variable), 0);

            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();

            // limit the number of visible entries
            lineChart.setVisibleXRangeMaximum(10);

            // move to the latest entry
            lineChart.moveViewToX(data.getEntryCount());
        }
    }

    private void index_append(String element, float index) {
        File element_data = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+element);
        try {
            FileOutputStream fos = new FileOutputStream(element_data, true);
            // float2binary
            fos.write(Float.floatToIntBits(index));
            fos.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what) {
                case 1:
                    uploadFile("glucose_index.txt");
                    uploadFile("sodium_index.txt");
                    uploadFile("potassium_index.txt");
                    uploadFile("lactate_index.txt");
                    uploadFile("ph_index.txt");
                    uploadFile("calcium_index.txt");
                    break;

                 default:

                     break;

            }
        }
    };
}




