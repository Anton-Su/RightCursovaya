package com.example.rightcursovaya;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/clients")
    Call<List<Client>> getClients();

    @POST("/auth/register")
    Call<ResponseBody> register(@Body Client client);

    @POST("/auth/login")
    Call<ResponseBody> login(@Body Client client);

    @POST("/auth/delete")
    Call<ResponseBody> delete(@Body Client client);

    @GET("/timetable")
    Call<List<Timetable>> getDoctors();

    @PUT("/timetable/{id}")
    Call<Timetable> updateItem(@Path("id") Long id, @Body Timetable item);

    @POST("/timetable")
    Call<Timetable> createItem(@Body Timetable item);


    @GET("/illnesses")
    Call<List<Illness>> getIlnesses();

    @POST("/illnesses")
    Call<Illness> createIllness(@Body Illness illness);

    @DELETE("/illnesses/{id}")
    Call<Void> deleteIllness(@Path("id") Long id);

    @POST("/complains")
    Call<Void> createComplain(@Body Complain complain);
}