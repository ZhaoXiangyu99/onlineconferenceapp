package com.dds.core.page;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dds.App;
import com.dds.core.MainActivity;
import com.dds.core.consts.Urls;
import com.dds.core.consts.showMsg;

import com.dds.core.socket.IUserState;
import com.dds.core.socket.SocketManager;
import com.dds.webrtc.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {
    private Button btn_login;
    private EditText et_username_login;
    private EditText et_pwd_login;
    private final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }
    public void getViews(){
        btn_login = findViewById(R.id.btn_loginpage);
        et_username_login = findViewById(R.id.et_username_login);
        et_pwd_login = findViewById(R.id.et_pwd_login);
    }
    public void initView(){
        getViews();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username_login.getText().toString().trim();
                String password = et_pwd_login.getText().toString().trim();
                if(!(username.isEmpty())&&(!password.isEmpty())){
                    postSync(username,password);
                }else {
                    showMsg.showLong(getApplicationContext(), "信息输入不全，重新输入");
                }
            }
        });
    }
    public void postSync(String username,String password){
        new Thread(new Runnable(){
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                String url = Urls.login();
                RequestBody body = new FormBody.Builder().add("username", username)
                        .add("password",password).build();
                Request request = new Request.Builder().url(url).post(body).build();
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
                    Log.i(TAG, res);
                    if(res.equals("登录成功")){
                        showMsg.showShort(getApplicationContext(), "登录成功");

                        // 设置用户名
                        App.getInstance().setUsername(username);
                        // 添加登录回调
                        SocketManager.getInstance().addUserStateCallback(new IUserState() {
                            @Override
                            public void userLogin() {
                                Timer timer = new Timer();
                                TimerTask timerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                };
                                timer.schedule(timerTask, 2000);
                            }
                            @Override
                            public void userLogout() {

                            }
                        });
                        // 连接socket
                        SocketManager.getInstance().connect(Urls.WS, username, 0);


                    }
                    if(res.equals("密码错误"))
                        showMsg.showShort(getApplicationContext(), "密码错误，请重新输入");
                    if(res.equals("没有此用户"))
                    {
                        showMsg.showShort(getApplicationContext(), "用户未注册,请注册");
                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Login.this,Register.class);
                                startActivity(intent);
                            }
                        };
                        timer.schedule(timerTask, 1500);
                    }
                    Looper.loop();
                }
            }
        }).start();
    }


}