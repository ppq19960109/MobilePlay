package com.mobileplay.pager;

import android.content.ContentResolver;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileplay.R;
import com.mobileplay.base.BasePager;
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
                        videoAdapter = new VideoAdapter();
                        listview.setAdapter(videoAdapter);
                    } else {
                        tv_nomedia.setVisibility(View.VISIBLE);
                    }
                    pb_load.setVisibility(View.GONE);
                    break;
            }
        }
    };

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
        new Thread() {
            @Override
            public void run() {
                super.run();
                Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContext().getContentResolver();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listview = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_load = view.findViewById(R.id.pb_load);
        initData();
    }

    class VideoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mediaItems.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            VideoHolder videoHolder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.adapt_video_pager, null);
                videoHolder = new VideoHolder();
                videoHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
                videoHolder.tv_name = convertView.findViewById(R.id.tv_name);
                videoHolder.tv_time = convertView.findViewById(R.id.tv_time);
                videoHolder.tv_size = convertView.findViewById(R.id.tv_size);
                convertView.setTag(videoHolder);
            } else {
                videoHolder = (VideoHolder) convertView.getTag();
            }
            MediaItem mediaItem = mediaItems.get(position);
            videoHolder.tv_name.setText(mediaItem.getName());
            videoHolder.tv_size.setText(Formatter.formatFileSize(getContext(), mediaItem.getSize()));

            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            videoHolder.tv_time.setText(formatter.format(mediaItem.getDuration()));
            return convertView;
        }
    }

    static class VideoHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}
