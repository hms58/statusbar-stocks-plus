package com.github.xiaohundun.statusbarstocks;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TencentService {
    public static List<JSONObject> getDetail(String[] codeList) {
        String q = "";
        for (String code : codeList) {
            if (code.startsWith("hk")) {
                q += String.format("r_%s,", code);
            } else{
               q += code+",";
            }
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String url = String.format("https://sqt.gtimg.cn/?q=%s&_=%s", q, new Date().getTime());
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                String body = response.body().string();
                return getJsonObjects(body);
            }
        } catch (IOException e) {
            // noop
        }
        return null;
    }

    private static @NotNull List<JSONObject> getJsonObjects(String body) {
        String[] lines = body.split(";");
        List<JSONObject> result = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length < 2) {
                continue; // 无效行，跳过
            }

            // 1. 提取等号右边的数据部分（去掉引号和分号）
            String dataPart = parts[1]
                    .replaceAll("[\";]\n", "")  // 移除双引号和分号
                    .trim();

            // 2. 按波浪符 ~ 分割字段
            String[] fields = dataPart.split("~");
            if (fields.length < 33) {
                continue;
            }
            // 3. 提取名称和代码（第2、3个字段）
            String name = fields[1];
            String code = fields[2];
            String price = fields[3];
            String percent = fields[32];
            String yesterday = fields[4];

            JSONObject json = new JSONObject();
            json.put("code", code);
            json.put("name", name);
            json.put("price", price);
            json.put("percent", percent);
            json.put("yesterday", yesterday);
            result.add(json);
        }
        return result;
    }
}
