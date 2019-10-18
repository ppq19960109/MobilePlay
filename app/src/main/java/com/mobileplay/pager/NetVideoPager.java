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
import com.mobileplay.activity.SystemVideoPlayer1;
import com.mobileplay.adapter.NetVideoAdapter;
import com.mobileplay.base.BasePager;
import com.mobileplay.common.CacheUtils;
import com.mobileplay.common.CommonUtils;
import com.mobileplay.doamain.NetMediaItem;
import com.mobileplay.doamain.Trailers;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetVideoPager extends BasePager {
    public final String NET_VIDEO_URL = "http://api.m.mtime.cn/PageSubArea/TrailerList.api";
    private final int GET_MEDIA = 1;

    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_load;

    private ArrayList<NetMediaItem> netMediaItems;

    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;

    private Handler handler = new MyHandler(this);

    @Override
    public void mHandleMessage(Message msg) {
        switch (msg.what) {
            case GET_MEDIA:
                if (netMediaItems != null && netMediaItems.size() > 0) {
                    NetVideoAdapter netVideoAdapter = new NetVideoAdapter(getContext(), netMediaItems);
                    listview.setAdapter(netVideoAdapter);
                } else {
                    tv_nomedia.setVisibility(View.VISIBLE);
                }
                pb_load.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
    }

    public NetVideoPager() {

    }

    public NetVideoPager(Context context) {
        super(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_net_video, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initListener();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    public void initView(View view) {
        listview = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_load = view.findViewById(R.id.pb_load);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

    }

    public void initListener() {
        setListViewListener();
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        initData();
                    }
                }.start();
            }
        });

    }

    @Override
    public void initData() {
        super.initData();
        netMediaItems = (ArrayList<NetMediaItem>) CacheUtils.readObject(context, "NetVideo");
//        handler.sendEmptyMessage(GET_MEDIA);
        getNetDataFromRetrofit();
    }
    @Override
    public void close() {
        handler.removeCallbacksAndMessages(null);
    }
    private void getNetDataFromRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()  //创建Retrofit实例
                .baseUrl("http://api.m.mtime.cn/")    //这里需要传入url的域名部分
                .addConverterFactory(GsonConverterFactory.create()) //返回的数据经过转换工厂转换成我们想要的数据，最常用的就是Gson
                .build();   //构建实例

        GetRequest_Interface retrofitService = retrofit.create(GetRequest_Interface.class);

        Call<Trailers> call = retrofitService.getCall();

        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                netMediaItems = response.body().getTrailers();
                CacheUtils.saveObject(context, netMediaItems, "NetVideo");
                handler.sendEmptyMessage(GET_MEDIA);
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Log.i("TAG", "onFailure:" + t);
                handler.sendEmptyMessage(GET_MEDIA);
            }
        });
    }

    private void setListViewListener() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetMediaItem item = (NetMediaItem) parent.getItemAtPosition(position);
                if (item != null) {
//                    Intent intent = new Intent(getContext(), SystemVideoPlayer1.class);
//                    intent.setData(Uri.parse(item.getData()));
//                    getContext().startActivity(intent);/

                    Intent intent = new Intent(getContext(), SystemVideoPlayer1.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("VideoList", netMediaItems);
                    intent.putExtras(bundle);
                    intent.putExtra("position", position);
                    getContext().startActivity(intent);
                }
            }
        });
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    //当不滚动的时候
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        //判断是否是最底部
                        if (view.getLastVisiblePosition() == (view.getCount()) - 1) {
                            CommonUtils.showToastMsg(null, "我是底部");
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

}
