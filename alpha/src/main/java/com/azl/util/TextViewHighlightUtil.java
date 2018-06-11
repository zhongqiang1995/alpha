package com.azl.util;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhong on 2018/3/6.
 */

public class TextViewHighlightUtil {


    public static void highlight(TextView tv, String text, String key, boolean isCase, int color) {
        SpannableStringBuilder sb = getSingleHighlight(text, key, isCase, color);
        tv.setText(sb);
    }

    public static void highlight(TextView tv, String text, String[] key, boolean isCase, int color) {
        SpannableStringBuilder sb = getMultipleHighlight(text, key, isCase, color);
        tv.setText(sb);
    }

    public static void highlight(TextView tv, String text, String[] key) {
        highlight(tv, text, key, false, Color.RED);
    }

    public static void highlight(TextView tv, String text, String key) {
        highlight(tv, text, key, false, Color.RED);
    }


    public static SpannableStringBuilder getSingleHighlight(String text, String key, boolean isCase, int color) {
        try {
            if (TextUtils.isEmpty(text)) {
                return new SpannableStringBuilder("");
            }
            if (TextUtils.isEmpty(key)) {
                return new SpannableStringBuilder(text);
            }
            String sText;
            String sKey;
            if (!isCase) {
                sText = text.toLowerCase();
                sKey = key.toLowerCase();
            } else {
                sText = text;
                sKey = key;
            }
            SpannableStringBuilder sb = new SpannableStringBuilder(text);

            Pattern p = Pattern.compile(sKey);
            Matcher m = p.matcher(sText);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                sb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            return sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableStringBuilder(text == null ? "" : text);
    }

    public static SpannableStringBuilder getMultipleHighlight(String text, String[] keys, boolean isCase, int color) {
        try {
            if (TextUtils.isEmpty(text)) {
                return new SpannableStringBuilder("");
            }
            if (keys == null || keys.length == 0) {
                return new SpannableStringBuilder(text);
            }
            String sText;
            if (isCase) {
                sText = text;
            } else {
                sText = text.toLowerCase();
            }

            SpannableStringBuilder sb = new SpannableStringBuilder(text);
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                String sKey = "";
                if (isCase) {
                    sKey = key;
                } else {
                    sKey = key.toLowerCase();
                }
                Pattern p = Pattern.compile(sKey);
                Matcher m = p.matcher(sText);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    sb.setSpan(new ForegroundColorSpan(color), start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            return sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableStringBuilder(text == null ? "" : text);
    }
}
