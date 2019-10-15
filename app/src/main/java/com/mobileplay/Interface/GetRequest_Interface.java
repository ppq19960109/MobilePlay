package com.mobileplay.Interface;

import com.mobileplay.doamain.NetMediaItem;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface GetRequest_Interface {
    @GET("PageSubArea/TrailerList.api")
    Call<NetMediaItem> getCall();

    @GET
    Call<ResponseBody> getImg(@Url String uri);
}
