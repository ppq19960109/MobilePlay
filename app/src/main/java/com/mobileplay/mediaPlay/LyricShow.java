package com.mobileplay.mediaPlay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.mobileplay.Utils.DensityUtil;
import com.mobileplay.doamain.Lyric;

import java.util.ArrayList;

import androidx.annotation.Nullable;


public class LyricShow extends TextView {
    private  Context context;
    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint whitePaint;

    /**
     * 歌词下标的索引
     */
    private int index;
    private int textHeight;

    private int currentPosition;
    private long sleepTime;
    private long timePoint;

    public LyricShow(Context context) {
        this(context,null);
    }

    public LyricShow(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public LyricShow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置歌词列表
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    private void initView()
    {
        textHeight = DensityUtil.dip2px(context,30);

        paint=new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtil.dip2px(context,20));
        paint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        whitePaint.setTextSize(DensityUtil.dip2px(context,20));
        //设置文字居中
        whitePaint.setTextAlign(Paint.Align.CENTER);
//        添加假歌词
//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1001; i++) {
//            lyric.setContent(i + "aaaaaaaaa" + i);
//            lyric.setSleepTime(5000 + i);
//            lyric.setTimePoint(5000 * i);
//            lyrics.add(lyric);//添加到集合中
//            lyric = new Lyric();
//        }
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=w;
        height=h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lyrics!=null&&lyrics.size()>0&&index< lyrics.size()){

            //平移动画
            float dy = 0;
            if(sleepTime ==0){
                dy = 0;
            }else{
                //花的时间:休眠时间 = 移动距离：行高
//                float push = ((currentPosition-timePoint)/sleepTime)*textHeight;

                // 坐标 = 行高 + 移动距离
                dy = ((currentPosition-timePoint)/sleepTime)*textHeight;
            }
//            Log.i("TAG", "onDraw: "+dy);
            canvas.translate(0,-dy);

            String content = lyrics.get(index).getContent();
            canvas.drawText(content, width / 2, height/2, paint);

            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }

            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitePaint);
            }
        }else {
            canvas.drawText("歌词不存在", width / 2, height/2, paint);
        }


    }

    public void setShowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null)
            return;
        for (int i = 1; i < lyrics.size(); i++) {
            //划出区域
            if (currentPosition < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;//0->1
                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    index = tempIndex;//歌词下标索引
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                    break;
               }
            }
        }
        invalidate();//强制绘制-onDraw()方法再次执行
    }
}
