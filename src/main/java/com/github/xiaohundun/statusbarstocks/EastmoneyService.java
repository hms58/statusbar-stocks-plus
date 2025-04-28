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
        String secID = code;
        if (!code.startsWith("6") && !code.startsWith("5")) {
            marketType = "0";
        }
        if (code.startsWith("sz")) {
            marketType = "0";
            secID = code.substring(2); // 去掉前缀
        } else if (code.startsWith("sh")) {
            marketType = "1";
            secID = code.substring(2); // 去掉前缀
        } else if (code.startsWith("hk")) {
            marketType = "116";
            secID = code.substring(2); // 去掉前缀
        } else if (code.startsWith("us")) {
            marketType = "105";
            secID = code.substring(2); // 去掉前缀
        }

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url = String.format("https://push2.eastmoney.com/api/qt/stock/get?secid=%s.%s&invt=2&fltt=2&fields=f43,f57,f58,f60,f170&_=%s", marketType, secID, new Date().getTime());
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
