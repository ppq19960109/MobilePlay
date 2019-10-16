package com.usb.startall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import io.vov.vitamio.LibsChecker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
    }

    public void startAll(View view) {
        Intent intent = new Intent();
        intent.setDataAndType(Uri.parse("https://vd3.bdstatic.com/mda-jir8449k6axgtzni/sc/mda-jir8449k6axgtzni.mp4"), "video/*");
        startActivity(intent);
    }
    public void startAllErr(View view) {
        Intent intent = new Intent();

//        intent.setDataAndType(Uri.parse("https://vd3.bdstatic.com/mda-jir8449k6axgtzni/sc/mda-jir8449k6axgtzni1.mp4"), "video/*");
        intent.setDataAndType(Uri.parse("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8"), "video/*");
//        intent.setDataAndType(Uri.parse("https://www.huya.com/bbwen"), "video/*");
        startActivity(intent);
    }
}
