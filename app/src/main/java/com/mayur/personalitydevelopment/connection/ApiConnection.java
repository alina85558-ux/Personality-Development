package com.mayur.personalitydevelopment.connection;


import android.content.Context;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mayur.personalitydevelopment.R;
import com.mayur.personalitydevelopment.Utils.Constants;
import com.mayur.personalitydevelopment.Utils.Utils;

import org.json.JSONObject;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiConnection {

    static Retrofit retrofit = null;

    public static Retrofit getClient() {
        try {
            if (retrofit == null) {
                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.getBaseUrl())
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retrofit;
    }

    public static void connectPost(final Context context,
                                   final SwipeRefreshLayout refreshLayout,
                                   Call<ResponseBody> connect,
                                   final ConnectListener listener) {
        try {
            if (connect != null) {
                if (!Utils.isNetworkAvailable(context)) {
                    listener.onConnectionFailure();
                    return;
                }

                if (refreshLayout != null) {
                    if (!refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(true);
                    }
                }

                connect.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        if (refreshLayout != null) {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }

                        ResponseBody responseData;

                        try {
                            if (response.isSuccessful()) {
                                responseData = response.body();
                                JSONObject jsonObject = new JSONObject(responseData.string());
                                String jsonResponse = "";
                                try {
                                    jsonResponse = jsonObject.getJSONObject("data").toString();
                                    listener.onResponseSuccess(jsonResponse, response.headers(), response.code());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    listener.onResponseSuccess(jsonResponse, response.headers(), response.code());
                                    //jsonResponse = jsonObject.getString("data");
                                }
                            } else {
                                listener.onResponseFailure(response.errorBody(), response.headers(), response.code());
                            }
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                            listener.onException(response.headers(), response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (refreshLayout != null) {
                            if (refreshLayout.isRefreshing()) {
                                refreshLayout.setRefreshing(false);
                            }
                        }
                        listener.onFailure(call.request().headers());
                    }
                });
            } else {
                Toast.makeText(context, context.getResources().getString(R.string.somehing_want_wrong), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ConnectListener {
        void onResponseSuccess(String response, Headers headers, int StatusCode);

        void onResponseFailure(ResponseBody responseData, Headers headers, int StatusCode);

        void onFailure(Headers headers);

        void onConnectionFailure();

        void onException(Headers headers, int StatusCode);
    }

/*
    public static void connectPostFPass(final Context context, final SwipeRefreshLayout refreshLayout, Call<BaseData> connect, final ConnectListener listener) {

        if (!Utils.isNetworkAvailable(context)) {
            listener.onConnectionFailure();
            return;
        }

        if (refreshLayout != null) {
            if (!refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
            }
        }

        connect.enqueue(new retrofit2.Callback<BaseData>() {
            @Override
            public void onResponse(Call<BaseData> call, Response<BaseData> response) {

                if (refreshLayout != null) {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                }

                try {
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(),  new MapDeserializerDoubleAsIntFix());

                    BaseData responseData = null;
                    if (response.isSuccessful()){
                        responseData = response.body();
                    }else{
                        responseData = new Gson().fromJson(response.errorBody().string(),BaseData.class);
                    }

                    if (responseData.getCode() == Constantss.STATUS_CODE.OK ) {
                        listener.onResponseSuccess(responseData, response.headers(), response.code());
                    } else {
                        listener.onResponseFailure(responseData, response.headers(), response.code());
                    }
                } catch (Exception e) {
                    if (e != null) {
                        e.printStackTrace();
                    }
                    listener.onException(response.headers(), response.code());
                }

            }

            @Override
            public void onFailure(Call<BaseData> call, Throwable t) {

                if (refreshLayout != null) {
                    if (refreshLayout.isRefreshing()) {
                        refreshLayout.setRefreshing(false);
                    }
                }

                listener.onFailure(call.request().headers());

            }
        });
    }
*/

}




