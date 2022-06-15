package com.dds.net.filetransport.download;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.dds.core.ui.file.FileFragment;
import com.dds.core.ui.room.RoomFragment;
import com.dds.webrtc.R;

public class Filedownload extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filedownload);
        initFragment();
    }

    public void initFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.file_container, new FileFragment())
                .commit();
    }

}