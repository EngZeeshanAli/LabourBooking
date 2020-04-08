package com.example.labourbooking.chatapp.NotifationsAndServices;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAsoGBhBs:APA91bHz9Crf_YHqPHfn9-GgTtPhGqJ6qhKRvsil0CNsYMkt-xq_Xjxmx408re70dHCfUWBuhdDUciDszgew0y9yMIHT0kCQ68p3MKvV-y_xErT6MM92QCZP25NvARLk2rP1YI3knXNU"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
