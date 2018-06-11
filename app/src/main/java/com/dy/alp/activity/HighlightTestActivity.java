package com.dy.alp.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.azl.util.TextViewHighlightUtil;
import com.dy.alp.R;

public class HighlightTestActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5,tv6,tv7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highlight_test);
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tv7 = findViewById(R.id.tv7);

        TextViewHighlightUtil.highlight(tv1, null, "的");
        TextViewHighlightUtil.highlight(tv2, "abcAbcAcba", "a");
        TextViewHighlightUtil.highlight(tv3, "abcAbcAcba", "a", true, Color.BLUE);

        TextViewHighlightUtil.highlight(tv4, "我的他的你的", new String[]{"我", "你", "他"});
        TextViewHighlightUtil.highlight(tv5, "我的他的你的", new String[]{"我", "你", "他"});
        TextViewHighlightUtil.highlight(tv6, "abcQWEAbcAA", new String[]{"abc", "q", "A"});
        TextViewHighlightUtil.highlight(tv7, "abcQWEAbcAA", new String[]{"abc", "q", "A"}, true, Color.MAGENTA);


    }
}
