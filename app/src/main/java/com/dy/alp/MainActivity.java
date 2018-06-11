package com.dy.alp;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.azl.activity.photo.PhotoPreviewActivity;
import com.azl.file.helper.D;
import com.dy.alp.activity.FlowLayoutActivity;
import com.dy.alp.activity.GridImageViewTestAcivity;
import com.dy.alp.activity.GridSelectViewTestActivity;
import com.dy.alp.activity.HighlightTestActivity;
import com.dy.alp.activity.ListMoreActivityTest;
import com.dy.alp.activity.ListTestActivity;
import com.dy.alp.activity.ScaleViewActivity;
import com.dy.alp.activity.SwitchLayoutTestActivity;
import com.dy.alp.activity.UploadFileActivity;
import com.dy.alp.activity.ViewPagerActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9, bt10, bt11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        D.APP = this;
        setContentView(R.layout.activity_main);

        Set<RequestListener> listeners = new HashSet<>();
        listeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(listeners)
                .build();
        Fresco.initialize(this, config);


        initView();
        initListener();
    }


    private void initView() {
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);
        bt5 = findViewById(R.id.bt5);
        bt6 = findViewById(R.id.bt6);
        bt7 = findViewById(R.id.bt7);
        bt8 = findViewById(R.id.bt8);
        bt9 = findViewById(R.id.bt9);
        bt10 = findViewById(R.id.bt10);
        bt11 = findViewById(R.id.bt11);
    }

    private void initListener() {
        bt11.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
        bt4.setOnClickListener(this);
        bt5.setOnClickListener(this);
        bt6.setOnClickListener(this);
        bt7.setOnClickListener(this);
        bt8.setOnClickListener(this);
        bt10.setOnClickListener(this);
        bt9.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bt1) {
            startActivity(new Intent(this, ViewPagerActivity.class));

        } else if (id == R.id.bt2) {
            startActivity(new Intent(this, ListMoreActivityTest.class));
        } else if (id == R.id.bt3) {
            startActivity(new Intent(this, UploadFileActivity.class));
        } else if (id == R.id.bt4) {
            startActivity(new Intent(this, ScaleViewActivity.class));
        } else if (id == R.id.bt5) {
            startActivity(new Intent(this, ListTestActivity.class));

        } else if (id == R.id.bt6) {
            startActivity(new Intent(this, SwitchLayoutTestActivity.class));
        } else if (id == R.id.bt7) {
            startActivity(new Intent(this, FlowLayoutActivity.class));
        } else if (id == R.id.bt8) {
            startActivity(new Intent(this, GridImageViewTestAcivity.class));
        } else if (id == R.id.bt9) {
            startActivity(new Intent(this, HighlightTestActivity.class));
        } else if (id == R.id.bt10) {
            startActivity(new Intent(this, GridSelectViewTestActivity.class));
        } else if (id == R.id.bt11) {
            Object[] arr = new Object[]{
                    Environment.getExternalStorageDirectory() + "/2222",
                    Environment.getExternalStorageDirectory() + "/1.jpg",
                    "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1523347806&di=089b052e636b7871a65ed149640a155b&imgtype=jpg&er=1&src=http%3A%2F%2Fattimg.dospy.com%2Fimg%2Fday_100711%2F20100711_a45f5ecbbf9a7f3493b0T4t8LFe86N32.png",

                    "file:///android_asset/222.jpg",
                    R.drawable.pull_to_refresh_arrow,
                    "https://fs.dyfchk2.kuxiao.cn/eYWt-t==",
                    "https://fs.dyfchk2.kuxiao.cn/eYWt-t==.jpg"
            };
            Intent intent = PhotoPreviewActivity.getJumpIntent(this, 0, "", arr);
            startActivity(intent);
        }

    }
}
