package com.dds.core.voip;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dds.core.base.BaseActivity;
import com.dds.net.filetransport.download.Filedownload;
import com.dds.net.filetransport.upload.UploadActivity;
import com.dds.permission.Permissions;
import com.dds.skywebrtc.CallSession;
import com.dds.skywebrtc.EnumType;
import com.dds.skywebrtc.SkyEngineKit;
import com.dds.skywebrtc.except.NotInitializedException;
import com.dds.webrtc.R;

/**
 * 多人通话界面
 */
public class CallMultiActivity extends BaseActivity implements CallSession.CallSessionCallback, View.OnClickListener {
    private SkyEngineKit gEngineKit;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView meetingHangupImageView;
    private CallSession.CallSessionCallback currentFragment;
    public static final String EXTRA_MO = "isOutGoing";
    private boolean isOutgoing;
    private TextView tv_roomID;
    private ImageView muteImageView;
    private boolean micEnabled = false;  //设置静音
    private ImageView switchCameraImageView;
    private ImageView speakerImageView;
    private boolean isCameraChanged = false;//调整前后置摄像头,默认是前置
    private boolean isSpeakerOn = false; //设置扬声器,默认关闭
    private ImageView file_uploadImageView;
    private ImageView file_downloadImageView;

    public static void openActivity(Activity activity, String room, boolean isOutgoing) {
        Intent intent = new Intent(activity, CallMultiActivity.class);
        intent.putExtra("room", room);
        intent.putExtra(EXTRA_MO, isOutgoing);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.activity_multi_call);
        initView();
        initListener();
        initData();
    }


    private void initView() {
        meetingHangupImageView = findViewById(R.id.meetingHangupImageView);
        muteImageView = findViewById(R.id.muteImageView);
        switchCameraImageView = findViewById(R.id.switchCameraImageView);
        speakerImageView = findViewById(R.id.speakerImageView);
        file_downloadImageView = findViewById(R.id.file_downloadImageView);
        file_uploadImageView = findViewById(R.id.file_uploadImageView);

        Fragment fragment = new FragmentMeeting();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.meeting_container, fragment)
                .commit();
        currentFragment = (CallSession.CallSessionCallback) fragment;
        tv_roomID = findViewById(R.id.tv_roomID);


    }

    private void initListener() {
        meetingHangupImageView.setOnClickListener(this);
        muteImageView.setOnClickListener(this);
        switchCameraImageView.setOnClickListener(this);
        speakerImageView.setOnClickListener(this);
        file_uploadImageView.setOnClickListener(this);
        file_downloadImageView.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        String room = intent.getStringExtra("room");
        tv_roomID.setText(room);
        isOutgoing = intent.getBooleanExtra(EXTRA_MO, false);
        try {
            gEngineKit = SkyEngineKit.Instance();
        } catch (NotInitializedException e) {
            finish();
        }
        String[] per = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        Permissions.request(this, per, integer -> {
            if (integer == 0) {
                // 权限同意
                init(room, isOutgoing);
            } else {
                // 权限拒绝
                CallMultiActivity.this.finish();
            }
        });

    }

    private void init(String room, boolean isOutgoing) {
        SkyEngineKit.init(new VoipEvent());
        if (isOutgoing) {
            // 创建一个房间并进入
            gEngineKit.createAndJoinRoom(this,room);
        } else {
            // 加入房间
            gEngineKit.joinRoom(this, room);
        }
        CallSession session = gEngineKit.getCurrentSession();
        if (session == null) {
            this.finish();
        } else {
            session.setSessionCallback(this);

        }
    }


    public SkyEngineKit getEngineKit() {
        return gEngineKit;
    }


    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }


    //-------------------------------------------------回调相关------------------------------------
    @Override
    public void didCallEndWithReason(EnumType.CallEndReason var1) {
        finish();
    }

    @Override
    public void didChangeState(EnumType.CallState callState) {
        handler.post(() -> currentFragment.didChangeState(callState));
    }

    @Override
    public void didChangeMode(boolean var1) {
        handler.post(() -> currentFragment.didChangeMode(var1));
    }

    @Override
    public void didCreateLocalVideoTrack() {
        handler.post(() -> currentFragment.didCreateLocalVideoTrack());
    }

    @Override
    public void didReceiveRemoteVideoTrack(String userId) {
        handler.post(() -> currentFragment.didReceiveRemoteVideoTrack(userId));
    }

    @Override
    public void didUserLeave(String userId) {
        handler.post(() -> currentFragment.didUserLeave(userId));
    }

    @Override
    public void didError(String var1) {
        finish();
    }

    @Override
    public void didDisconnected(String userId) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.meetingHangupImageView:  //挂断通话
                handleHangup();
                break;
            case R.id.muteImageView:           //设置静音
                setMute();
                break;
            case R.id.switchCameraImageView:   //切换前置摄像头
                changeCamera();
                break;
            case R.id.speakerImageView:        //打开扬声器
                speakerOn();
                break;
            case R.id.file_uploadImageView:    //文件上传
                file_upload();
                break;
            case R.id.file_downloadImageView:  //文件下载
                file_download();
                break;
        }
    }

    // 处理挂断事件
    private void handleHangup() {
        SkyEngineKit.Instance().leaveRoom();
        this.finish();
    }

    //设置静音
    private void setMute(){
        CallSession session = SkyEngineKit.Instance().getCurrentSession();
        if (session != null && session.getState() != EnumType.CallState.Outgoing) {
            if (session.toggleMuteAudio(!micEnabled)) {
                micEnabled = !micEnabled;
            }
            muteImageView.setSelected(micEnabled);
        }
    }

    //调整前后摄像头
    private void changeCamera(){
        CallSession session = SkyEngineKit.Instance().getCurrentSession();
        if(session != null && session.getState() != EnumType.CallState.Outgoing){
            session.switchCamera();
            switchCameraImageView.setSelected(!isCameraChanged);
        }
    }

    //打开扬声器
    private void speakerOn(){
        CallSession session = SkyEngineKit.Instance().getCurrentSession();
        if (session != null && session.getState() != EnumType.CallState.Outgoing) {
            if (session.toggleSpeaker(!isSpeakerOn)) {
                isSpeakerOn = !isSpeakerOn;
            }
            speakerImageView.setSelected(isSpeakerOn);
        }
    }

    //下载文件
    private void file_download(){
        Intent intent = new Intent(CallMultiActivity.this, Filedownload.class);
        startActivity(intent);
    }

    //上传文件
    private void file_upload(){
        Intent intent = new Intent(CallMultiActivity.this, UploadActivity.class);
        startActivity(intent);
    }
}
