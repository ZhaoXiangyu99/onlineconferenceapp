package com.dds.core.ui.file;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSON;
import com.dds.core.consts.Urls;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FileViewModel extends ViewModel {

    public MutableLiveData<List<FileInfo>> mList;
    public Thread thread;
    public static String TAG = "zxy";
    public FileViewModel() {
    }

    public MutableLiveData<List<FileInfo>> getFileList(){
        if(mList == null){
            mList = new MutableLiveData<>();
            loadFiles();
        }
        return  mList;
    }


    public void loadFiles(){
        thread = new Thread(() -> {

            String url = Urls.query();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).get().build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(TAG,"请求失败");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    List<FileInfo> fileInfos = JSON.parseArray(response.body().string(), FileInfo.class);
                    mList.postValue(fileInfos);
                }
            });
        });
        thread.start();
    }
}
