package com.dy.alp.activity;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.azl.base.StatusActivity;
import com.azl.view.grid.image.GridImageView;
import com.azl.view.helper.adapter.multiple.BaseMultipleTypeAdapter;
import com.azl.view.helper.adapter.multiple.holder.ItemHolder;
import com.azl.view.helper.holder.CommonHolder;
import com.dy.alp.R;

import java.util.ArrayList;
import java.util.List;

public class GridImageViewTestAcivity extends StatusActivity {

    private GridImageView mGridView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_image_view_test_acivity);
        mGridView = findViewById(R.id.gridView);
        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BaseMultipleTypeAdapter adapter = new BaseMultipleTypeAdapter(this) {
            @Override
            public ItemHolder[] getItemHolder() {
                return new ItemHolder[]{new ItemHolder(0) {
                    private GridImageView imgView;

                    @Override
                    public void initViewHolder(CommonHolder ch) {
                        super.initViewHolder(ch);
                        imgView = ch.findViewById(R.id.gridView);
                    }

                    @Override
                    public void setItemView(Object o, Object o2, CommonHolder ch, int position) {
                        List<Object> list = new ArrayList<>();
                        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
                        if(position%3==0){
                            list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
                        }
                        if(position%2==0){
                            list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
                            list.add("file:///android_asset/222.jpg");
                        }

                        imgView.setDataIds(list);
                    }

                    @Override
                    public boolean isSelectItem(Object o, int position, Object item) {
                        return true;
                    }

                    @Override
                    public int getItemLayoutId() {
                        return R.layout.item_test_recycler_grid_view;
                    }
                }};
            }
        };

        recyclerView.setAdapter(adapter);
        ArrayList lists = new ArrayList();
        for (int i = 0; i < 100; i++) {
            lists.add(i);
        }
        adapter.refresh(lists);


        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
//        list.add(R.mipmap.ic_launcher);
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
//        list.add("file:///android_asset/222.jpg");

        mGridView.setDataIds(list);
    }

    public void bt1(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        mGridView.setDataIds(list);
    }

    public void bt2(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        mGridView.setDataIds(list);

    }

    public void bt3(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        mGridView.setDataIds(list);
    }

    public void bt4(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        mGridView.setDataIds(list);
    }

    public void bt5(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        mGridView.setDataIds(list);
    }

    public void bt6(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        mGridView.setDataIds(list);
    }

    public void bt7(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        mGridView.setDataIds(list);
    }

    public void bt8(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        mGridView.setDataIds(list);
    }

    public void bt9(View view) {
        List<Object> list = new ArrayList<>();
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://b.hiphotos.baidu.com/image/pic/item/359b033b5bb5c9eab4279cc5d939b6003bf3b3c4.jpg");
        list.add("http://d.hiphotos.baidu.com/image/pic/item/8601a18b87d6277fcdb9b01d24381f30e924fc68.jpg");
        mGridView.setDataIds(list);
    }

}
