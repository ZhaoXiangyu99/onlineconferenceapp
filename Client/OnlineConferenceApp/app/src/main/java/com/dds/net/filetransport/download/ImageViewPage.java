package com.dds.net.filetransport.download;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dds.core.consts.Urls;
import com.dds.webrtc.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageViewPage extends AppCompatActivity {

    private ImageView ivImg;
    private Button reverse;
    private Bitmap img;
    private ImageHandler imgHandler = new ImageHandler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view_page);
        initView();
    }

    public void initView(){
        ivImg = findViewById(R.id.img_display);
        reverse = findViewById(R.id.reverseImg);
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
        downloadImg(fileName);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivImg.setPivotX(ivImg.getWidth()/2);
                ivImg.setPivotY(ivImg.getWidth()/2);
                ivImg.setRotation(90);

            }
        });
    }

    private String getParam(String imageName){
        JSONObject jsObj = new JSONObject();
        try {
            jsObj.put("imageName", imageName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsObj.toString();
    }

    /**
     * 从服务器读取图片流数据，并转换为Bitmap格式
     */
    private Bitmap getImg(String imgName){
        Bitmap img = null;
        try {
            String url = Urls.download() + "?imageName=" + imgName;
            URL imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Content-Type","application/Json");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Type", "image/jpeg;charset=UTF-8");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();

            //输出流写参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            String param = getParam(imgName);
            dos.writeBytes(param);
            dos.flush();
            dos.close();

            int resultCode = conn.getResponseCode();

            if(HttpURLConnection.HTTP_OK == resultCode){
                InputStream is = conn.getInputStream();
                img = BitmapFactory.decodeStream(is);
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return img;
    }

    /**
     * 异步线程请求到的图片数据，利用Handler，在主线程中显示
     */
    class ImageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0:
                    img = (Bitmap)msg.obj;
                    if(img != null){
                        ivImg.setImageBitmap(img);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 异步从服务器加载图片数据
     */
    private void downloadImg(String imgName){
        new Thread( () -> {
            Bitmap img =  getImg(imgName);
            Message msg = imgHandler.obtainMessage();
            msg.what = 0;
            msg.obj = img;
            imgHandler.sendMessage(msg);
        }).start();
    }

}