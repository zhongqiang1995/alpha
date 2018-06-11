package com.dy.alp.activity;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.azl.view.ActionExecutionView;
import com.azl.view.PullToRefreshLayout;
import com.dy.alp.R;

public class SwitchLayoutTestActivity extends AppCompatActivity {


    private PullToRefreshLayout pullToLayout;
    private android.support.v4.widget.NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_layout_test);
        this.pullToLayout = findViewById(R.id.pullToLayout);
        this.scrollView = findViewById(R.id.scrollView);
        this.pullToLayout.setOnScrollStatusChangeListener(new ActionExecutionView.OnScrollStatusChangeListener() {
            @Override
            public void onScrollChange(int y) {
                Log.e("onScrollChange", ""+y);
            }
        });
    }
}
