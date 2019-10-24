package com.mobileplay.aidl;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobileplay.doamain.MediaItem;

import java.io.IOException;
import java.util.List;

public class AudioMediaController implements Parcelable {
    private Service service;
    public List<MediaItem> mediaItems;
    public MediaItem mediaItem;
    private int position;
    private int currentPosition=-1;
    public MediaPlayer mediaplayer;

    private OnPreparedListener mOnPrepared;

    public void setOnPreparedListener(OnPreparedListener OnPrepared){
        this.mOnPrepared=OnPrepared;
    }

    public AudioMediaController(Service service) {
        this.service=service;
    }
    public void close(){
        if (mediaplayer != null) {
            mediaplayer.reset();
            mediaplayer.release();
            mediaplayer = null;
        }
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
        if(currentPosition==position){
            return;
        }
        mediaItem=mediaItems.get(position);
        if (mediaplayer != null) {
            mediaplayer.reset();
//            mediaplayer.release();
//            mediaplayer = null;
        } else {
            mediaplayer = new MediaPlayer();
            //设置准备好的监听
            mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if(mOnPrepared!=null){
                        mOnPrepared.OnPrepared();
                    }
                    sendBroadcastToActivity();
                    mediaplayer.start();
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

                }
            });

        }
        if (mediaItems != null && mediaItems.size() > 0) {
            MediaItem mediaItem = mediaItems.get(position);
            try {
                mediaplayer.setDataSource(mediaItem.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaplayer.prepareAsync();
            currentPosition=position;
        }
    }

    private void sendBroadcastToActivity() {
        Intent intent = new Intent();
        intent.setAction("AudioPlayer");
//        intent.setComponent(new ComponentName("com.mobileplay.mediaPlay","com.mobileplay.mediaPlay.AudioBroadcastReceiver"));
        service.sendBroadcast(intent);
    }

    public boolean startAndPause() {
          if(mediaplayer.isPlaying()){
              mediaplayer.pause();
              return false;
          }else {
              mediaplayer.start();
              return true;
          }
    }

    public void pause() {

    }

    public void next() {

    }

    public void pre() {

    }

    public int getPlaymode() {
        return 0;
    }

    public void setPlaymode(int playmode) {

    }

    public int getCurrentPosition() {
        if(mediaplayer!=null){
            return mediaplayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if(mediaplayer!=null) {
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
        public void OnPrepared(){}

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

