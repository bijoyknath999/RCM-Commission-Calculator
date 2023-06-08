package com.rcm.calculator.api;


import com.rcm.calculator.models.InstallUsers;
import com.rcm.calculator.models.PrivacyPolicy;
import com.rcm.calculator.models.Settings;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiRequest {
    @FormUrlEncoded
    @POST("Calculator_app_settings/users")
    Call<List<Settings>> getSettings(@Field("apikey") String apikey);

    @FormUrlEncoded
    @POST("Calculator_install_update/users")
    Call<InstallUsers> saveInstallation(@Field("apikey") String apikey);


    @FormUrlEncoded
    @POST("Calculator_app_policy/users")
    Call<List<PrivacyPolicy>> getAllText(@Field("apikey") String apikey);
}
