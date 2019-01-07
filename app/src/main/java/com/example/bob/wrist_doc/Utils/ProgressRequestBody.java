package com.example.bob.wrist_doc.Utils;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
/**
 * 文件读取写入BufferSink AND 进度条更新类
 * */
public class ProgressRequestBody extends RequestBody {

    private File file;
    private UploadCallBacks listener;
    private static final int DEFAULT_BUFFER_SIZE = 4096;

    // ProgressRequestBody constructor
    public ProgressRequestBody(File file, UploadCallBacks listener) {
        this.file = file;
        this.listener = listener;
    }

    @Nullable
    @Override
    // 重写抽象的文件类型返回类型方法
    public MediaType contentType() {
        return MediaType.parse("text/*");
    }

    @Override
    // 重写文件长度返回类型方法
    public long contentLength() throws IOException {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = file.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(file);
        long uploaded = 0;

        try{
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            // 逐次4kb读入
            while((read = in.read(buffer)) != -1) {
                // 后台线程事件分发至完成（更新进度条）
                handler.post(new ProgressUpdater(uploaded, fileLength));
                uploaded+=read;
                /**
                 * Removes all bytes from {@code source} and appends them to this sink. Returns the
                 * number of bytes read which will be 0 if {@code source} is exhausted.
                 */
                sink.write(buffer, 0, read);
            }
        }finally {
            in.close();
        }
    }


    // 上传进度显示控件
    private class ProgressUpdater implements Runnable {
        private long uploaded;
        private long fileLength;

        // ProgressUpdater constructor
        public ProgressUpdater(long uploaded, long fileLength) {
            this.fileLength = fileLength;
            this.uploaded = uploaded;
        }

        @Override
        public void run() {
            listener.onProgressUpdate((int)(100*uploaded/fileLength));
        }
    }
}
