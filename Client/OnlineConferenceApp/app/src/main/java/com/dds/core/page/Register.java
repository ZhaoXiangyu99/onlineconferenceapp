package com.dds.core.page;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dds.core.consts.Urls;
import com.dds.webrtc.R;
import com.dds.core.consts.showMsg;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


import okhttp3.Call;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class Register extends AppCompatActivity {

    private EditText et_username_register;
    private EditText et_pwd_register;
    private Button register;
    private final String TAG = "Register";
    private static boolean isDuplicate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    public void getViews(){
        et_username_register = findViewById(R.id.et_username_register);
        et_pwd_register = findViewById(R.id.et_pwd_register);
        register = findViewById(R.id.btn_register);
    }

    public void initView(){
        getViews();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = et_username_register.getText().toString().trim();
                String password = et_pwd_register.getText().toString().trim();
                if(!(username.isEmpty())&&(!password.isEmpty())){
                    getSync(username,password);
                }else{
                    showMsg.showShort(getApplicationContext(), "信息输入不全，重新输入");
                }
            }
        });
    }


    public void getSync(String username,String password){
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = Urls.register() + "?username=" + username + "&password=" + password;
                Request request = new Request.Builder().url(url).get().build();
                Call call = client.newCall(request);
                Response response = null;
                try {
                    response = call.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response.isSuccessful()){
                    Looper.prepare();
                    String res = null;
                    try {
                        res = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, res);
                    if(res.equals("用户已存在")){
                        showMsg.showLong(getApplicationContext(), res + ",请重新输入");
                    }
                    if(res.equals("添加成功")){
                        showMsg.showShort(getApplicationContext(), "注册成功");
                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Register.this,Login.class);
                                startActivity(intent);
                            }
                        };
                        timer.schedule(timerTask, 2000);
                    }
                    if(res.equals("添加失败")){
                        showMsg.showShort(getApplicationContext(), "注册失败");
                    }
                    Looper.loop();
                }
            }
        }).start();
    }

}