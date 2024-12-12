/*
 * Copyright (c)2019. ByteDance Inc. All rights reserved.
 */

package com.nocturne.utils;


import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HttpsUtil {

    private static final Integer CONNECT_TIME_OUT = 3;

    private static final Integer READ_TIME_OUT = 30;

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
            .build();


    /**
     * 带参get请求
     *
     * @param params request params
     * @param url    request url
     * @param clazz  to deserialize
     * @return the deserialized object instance
     */
    public static <T> T get(String url, Map<String, String> params, Class<T> clazz) {
        return get(url, params, clazz, Maps.newHashMap());
    }

    /**
     * 带参get请求
     *
     * @param params request params
     * @param url    request url
     * @param clazz  to deserialize
     * @return the deserialized object instance
     */
    public static <T> T get(String url, Map<String, String> params, Class<T> clazz, Map<String, String> headers) {
        try (Response response = doGet(url, params, headers)) {
            if (!response.isSuccessful() || Objects.isNull(response.body())) {
                return null;
            }

            String body = response.body().string();
            if (StringUtils.isBlank(body)) {
                return null;
            }

            return JSON.parseObject(body, clazz);
        } catch (IOException e) {
            throw new RuntimeException("read responseBody failed", e);
        }
    }

    public static Response doGet(String url, Map<String, String> params, Map<String, String> headers) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder();
        if (params != null) {
            params.forEach(urlBuilder::addQueryParameter);
        }

        String requestUrl = urlBuilder.build().toString();
        Request.Builder requestBuilder = new Request.Builder().url(requestUrl);
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(requestBuilder::addHeader);
        }

        Call call = okHttpClient.newCall(requestBuilder.build());
        try {
            return call.execute();
        } catch (IOException e) {
            throw new RuntimeException("do get request failed", e);
        }
    }

}
