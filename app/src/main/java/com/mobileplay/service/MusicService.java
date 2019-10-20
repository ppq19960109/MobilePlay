package com.mobileplay.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mobileplay.doamain.IMusicService;
import com.mobileplay.doamain.MediaItem;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {

    private List<MediaItem> mediaItems;


    private int position;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initData();
    }

    private void initData() {

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

    private final IMusicService.Stub stub = new IMusicService.Stub() {
        @Override
        public List<MediaItem> getMediaList() throws RemoteException {
            return getMediaItems();
        }

        @Override
        public void setMediaList(List<MediaItem> mediaItems) throws RemoteException {
            setMediaItems(mediaItems);
            Log.e("TAG", mediaItems.get(0).toString());
        }

        @Override
        public void setMediaPosition(int position) throws RemoteException {
            setPosition(position);
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private AudioMedia audioMedia = new AudioMedia();

    public class AudioMedia {
        private MediaPlayer mediaplayer;

        public void openAudio(int position) {
            if(mediaplayer != null){
                mediaplayer.reset();
                mediaplayer.release();
                mediaplayer = null;
            }else {
                mediaplayer = new MediaPlayer();
                //设置准备好的监听
                mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
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
            if(mediaItems != null && mediaItems.size() >0) {
                MediaItem mediaItem = mediaItems.get(position);
                try {
                    mediaplayer.setDataSource(mediaItem.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaplayer.prepareAsync();
            }
        }

        public void start() {

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
            return 0;
        }

        public int getDuration() {
            return 0;
        }

        public String getName() throws RemoteException {
            return null;
        }

        public String getArtist() throws RemoteException {
            return null;
        }

        public void seekTo(int seekto) {

        }

        public boolean isPlaying() {
            return false;
        }

        public void notifyChange(String action) {

        }
    }
}
