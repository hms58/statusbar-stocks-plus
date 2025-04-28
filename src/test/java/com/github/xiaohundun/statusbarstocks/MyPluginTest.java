package com.github.xiaohundun.statusbarstocks;

public class MyPluginTest {
    public static void main(String[] args) {

        System.out.println(EastmoneyService.getDetail("601919"));

        System.out.println(TencentService.getDetail(new String[]{"sh000001","hk00700","hkHSI"}));
    }
}
