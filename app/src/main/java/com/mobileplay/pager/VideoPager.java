package com.mobileplay.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.mobileplay.R;
import com.mobileplay.adapter.VideoAdapter;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.mediaPlay.VideoPlay.system.SystemVideoPlayer;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class VideoPager extends BasePager {
    private final String MEDIA_LIST = "VideoList";
    private final String MEDIA_POSITION = "position";

    private ListView listview;
    private TextView tv_nomedia;
    private ProgressBar pb_load;

    private ArrayList<MediaItem> mediaItems = new ArrayList<>();

    public VideoPager() {

    }

    public VideoPager(Context context) {
        super(context);
    }

    public void initView(View view) {
        listview = view.findViewById(R.id.listview);
        tv_nomedia = view.findViewById(R.id.tv_nomedia);
        pb_load = view.findViewById(R.id.pb_load);
    }

    public void initListener() {

    }

    @Override
    public void initData() {
        super.initData();
        getDataFromLocal();
    }

    @Override
    public void close() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, null);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void getDataFromLocal() {

        Observable.create(new ObservableOnSubscribe<Cursor>() {
            @Override
            public void subscribe(ObservableEmitter<Cursor> emitter) throws Exception {
                Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContext().getContentResolver();
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.ARTIST,
                };
                Cursor cursor = contentResolver.query(videoUri, objs, null, null, null);
                emitter.onNext(cursor);
                emitter.onComplete();
            }
        }).map(new Function<Cursor, ArrayList<MediaItem>>() {
            @Override
            public ArrayList<MediaItem> apply(Cursor cursor) throws Exception {
                Log.e("TAG", "map..." + Thread.currentThread().getName());
                if (cursor != null) {
                    mediaItems.clear();
                    while (cursor.moveToNext()) {
                        MediaItem mediaItem = new MediaItem();
                        mediaItems.add(mediaItem);
                        String name = cursor.getString(0);
                        mediaItem.setName(name);
                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);
                        String data = cursor.getString(3);
                        mediaItem.setData(data);
                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);
                    }
                    cursor.close();
                }
                return mediaItems;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext(new Consumer<ArrayList<MediaItem>>() {
                    @Override
                    public void accept(ArrayList<MediaItem> mediaItems) throws Exception {
                        Log.e("TAG", "doAfterNext..." + Thread.currentThread().getName());
                        setListViewListener();
                    }
                })
                .subscribe(new Observer<ArrayList<MediaItem>>() {
                               @Override
                               public void onSubscribe(Disposable d) {

                               }

                               @Override
                               public void onNext(ArrayList<MediaItem> mediaItems) {
                                   Log.e("TAG", "onNext..." + Thread.currentThread().getName());

                                   if (mediaItems != null && mediaItems.size() > 0) {
                                       VideoAdapter videoAdapter = new VideoAdapter(getContext(), mediaItems);
                                       listview.setAdapter(videoAdapter);
                                   } else {
                                       tv_nomedia.setVisibility(View.VISIBLE);
                                   }
                                   pb_load.setVisibility(View.GONE);

                               }

                               @Override
                               public void onError(Throwable e) {

                               }

                               @Override
                               public void onComplete() {

                               }
                           }
                );

    }

    private void setListViewListener() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MediaItem item = (MediaItem) parent.getItemAtPosition(position);
                if (item != null) {
//                    Intent intent = new Intent(getContext(), SystemVideoPlayer.class);
//                    intent.setData(Uri.parse(item.getData()));
//                    getContext().startActivity(intent);

                    Intent intent = new Intent(getContext(), SystemVideoPlayer.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(MEDIA_LIST, mediaItems);
                    intent.putExtras(bundle);
                    intent.putExtra(MEDIA_POSITION, position);
                    getContext().startActivity(intent);
                }
            }

        });
    }

}
