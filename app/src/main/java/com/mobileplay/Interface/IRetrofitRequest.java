package com.mobileplay.Interface;

import com.mobileplay.doamain.Trailers;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IRetrofitRequest {
    @GET("PageSubArea/TrailerList.api")
    Observable<Trailers> getNetVideoList();
//    Call<Trailers> getNetVideoList();

    @GET
    Call<ResponseBody> getNetVideoImage(@Url String uri);
}
