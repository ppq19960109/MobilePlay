package com.mobileplay.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileplay.R;
import com.mobileplay.base.BasePager;
import com.mobileplay.doamain.MediaItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class VideoPager extends BasePager {

    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_load;
    private ArrayList<MediaItem> mediaItems = new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if(mediaItems!=null&&mediaItems.size()>0){

                        List<String> listdata = new ArrayList<String>();
                        listdata.add("上海");
                        listdata.add("北京");
                        listdata.add("天津");
                        listdata.add(mediaItems.get(0).getName());
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,listdata);//listdata和str均可
                        listview.setAdapter(arrayAdapter);
                    }else {
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
    class VideoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
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
            return null;
        }
    }
}
