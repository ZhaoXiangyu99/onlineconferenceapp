package com.dds.core.page;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dds.webrtc.R;

public class WelcomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
    }
    public void toLogin(View view){
        Intent intent = new Intent(WelcomePage.this, Login.class);
        startActivity(intent);
    }

    public void toRegister(View view){
        Intent intent = new Intent(WelcomePage.this, Register.class);
        startActivity(intent);
    }
}