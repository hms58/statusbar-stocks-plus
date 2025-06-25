package com.github.xiaohundun.statusbarstocks;

import java.util.Arrays;
import java.util.Comparator;

public class StringUtils {

    /**
     * 移除字符串结尾的第一个匹配后缀（按数组顺序匹配）
     * @param str 原始字符串
     * @param suffixes 后缀数组
     * @return 处理后的字符串
     */
    public static String removeFirstSuffix(String str, String[] suffixes) {
        if (str == null || suffixes == null) return str;

        for (String suffix : suffixes) {
            if (suffix != null && str.endsWith(suffix)) {
                return str.substring(0, str.length() - suffix.length());
            }
        }
        return str;
    }

    /**
     * 递归移除所有匹配后缀（优先移除最长后缀）
     * @param str 原始字符串
     * @param suffixes 后缀数组
     * @return 处理后的字符串
     */
    public static String removeAllSuffixes(String str, String[] suffixes) {
        if (str == null || suffixes == null) return str;

        // 按长度降序排序（优先匹配长后缀）
        String[] sortedSuffixes = Arrays.copyOf(suffixes, suffixes.length);
        Arrays.sort(sortedSuffixes, Comparator.comparingInt(String::length).reversed());

        for (String suffix : sortedSuffixes) {
            if (suffix != null && !suffix.isEmpty() && str.endsWith(suffix)) {
                String newStr = str.substring(0, str.length() - suffix.length());
                // 递归检查新字符串是否仍有后缀
                return removeAllSuffixes(newStr, sortedSuffixes);
            }
        }
        return str;
    }
}
