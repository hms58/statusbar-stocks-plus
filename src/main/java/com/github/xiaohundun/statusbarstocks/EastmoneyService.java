package com.github.xiaohundun.statusbarstocks;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

public class EastmoneyService {

    public static JSONObject getDetail(String code) {
        String marketType = "1";
        if (!code.startsWith("6")) {
            marketType = "4";
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url = String.format("https://push2.eastmoney.com/api/qt/stock/get?secid=%s.%s&invt=2&fltt=2&fields=f43,f58,f60,f170&_=%s", marketType, code, new Date().getTime());
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return new JSONObject(response.body().string());
            }
        } catch (IOException e) {
            // noop
        }
        return null;
    }
}
