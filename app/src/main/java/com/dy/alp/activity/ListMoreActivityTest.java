package com.dy.alp.activity;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azl.view.SwipeRecyclerView;
import com.azl.view.adapter.LoadMoreRecyclerAdapter;
import com.dy.alp.R;

import java.util.ArrayList;
import java.util.List;

public class ListMoreActivityTest extends AppCompatActivity {

    SwipeRecyclerView mRecyclerView;
    List<String> mList = new ArrayList<>();
    MAdapter adapter = new MAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_more_test);
        for (int i = 0; i < 10; i++) {
            mList.add(i + "");
        }

        mRecyclerView = (SwipeRecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLoadMoreEnable(true);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onLoadMore() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < count; i++) {
                            mList.add(mList.size() + "");
                        }
                        adapter.notifyItemRangeChanged(mList.size() - count, mList.size());
                        c++;
                        if (c == 5) {
                            mRecyclerView.onNoMore("没有更多了");
                        } else {
                            mRecyclerView.complete();

                        }
                    }
                }, 2000);

            }
        });
        mRecyclerView.setOnRefreshListener(new SwipeRecyclerView.OnRefreshListener() {
            @Override
            public void onRefreshing() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int oldCount = mList.size();
                        mList.clear();
                        adapter.notifyItemRangeRemoved(0, oldCount);
                        for (int i = 0; i < 10; i++) {
                            mList.add(i + "");
                        }
                        adapter.notifyItemRangeInserted(0, mList.size());
                        mRecyclerView.complete();
                        mRecyclerView.setLoadMoreEnable(true);
                    }
                }, 2000);

            }
        });

    }

    int count = 110;

    int c = 1;

    class MAdapter extends RecyclerView.Adapter<MVh> {


        @Override
        public MVh onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ListMoreActivityTest.this).inflate(R.layout.item, null, false);

            return new MVh(view);
        }

        @Override
        public void onBindViewHolder(MVh holder, final int position) {
            holder.tv.setText(mList.get(position) + "");

            holder.position = position;
            holder.adapter = this;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    class MVh extends RecyclerView.ViewHolder {

        TextView tv;
        int position;
        MAdapter adapter;

        public MVh(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tvNum);
            itemView.setOnClickListener(new OnClick());
        }

        class OnClick implements View.OnClickListener {

            @Override
            public void onClick(View view) {
                if (position < mList.size()) {
                    mList.remove(position);

                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, mList.size() - position);
                }

            }
        }
    }

}
