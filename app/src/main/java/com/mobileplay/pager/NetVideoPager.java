package com.mobileplay.pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobileplay.Interface.GetRequest_Interface;
import com.mobileplay.R;
import com.mobileplay.activity.SystemVideoPlayer;
import com.mobileplay.adapter.NetVideoAdapter;
import com.mobileplay.adapter.VideoAdapter;
import com.mobileplay.base.BasePager;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.doamain.Movie;
import com.mobileplay.doamain.Trailers;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetVideoPager extends BasePager {
    public static final String NET_VIDEO_URL = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_load;
    private Trailers mediaItems;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mediaItems != null && mediaItems.getTrailers().size() > 0) {
                        NetVideoAdapter netVideoAdapter = new NetVideoAdapter(getContext(), mediaItems.getTrailers());
                        listview.setAdapter(netVideoAdapter);
                    } else {
                        tv_nomedia.setVisibility(View.VISIBLE);
                    }
                    pb_load.setVisibility(View.GONE);
                    break;
            }
        }
    };
    public NetVideoPager() {

    }

    public NetVideoPager(Context context) {
        super(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie item = (Movie) parent.getItemAtPosition(position);
//                CommonUtils.showToastMsg(null,item.getData()+"="+Uri.parse(item.getData()));
                if (item != null) {
//                    Intent intent = new Intent(getContext(), SystemVideoPlayer.class);
//                    intent.setData(Uri.parse(item.getData()));
//                    getContext().startActivity(intent);/

                    Intent intent = new Intent(getContext(), SystemVideoPlayer.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("VideoList",  mediaItems.getTrailers());
                    intent.putExtras(bundle);
                    intent.putExtra("position",position);
                    getContext().startActivity(intent);
                }

            }

        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_net_video, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    public void initView(View view) {
        listview = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_load = view.findViewById(R.id.pb_load);

    }

    @Override
    public View initRootView() {

        return null;
    }

    @Override
    public void initData() {
        super.initData();

        getRetrofit();
    }

    private void getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()  //创建Retrofit实例
                .baseUrl("http://api.m.mtime.cn/")    //这里需要传入url的域名部分
                .addConverterFactory(GsonConverterFactory.create()) //返回的数据经过转换工厂转换成我们想要的数据，最常用的就是Gson
                .build();   //构建实例
        GetRequest_Interface retrofitService = retrofit.create(GetRequest_Interface.class);

        Call<Trailers> call = retrofitService.getCall();
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                mediaItems= response.body();
                handler.sendEmptyMessage(1);
//                Log.i("TAG", "onResponse: ="+trailers);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.i("TAG", "onFailure:"+t);
            }
        });
    }

}
