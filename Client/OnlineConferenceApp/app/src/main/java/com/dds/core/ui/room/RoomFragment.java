package com.dds.core.ui.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dds.App;
import com.dds.core.voip.CallMultiActivity;
import com.dds.webrtc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class RoomFragment extends Fragment {

    private RoomViewModel roomViewModel;
    private RecyclerView list;
    private List<RoomInfo> datas;
    private RoomAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private TextView no_data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        View root = inflater.inflate(R.layout.fragment_room, container, false);
        initView(root);
        initData();

        return root;
    }


    private void initView(View root) {
        setHasOptionsMenu(true);
        list = root.findViewById(R.id.list);
        refreshLayout = root.findViewById(R.id.swipe);
        no_data = root.findViewById(R.id.no_data);
    }

    private void initData() {
        adapter = new RoomAdapter();
        datas = new ArrayList<>();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        roomViewModel.getRoomList().observe(getViewLifecycleOwner(), roomInfos -> {
            if (roomInfos.size() > 0) {
                no_data.setVisibility(View.GONE);
            } else {
                no_data.setVisibility(View.VISIBLE);
            }
            datas.clear();
            //??????????????????????????????????????????
            datas.addAll(roomInfos);
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);
        });

        refreshLayout.setOnRefreshListener(() -> {
            roomViewModel.loadRooms();
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_room, menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //???????????????????????????????????????
        if (item.getItemId() == R.id.action_create) {
            createRoom();
            return true;
        }
        //???????????????
        if(item.getItemId() == R.id.search_by_roomID){
            joinRoom();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ????????????
    private void createRoom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("???????????????????????????????????????");
        builder.setPositiveButton("??????", (dialog, which) -> {
            // ???????????????????????????

            //????????????9???????????????
            String room = "";
            Random random  = new Random();
            for(int i = 0;i<9;i++){
                room += random.nextInt(10);
            }
            RoomInfo info = new RoomInfo();
            info.setRoomId(room);
            info.setCurrentSize(1);
            info.setMaxSize(9);
            info.setUserId(App.getInstance().getUsername());
            datas.add(info);
            initData();

            CallMultiActivity.openActivity(getActivity(),
                    room, true);

        }).setNegativeButton("??????", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void joinRoom(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("???????????????");
        EditText editText = new EditText(getContext());
        builder.setView(editText);
        builder.setCancelable(false);
        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String room = editText.getText().toString().trim();
                boolean flag = false;
                for(int pos = 0;pos <datas.size();pos++){
                    if(datas.get(pos).getRoomId().equals(room)){
                        flag = true;
                        Toast.makeText(getContext(), "???????????????" + room + " ????????????", Toast.LENGTH_SHORT).show();

                        Timer timer = new Timer();
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {
                                CallMultiActivity.openActivity(getActivity(), room, false);

                            }
                        };
                        timer.schedule(timerTask, 2000);
                        break;
                    }
                }
                if(flag == false){
                    Toast.makeText(getContext(), "??????????????????,???????????????????????????????????????", Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("???", (dialog, which) -> dialog.dismiss() );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class RoomAdapter extends RecyclerView.Adapter<RoomFragment.Holder> {

        @NonNull
        @Override
        public RoomFragment.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_rooms, parent, false);
            return new RoomFragment.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomFragment.Holder holder, int position) {
            RoomInfo roomInfo = datas.get(position);
            holder.text.setText(roomInfo.getRoomId());
            //????????????
            holder.item_join_room.setOnClickListener(v -> {
                CallMultiActivity.openActivity(getActivity(), roomInfo.getRoomId(), false);
            });
        }


        @Override
        public int getItemCount() {
            return datas.size();
        }


    }

    private static class Holder extends RecyclerView.ViewHolder {

        private final TextView text;
        private final Button item_join_room;

        Holder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.item_user_name);
            item_join_room = itemView.findViewById(R.id.item_join_room);

        }
    }

}