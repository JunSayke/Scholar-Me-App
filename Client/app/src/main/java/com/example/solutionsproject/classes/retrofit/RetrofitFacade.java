package com.example.solutionsproject.classes.retrofit;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFacade {
    private final RetrofitService retrofitService;
    protected static final Set<String> cookies = new HashSet<>();

    public RetrofitFacade(String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(getCookiesInterceptor())
                .addInterceptor(getRequestInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    public RetrofitService getRetrofitService() {
        return retrofitService;
    }

    public Interceptor getCookiesInterceptor() {
        return new CookiesInterceptor();
    }

    public Interceptor getRequestInterceptor() {
        return new RequestInterceptor();
    }

    private static class CookiesInterceptor implements Interceptor {
        private final String TAG = "CookiesInterceptor";

        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);

            List<String> cookies = response.headers("Set-Cookie");

            RetrofitFacade.cookies.addAll(cookies);

//            Log.d(TAG, RetrofitFacade.cookies.toString());

            return response;
        }
    }

    private static class RequestInterceptor implements Interceptor {
        private final String TAG = "RequestInterceptor";
        @NonNull
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder requestBuilder = chain.request().newBuilder();

            for (String cookie : RetrofitFacade.cookies) {
                requestBuilder.addHeader("Cookie", cookie);
            }

//            Log.d(TAG, RetrofitFacade.cookies.toString());

            return chain.proceed(requestBuilder.build());
        }
    }
}
