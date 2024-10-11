package com.example.projecmanage.apis

import com.example.projecmanage.Models.Notification
import com.example.projecmanage.Utils.Constants
//import com.google.auth.oauth2.AccessToken
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/*interface NotificationInterface {

    @POST("/v1/projects/projecmanage-ffe79/messages:send")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json "
    )
    fun notification(
        @Body message : Notification,
        @Header("Authorization") accessToken: String = "Bearer ${Constants.getAccessToken()}"
    ):Call<Notification>
}

 */