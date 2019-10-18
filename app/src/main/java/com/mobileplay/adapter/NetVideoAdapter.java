package com.mobileplay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mobileplay.Interface.IRetrofitRequest;
import com.mobileplay.R;
import com.mobileplay.doamain.NetMediaItem;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NetVideoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NetMediaItem> mediaItems;

    public NetVideoAdapter(Context context, ArrayList<NetMediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoHolder videoHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.adapt_net_video_pager, null);
            videoHolder = new VideoHolder();
            videoHolder.iv_icon = convertView.findViewById(R.id.iv_icon);
            videoHolder.tv_name = convertView.findViewById(R.id.tv_name);
            videoHolder.tv_desc = convertView.findViewById(R.id.tv_desc);

            convertView.setTag(videoHolder);
        } else {
            videoHolder = (VideoHolder) convertView.getTag();
        }
        NetMediaItem mediaItem = mediaItems.get(position);

        videoHolder.tv_name.setText(mediaItem.getMovieName());
        videoHolder.tv_desc.setText(mediaItem.getVideoTitle());

//        getRetrofit(videoHolder.iv_icon,mediaItem.getCoverImg());
        getGlide(videoHolder.iv_icon, mediaItem.getCoverImg());

        return convertView;
    }

    private void getGlide(ImageView iv_icon, String coverImg) {
        Glide.with(context)
                .load(coverImg)
                .into(iv_icon);
    }

    private void getRetrofit(final ImageView view, String url) {
        Retrofit retrofit = new Retrofit.Builder()  //创建Retrofit实例
                .baseUrl("http://img5.mtime.cn/")    //这里需要传入url的域名部分
//                .addConverterFactory(GsonConverterFactory.create()) //返回的数据经过转换工厂转换成我们想要的数据，最常用的就是Gson
                .build();   //构建实例

        IRetrofitRequest retrofitService = retrofit.create(IRetrofitRequest.class);

        Call<ResponseBody> call = retrofitService.getNetVideoImage(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                Bitmap bitmap = BitmapFactory.decodeStream(body.byteStream());
                view.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("TAG", "onFailure:" + t);
            }
        });
    }

    private class VideoHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }

}
