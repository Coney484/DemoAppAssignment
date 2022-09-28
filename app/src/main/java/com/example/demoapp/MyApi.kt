package com.example.demoapp

import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyApi {


    @Multipart
    @POST("content.php?cid=7ec99b415af3e88205250e3514ce0fa7")
    fun uploadImage(

        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody
    ): retrofit2.Call<UploadResponse>


    companion object {
        operator fun invoke(): MyApi {
            return Retrofit.Builder()
                .baseUrl("https://dev.nojoto.com/api/beta/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}