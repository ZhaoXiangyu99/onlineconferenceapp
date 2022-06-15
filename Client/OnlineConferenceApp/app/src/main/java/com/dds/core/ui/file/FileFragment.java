package com.dds.core.ui.file;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dds.net.filetransport.download.ImageViewPage;
import com.dds.webrtc.R;

import java.util.ArrayList;
import java.util.List;

public class FileFragment extends Fragment {
    private FileViewModel fileViewModel;
    private RecyclerView list;
    private List<FileInfo> datas;
    private FileAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    private TextView no_data;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fileViewModel = new ViewModelProvider(requireActivity()).get(FileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_file, container,false);
        initView(root);
        initData();
        return root;
    }

    public void initView(View root){
        setHasOptionsMenu(true);
        list = root.findViewById(R.id.list);
        refreshLayout = root.findViewById(R.id.swipe);
        no_data = root.findViewById(R.id.no_data);
    }

    public void initData(){
        adapter = new FileFragment.FileAdapter();
        datas = new ArrayList<>();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getContext()));


        fileViewModel.getFileList().observe(getViewLifecycleOwner(), fileInfos -> {
            if(fileInfos.size() > 0){
                no_data.setVisibility(View.GONE);
            }else{
                no_data.setVisibility(View.VISIBLE);
            }
            datas.clear();
            //将服务器上的所有文件加入
            datas.addAll(fileInfos);
            adapter.notifyDataSetChanged();
            refreshLayout.setRefreshing(false);

            refreshLayout.setOnRefreshListener(() -> {
                fileViewModel.loadFiles();
            });
        });
    }

    private class FileAdapter extends RecyclerView.Adapter<FileFragment.Holder>{

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_files, parent,false);
            return new FileFragment.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            FileInfo fileInfo = datas.get(position);
            holder.textView.setText(fileInfo.getFile_name());
            //开始下载文件
            holder.item_file_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String fileName = fileInfo.getFile_name();
                    Intent intent = new Intent(getActivity(),ImageViewPage.class);
                    intent.putExtra("fileName", fileName);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }


    private static class Holder extends RecyclerView.ViewHolder{
        private final TextView textView;
        private final Button item_file_download;
        Holder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.item_file_name);
            item_file_download = itemView.findViewById(R.id.item_file_download);
        }
    }
}