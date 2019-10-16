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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mobileplay.Interface.GetRequest_Interface;
import com.mobileplay.R;
import com.mobileplay.activity.SystemVideoPlayer;
import com.mobileplay.adapter.NetVideoAdapter;
import com.mobileplay.adapter.VideoAdapter;
import com.mobileplay.base.BasePager;
import com.mobileplay.common.CommonUtils;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.doamain.Movie;
import com.mobileplay.doamain.NetMediaItem;

import java.io.Serializable;
import java.util.ArrayList;

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
    private ArrayList<Movie> NetMediaItems;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (NetMediaItems != null && NetMediaItems.size() > 0) {
                        NetVideoAdapter netVideoAdapter = new NetVideoAdapter(getContext(), NetMediaItems);
//                        netVideoAdapter.notifyDataSetChanged();
                        listview.setAdapter(netVideoAdapter);
                    } else {
                        tv_nomedia.setVisibility(View.VISIBLE);
                    }
                    pb_load.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
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
    public void onStart() {
        super.onStart();


    }

    private void setListViewListener() {
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
                    bundle.putSerializable("VideoList",  NetMediaItems);
                    intent.putExtras(bundle);
                    intent.putExtra("position",position);
                    getContext().startActivity(intent);
                }

            }

        });
    }

    public void onResume() {
        super.onResume();


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
        initData();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListViewListener();
    }

    public void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        listview = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_load = view.findViewById(R.id.pb_load);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               new Thread(){
                   @Override
                   public void run() {
                       super.run();
                       initData();
                   }
               }.start();
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    //当不滚动的时候
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        //判断是否是最底部
                        if(view.getLastVisiblePosition()==(view.getCount())-1 ){
                            CommonUtils.showToastMsg(null,"我是底部");
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
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

        Call<NetMediaItem> call = retrofitService.getCall();
        call.enqueue(new Callback<NetMediaItem>() {
            @Override
            public void onResponse(Call<NetMediaItem> call, Response<NetMediaItem> response) {
                NetMediaItems= response.body().getTrailers();
                handler.sendEmptyMessage(1);

//                Log.i("TAG", "onResponse: ="+trailers);
            }

            @Override
            public void onFailure(Call<NetMediaItem> call, Throwable t) {
                Log.i("TAG", "onFailure:"+t);
            }
        });
    }

}
