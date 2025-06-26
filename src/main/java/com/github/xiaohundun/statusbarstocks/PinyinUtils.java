package com.github.xiaohundun.statusbarstocks;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtils {
    /**
     * 将中文转换为拼音首字母（大写）
     * @param chinese 中文字符串
     * @return 首字母大写字符串（如 "中国" → "ZG"）
     */
    public static String toFirstCharUpperCase(String chinese) {
        if (chinese == null || chinese.trim().isEmpty()) {
            return chinese;
        }

        StringBuilder result = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 忽略声调

        for (char c : chinese.toCharArray()) {
            if (Character.toString(c).matches("[\\u4E00-\\u9FA5]")) { // 匹配汉字
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyinArray != null && pinyinArray.length > 0) {
                        result.append(pinyinArray[0].charAt(0)); // 取拼音首字母
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                result.append(c); // 非汉字保留原字符
            }
        }
        return result.toString().toUpperCase(); // 统一转为大写
    }
}