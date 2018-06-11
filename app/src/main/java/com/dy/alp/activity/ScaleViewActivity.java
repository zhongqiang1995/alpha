package com.dy.alp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.azl.util.ScreenUtil;
import com.azl.view.MovePhotoView;
import com.dy.alp.R;
import com.facebook.drawee.view.SimpleDraweeView;

public class ScaleViewActivity extends AppCompatActivity {

    private MovePhotoView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_view);
        img = (MovePhotoView) findViewById(R.id.imga);

        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] arr = new int[2];
                img.getLocationInWindow(arr);
                int wX = arr[0];
                int wY = arr[1];
    
            }
        });

    }
}
