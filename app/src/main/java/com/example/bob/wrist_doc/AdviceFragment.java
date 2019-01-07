package com.example.bob.wrist_doc;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.bob.wrist_doc.Utils.ProgressRequestBody;
import com.example.bob.wrist_doc.Utils.UploadCallBacks;
import com.github.mikephil.charting.charts.LineChart;
import com.pusher.client.Pusher;
import java.io.File;
import Remote.IUploadAPI;
import Remote.RetrofitClient;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdviceFragment extends Fragment implements UploadCallBacks {
    private static final int REQUEST_PERMISSION = 1000;
    private static final int PICK_FILE_REQUEST = 1001;
    private View view;
    private Button upload_btn;
    private Button choose_file;
    private Uri selectFileUri;
    private ProgressDialog dialog;

    public static final String BASE_URL = "http://10.0.2.2/";
    private IUploadAPI mService;
    private IUploadAPI getAPIUpload() {
        return RetrofitClient.getClient(BASE_URL).create(IUploadAPI.class);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.advice, container, false);

        init();

        //Service
        mService = getAPIUpload();

        // check permission
        if(ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_PERMISSION);
        }

        return view;
    }

    private void init() {
        upload_btn = (Button) view.findViewById(R.id.upload);
        choose_file = (Button) view.findViewById(R.id.choose_file);

        choose_file.setOnClickListener(new View.OnClickListener() {     // View 类所继承的抽象方法（onClickListener）
            @Override
            public void onClick(View view) {
                choose_file();
            }
        });

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void uploadFile() {
        if(selectFileUri != null) {
            dialog = new ProgressDialog(getContext());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMessage("Uploading...");
            dialog.setIndeterminate(false);
            dialog.setMax(100);
            dialog.setCancelable(false);
            dialog.show();

            // 封装成File类型的object
            File file = com.ipaulpro.afilechooser.utils.FileUtils.getFile(getContext(), selectFileUri);
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
                                    Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
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
    }

    private void choose_file() {
        // import module
        // add project dependency
        Intent getContentIntent = Intent.createChooser(com.ipaulpro.afilechooser.utils.FileUtils.createGetContentIntent(), "Select a file");
        startActivityForResult(getContentIntent, PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                selectFileUri = data.getData();
                if(selectFileUri != null && !selectFileUri.getPath().isEmpty()) {
                    Toast.makeText(getContext(), "file_selected.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Error.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onProgressUpdate(int percentage) {
        dialog.setProgress(percentage);
    }
}
