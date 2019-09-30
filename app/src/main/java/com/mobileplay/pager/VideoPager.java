package com.mobileplay.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileplay.R;
import com.mobileplay.activity.SystemVideoPlayer;
import com.mobileplay.adapter.VideoAdapter;
import com.mobileplay.base.BasePager;
import com.mobileplay.common.CommonUtils;
import com.mobileplay.doamain.MediaItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VideoPager extends BasePager {

    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_load;
    private VideoAdapter videoAdapter;
    private ArrayList<MediaItem> mediaItems = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mediaItems != null && mediaItems.size() > 0) {
                        videoAdapter = new VideoAdapter(getContext(), mediaItems);
                        listview.setAdapter(videoAdapter);
                    } else {
                        tv_nomedia.setVisibility(View.VISIBLE);
                    }
                    pb_load.setVisibility(View.GONE);
                    break;
            }
        }
    };
    public VideoPager() {

    }
    public VideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {

        return null;
    }

    @Override
    public void initData() {
        super.initData();
        getDataFromLocal();
    }

    private void getDataFromLocal() {
        mediaItems.clear();
        new Thread() {
            @Override
            public void run() {
                super.run();
                Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver =getContext() .getContentResolver();
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.ARTIST,
                };
                Cursor query = contentResolver.query(videoUri, objs, null, null, null);
                if (query != null) {
                    while (query.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        mediaItems.add(mediaItem);
                        String name = query.getString(0);
                        mediaItem.setName(name);
                        long duration = query.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = query.getLong(2);
                        mediaItem.setSize(size);
                        String data = query.getString(3);
                        mediaItem.setData(data);
                        String artist = query.getString(4);
                        mediaItem.setArtist(artist);

                    }
                    query.close();
                }

                handler.sendEmptyMessage(1);
            }
        }.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_load = view.findViewById(R.id.pb_load);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaItem item = (MediaItem) parent.getItemAtPosition(position);
//                CommonUtils.showToastMsg(null,item.getData()+"="+Uri.parse(item.getData()));
                if (item != null) {
//                    Intent intent = new Intent(getContext(), SystemVideoPlayer.class);
//                    intent.setData(Uri.parse(item.getData()));
//                    getContext().startActivity(intent);/
                    Intent intent = new Intent(getContext(), SystemVideoPlayer.class);
                    intent.setData(Uri.parse(item.getData()));
                    getContext().startActivity(intent);
                }

            }

        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("TAG","fragment onConfigurationChanged");
    }
}
