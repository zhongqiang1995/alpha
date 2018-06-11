package com.dy.alp.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.azl.view.ActionExecutionView;
import com.azl.view.PullToRefreshLayout;
import com.dy.alp.R;
import com.dy.alp.adapter.MultipleTypeAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListTestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MultipleTypeAdapter adapter;

    private PullToRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);
        refreshLayout= (PullToRefreshLayout) findViewById(R.id.refreshLayout);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MultipleTypeAdapter(this);
        recyclerView.setAdapter(adapter);
        List<Integer> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        adapter.refresh(list);
        final List<String> list1=new ArrayList<>();
        for (int i = 21; i <30 ; i++) {
            list1.add(i+"");
        }

        refreshLayout.setOnScrollStatusListener(new ActionExecutionView.OnScrollStatusListener() {
            @Override
            public void dropDown() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("refreshLayout","dropDown");
                        List<Integer> list=new ArrayList<>();
                        for (int i = 0; i < 22; i++) {
                            list.add(i);
                        }
                        adapter.refresh(list);
                        refreshLayout.restoreLocation();
                    }
                },2000);
            }

            @Override
            public void pullTop() {
                Log.e("refreshLayout","pullTop");

                List<Integer> list=new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    list.add(i);
                }
                adapter.next(list);
                refreshLayout.restoreLocation();
            }
        });

    }
    int c=100;
    public void onClick(View view){
        c++;
        List<Object> list=new ArrayList<>();
        list.add("110");
        list.add("111");
        list.add("112");
        list.add("113");
        list.add("114");

        adapter.diffInsertItems(1011,list);
//            adapter.diffRemoveItems(0,2);
    }
}
