package com.mobileplay.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mobileplay.aidl.AudioMediaController;
import com.mobileplay.doamain.IMusicService;
import com.mobileplay.doamain.MediaItem;

import java.util.List;

public class MusicService extends Service {

    private AudioMediaController audioMediaController = new AudioMediaController();

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "MusicService onCreate");
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "MusicService onDestroy");
        audioMediaController.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void initData() {

    }


    private final IMusicService.Stub stub = new IMusicService.Stub() {
        @Override
        public List<MediaItem> getMediaList() throws RemoteException {
            return audioMediaController.getMediaItems();
        }

        @Override
        public void setMediaList(List<MediaItem> mediaItems) throws RemoteException {
            audioMediaController.setMediaItems(mediaItems);
        }

        @Override
        public void setMediaPosition(int position) throws RemoteException {
            audioMediaController.setPosition(position);
        }

        @Override
        public AudioMediaController getAudioMediaController() throws RemoteException {
            return audioMediaController;
        }

    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG", "MusicService onBind");
        return stub;
    }

}
