package com.mobileplay.aidl;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.mobileplay.R;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.mediaPlay.AudioPlayer;
import com.mobileplay.mediaPlay.CacheUtils;
import com.mobileplay.pager.AudioPager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;



public class AudioMediaController implements Parcelable {
    public static final int REPEAT_NORMAL = 0;
    public static final int REPEAT_SINGLE = 1;
    public static final int REPEAT_ALL = 2;
    private int playMode =REPEAT_NORMAL;

    private Service service;
    public List<MediaItem> mediaItems;
    public MediaItem mediaItem;
    private int position;
    private int currentPosition = -1;
    public MediaPlayer mediaplayer;

    private OnPreparedListener mOnPrepared;

    public void setOnPreparedListener(OnPreparedListener OnPrepared) {
        this.mOnPrepared = OnPrepared;
    }

    public AudioMediaController(Service service) {
        this.service = service;

    }

    public void close() {
        if (mediaplayer != null) {
            mediaplayer.reset();
            mediaplayer.release();
            mediaplayer = null;
        }
        cancelNoification();
    }

    public List<MediaItem> getMediaItems() {
        return mediaItems;
    }

    public void setMediaItems(List<MediaItem> mediaItems) {
        this.mediaItems = mediaItems;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void openAudio() {
        if (currentPosition == position) {
            sendBroadcastToActivity();
            return;
        }
        mediaItem = mediaItems.get(position);
        if (mediaplayer != null) {
            mediaplayer.reset();
//            mediaplayer.release();
//            mediaplayer = null;
        } else {
            playMode=CacheUtils.getInt(service,"AudioPlayMode","playmode");
            mediaplayer = new MediaPlayer();
            //设置准备好的监听
            mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    currentPosition = position;
                    if (mOnPrepared != null) {
                        mOnPrepared.OnPrepared();
                    }
                    sendBroadcastToActivity();
                    startAndPause();
                }
            });
            mediaplayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return false;
                }
            });
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                        next();
//                      switch (playMode){
//                          case REPEAT_NORMAL:
//                          case REPEAT_ALL:
//                              next();
//                              break;
//                          case REPEAT_SINGLE:
//                              currentPosition=-1;
//                              openAudio();
//                              break;
//                      }
                }
            });
            mediaPlayLoop();
        }


        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            try {
                mediaplayer.setDataSource(mediaItem.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaplayer.prepareAsync();

        }

    }

    public void sendBroadcastToActivity() {
//        Intent intent = new Intent();
//        intent.setAction("AudioPlayer");
////        intent.setComponent(new ComponentName("com.mobileplay.mediaPlay","com.mobileplay.mediaPlay.AudioBroadcastReceiver"));
//        service.sendBroadcast(intent);

        EventBus.getDefault().post(new MediaItem());
    }
    public void cancelNoification() {
        NotificationManager manager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(5);
    }
    public void sendNoification() {
        NotificationManager manager = (NotificationManager) service.getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(service, AudioPlayer.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AudioPager.MEDIA_LIST, (ArrayList<MediaItem>)mediaItems);
        intent.putExtras(bundle);
        intent.putExtra(AudioPager.MEDIA_POSITION, position);
        intent.putExtra("Notification", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(service, 11,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder  builder = new Notification.Builder(service)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setWhen(System.currentTimeMillis())
                .setTicker("321音乐提示")
                .setContentTitle("321音乐")
                .setContentText("正在播放："+getName())
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setVibrate(new long[]{0})
                .setSound(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(true);
        }

        String ChannelId="com.mobileplay.aidla";
        String ChannelName="AudioMediaController";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(ChannelId, ChannelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            // 设置渠道描述
            notificationChannel.setDescription("test");
            // 是否绕过请勿打扰模式
//            notificationChannel.canBypassDnd();
            // 设置绕过请勿打扰模式
//            notificationChannel.setBypassDnd(true);
            // 桌面Launcher的消息角标
            notificationChannel.canShowBadge();
            // 设置显示桌面Launcher的消息角标
            notificationChannel.setShowBadge(true);
            // 设置通知出现时声音，默认通知是有声音的
            notificationChannel.setSound(null, null);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            notificationChannel.enableVibration(false);
//            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400,
//                    300, 200, 400});
            notificationChannel.setVibrationPattern(new long[]{0});

            //先删除之前的channelId对应的消息通道.
//            manager.deleteNotificationChannel(ChannelId);
            manager.createNotificationChannel(notificationChannel);

            builder .setChannelId(ChannelId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Notification build = builder.build();
            build.flags=Notification.FLAG_SHOW_LIGHTS;
            manager.notify(5, build);
            Log.i("TAG", "sendNoification: ");
        }

    }

    public boolean startAndPause() {
        if (mediaplayer.isPlaying()) {
            mediaplayer.pause();
            cancelNoification();
            return false;
        } else {
            mediaplayer.start();
            sendNoification();
            return true;
        }
    }

    public void next() {
        if(position+1<mediaItems.size()) {
            ++position;
            openAudio();
            return;
        }
        if (playMode==REPEAT_ALL) {
            if(position+1==mediaItems.size()) {
                position = 0;
                openAudio();
                return;
            }
        }
    }

    public void pre() {
        if(position>0) {
            --position;
            openAudio();
            return;
        }
        if (playMode==REPEAT_ALL) {
            if(position==0) {
                position = mediaItems.size()-1;
                openAudio();
                return;
            }
        }
    }

    public int getPlaymode() {
        return playMode;
    }

    public void setPlaymode(int playmode) {
        playMode=playmode;
        mediaPlayLoop();
        CacheUtils.putInt(service,"AudioPlayMode","playmode",playmode);
    }

    private void mediaPlayLoop() {
        if(playMode==REPEAT_SINGLE){
            mediaplayer.setLooping(true);
        }else {
            mediaplayer.setLooping(false);
        }
    }

    public int getCurrentPosition() {
        if (mediaplayer != null) {
            return mediaplayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaplayer != null) {
            return mediaplayer.getDuration();
        }
        return 0;
    }

    public String getName() {
        return mediaItem.getName();
    }

    public String getArtist() {
        return mediaItem.getArtist();
    }

    public void seekTo(int seekto) {
        mediaplayer.seekTo(seekto);
    }

    public boolean isPlaying() {
        return false;
    }

    public void notifyChange(String action) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mediaItems);
        dest.writeInt(this.position);
        dest.writeInt(this.currentPosition);
//        dest.writeParcelable(this.mediaplayer, flags);
        dest.writeParcelable(this.mOnPrepared, flags);
    }

    protected AudioMediaController(Parcel in) {
        this.mediaItems = in.createTypedArrayList(MediaItem.CREATOR);
        this.position = in.readInt();
        this.currentPosition = in.readInt();
//        this.mediaplayer = in.readParcelable(MediaPlayer.class.getClassLoader());
        this.mOnPrepared = in.readParcelable(OnPreparedListener.class.getClassLoader());
    }

    public static final Creator<AudioMediaController> CREATOR = new Creator<AudioMediaController>() {
        @Override
        public AudioMediaController createFromParcel(Parcel source) {
            return new AudioMediaController(source);
        }

        @Override
        public AudioMediaController[] newArray(int size) {
            return new AudioMediaController[size];
        }
    };

    public static class OnPreparedListener implements Parcelable {
        public void OnPrepared() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        public OnPreparedListener() {
        }

        protected OnPreparedListener(Parcel in) {
        }

        public static final Creator<OnPreparedListener> CREATOR = new Creator<OnPreparedListener>() {
            @Override
            public OnPreparedListener createFromParcel(Parcel source) {
                return new OnPreparedListener(source);
            }

            @Override
            public OnPreparedListener[] newArray(int size) {
                return new OnPreparedListener[size];
            }
        };
    }
}

