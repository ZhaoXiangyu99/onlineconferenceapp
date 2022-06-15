package com.dds.net.filetransport.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.dds.core.consts.Urls;
import com.dds.webrtc.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity {

    // 动态请求权限
    private final static int REQUEST_PERMISSION_CODE = 1000;
    // 相册
    private final static int REQUEST_GALLERY = 100;

    // 图片最大数量
    private final static int DEFAULT_NUM = 4;

    private ActivityViewHolder viewHolder;
    private List<ItemBean> list;
    private LoadImageAdapter adapter;
    private ItemBean addImgButton = new ItemBean(null, true);


    private MyHandler myHandler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        viewHolder = new ActivityViewHolder(getWindow());
        init();

        // 对于api23或以上,动态请求权限
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                    || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                String sdpath = Environment.getExternalStorageDirectory() + "/tmp";
                File file = new File(sdpath);
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
            }
        }
    }

    private void init() {
        list = new ArrayList<>();
        list.add(addImgButton);
        viewHolder.mRV.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new LoadImageAdapter(list);
        myHandler = new MyHandler(this,adapter);
        adapter.setOnImageItemClickListener(new LoadImageAdapter.OnImageItemClickListener() {
            @Override
            public void onClick(View view, ItemBean itemBean, int position) {
                if (itemBean.isButton()) {
                    // 按钮事件，则打开图片选择器，添加图片
                    selectImage();
                } else {
                    // 图片事件，则预览图片

                    previewPhoto(itemBean.getImageFileBean());
                }
            }

            @Override
            public void onDelete(View view, ItemBean itemBean, int position) {

                list.remove(addImgButton);
                list.remove(itemBean);
                list.add(addImgButton);
                adapter.notifyDataSetChanged();

            }
        });
        viewHolder.mRV.setAdapter(adapter);
        viewHolder.mRV.addItemDecoration(new GridDividerItemDecoration(this, 15, true));

    }

    private void previewPhoto(ImageFileBean fileBean) {
        // TODO 添加预览图片功能
    }

    /**
     * 选择图片
     */
    private void selectImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT); // 系统默认的图片选择程序
        galleryIntent.setType("*/*");
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GALLERY:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    saveUriToFile(uri,REQUEST_GALLERY);
                }

                break;
        }
    }

    /**
     * 将Uri图片类型转换成File，BitMap类型
     * 在界面上显示BitMap图片，以防止内存溢出
     * 上传可选择File文件上传
     *
     * @param uri
     */
    private void saveUriToFile(Uri uri,int from) {
        Bitmap bitmap = null;
        if (uri != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2; // 图片宽高都为原来的二分之一，即图片为原来的四分之一
                bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri), null, options);
                File file = null;
                switch (from){
                    case REQUEST_GALLERY:
                        String filePath = FileUtils.getRealFilePath(this,uri);
                        File oldFile = new File(filePath);
                        // 修改文件名
                        //String newFileName = UUID.randomUUID().toString().replace("-","")+".jpg";
                        //生成一个6位数的图片名
                        String newFileName = "";
                        Random random = new Random();
                        for(int i = 0;i<6;i++){
                            newFileName += random.nextInt(10);
                        }
                        newFileName += ".jpg";
                        String newFilePath = oldFile.getParent()+"/"+newFileName;
                        file = new File(newFilePath);
                        oldFile.renameTo(file);

                        break;
                }

                if(file == null || !file.exists()){
                    Log.e("异常：","文件不存在！");
                }
                list.remove(addImgButton); // 先删除
                if (list.size() < DEFAULT_NUM) {
                    ItemBean itemBean = new ItemBean(new ImageFileBean(file, bitmap, false), false);
                    list.add(itemBean);
                    if (list.size() < DEFAULT_NUM) {
                        // 如果图片数量还没有达到最大值，则将添加按钮添加到list后面
                        list.add(addImgButton);
                    }
                }

                adapter.notifyDataSetChanged();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSION_CODE == requestCode) {
            // TODO 添加权限请求后的逻辑
        }
    }


    public void startUpload(View view) {

        for (ItemBean itemBean : list) {
            if (itemBean.isButton()) continue;
            uploadImage(itemBean.getImageFileBean());
            itemBean.getImageFileBean().setStartUpload(true);
            adapter.notifyDataSetChanged();
        }

    }


    private static class MyHandler extends Handler {
        private Context mContext;
        private LoadImageAdapter loadImageAdapter;
        public MyHandler(Context context,LoadImageAdapter loadImageAdapter) {
            this.mContext = context;
            this.loadImageAdapter = loadImageAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            List<ItemBean> itemBeanList = null;
            ImageFileBean fileBean = null;
            if(msg.obj instanceof List){
                itemBeanList = (List<ItemBean>)msg.obj;
                for(ItemBean itemBean:itemBeanList){
                    if(itemBean.isButton())continue;
                    itemBean.getImageFileBean().setStartUpload(false);
                }
            }else if(msg.obj instanceof ImageFileBean){
                fileBean= (ImageFileBean)msg.obj;
                fileBean.setStartUpload(false);
            }

            switch (msg.what) {
                case 0:
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    if(fileBean !=null) {
                        fileBean.setUpload(false);
                    }else if(itemBeanList != null){
                        for(ItemBean itemBean:itemBeanList){
                            if(itemBean.isButton())continue;
                            itemBean.getImageFileBean().setUpload(false);
                        }
                    }
                    break;

                case 2:
                    Toast.makeText(mContext,"获取服务端数据为空",Toast.LENGTH_SHORT).show();

                    break;

                case 3:
                    Toast.makeText(mContext, "上传成功！", Toast.LENGTH_SHORT).show();
                    if(fileBean !=null) {
                        fileBean.setUpload(true);
                    }else if(itemBeanList != null){
                        for(ItemBean itemBean:itemBeanList){
                            if(itemBean.isButton())continue;
                            itemBean.getImageFileBean().setUpload(true);
                        }
                    }

                    break;
            }
            loadImageAdapter.notifyDataSetChanged();

        }
    }

    private void uploadImage(final ImageFileBean fileBean) {
        File file = fileBean.getFile();
        if (file == null) return;
        if(!file.exists()){
            Toast.makeText(this, "文件不存在！", Toast.LENGTH_SHORT).show();
            return;
        }

        viewHolder.mTVText.setText(file.getName());
        // 准备Body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name",file.getName())

                .addFormDataPart("id","12,13,14")
                .addFormDataPart("type","2")
                .addFormDataPart("file", file.getName(),
                        RequestBody.Companion.create(file, MediaType.parse("multipart/form-data")))//文件
                .build();
        Request request = new Request.Builder()
                .url(Urls.upload()).post(requestBody)
                .addHeader("user-agent", "PDA")
                .addHeader("x-userid", "752332")// 添加x-userid请求头
                .addHeader("x-sessionkey", "kjhsfjkaskfashfuiwf")// 添加x-sessionkey请求头
                .addHeader("x-tonce", Long.valueOf(System.currentTimeMillis()).toString())// 添加x-tonce请求头
                .addHeader("x-timestamp", Long.valueOf(System.currentTimeMillis()).toString())// 添加x-timestamp请求头
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        final Message msg = myHandler.obtainMessage();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                msg.obj = fileBean;
                msg.what =0;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.i("上传图片结果：", result);
                msg.obj = fileBean;
                if (!response.isSuccessful()) {
                    Log.i("响应失败：", response.code() + "");
                    msg.what =1;
                    return;
                }
                msg.what = 3;
                myHandler.sendMessage(msg);

            }
        });
    }

    static class ActivityViewHolder {
        RecyclerView mRV;
        TextView mTVText;

        public ActivityViewHolder(Window window) {
            mRV = window.findViewById(R.id.rv_img);
            mTVText = window.findViewById(R.id.tv_text);
        }
    }
}