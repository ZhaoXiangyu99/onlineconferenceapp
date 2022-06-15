package com.dds.core.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dds.core.page.WelcomePage;
import com.dds.core.socket.SocketManager;
import com.dds.webrtc.R;


public class SettingFragment extends Fragment {

    private SettingViewModel notificationsViewModel;
    private Button btn_exit_login;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = new ViewModelProvider(requireActivity()).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        btn_exit_login = root.findViewById(R.id.exit);

        //退回到登录页面
        btn_exit_login.setOnClickListener(view -> {
            SocketManager.getInstance().unConnect();
        });
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }


}