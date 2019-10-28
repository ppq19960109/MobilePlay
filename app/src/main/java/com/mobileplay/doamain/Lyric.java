package com.mobileplay.doamain;

/**
 * 作者：杨光福 on 2016/5/28 10:46
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：
 * Lyric
 歌词内容：content
 时间戳：timePoint
 高亮时间：sleepTime
 */
public class Lyric {

    private String content;

    private long timePoint;

    private long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
