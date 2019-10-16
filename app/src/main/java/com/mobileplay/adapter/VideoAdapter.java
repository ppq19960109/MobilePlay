package com.mobileplay.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileplay.R;
import com.mobileplay.doamain.MediaItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

public class VideoAdapter extends BaseAdapter {

    private  ArrayList<MediaItem> mediaItems;
    private  Context context;

    public VideoAdapter(Context context, ArrayList<MediaItem> mediaItems){
        this.context=context;
        this.mediaItems=mediaItems;
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
            convertView = View.inflate(context, R.layout.adapt_video_pager, null);
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

        videoHolder.iv_icon.setImageBitmap(mediaItem.getBitmap());
        videoHolder.tv_name.setText(mediaItem.getName());
        videoHolder.tv_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        videoHolder.tv_time.setText(formatter.format(mediaItem.getDuration()));
        return convertView;
    }


     class VideoHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}
