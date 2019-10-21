// IMusicService.aidl
package com.mobileplay.doamain;
import com.mobileplay.doamain.MediaItem;
import com.mobileplay.aidl.AudioMediaController;
// Declare any non-default types here with import statements

interface IMusicService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

           List<MediaItem> getMediaList();
           void setMediaList(inout List<MediaItem> mediaItems);

           void setMediaPosition(in int position);

           AudioMediaController getAudioMediaController();
}
