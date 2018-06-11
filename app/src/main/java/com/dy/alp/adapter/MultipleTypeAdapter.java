package com.dy.alp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.azl.view.helper.adapter.multiple.BaseMultipleTypeAdapter;
import com.azl.view.helper.adapter.multiple.holder.ItemHolder;
import com.azl.view.helper.holder.CommonHolder;
import com.dy.alp.R;

/**
 * Created by zhong on 2018/1/16.
 */

public class MultipleTypeAdapter extends BaseMultipleTypeAdapter {
    public MultipleTypeAdapter(Context context) {
        super(context);
    }

    @Override
    public ItemHolder[] getItemHolder() {
        return new ItemHolder[]{new Item(0),new Item2(1)};
    }

    class Item extends ItemHolder<MultipleTypeAdapter,Integer>{


        public Item(int type) {
            super(type);
        }

        @Override
        public void setItemView(MultipleTypeAdapter multipleTypeAdapter, Integer integer, CommonHolder ch, int position) {
            TextView tv=ch.findViewById(R.id.tvItem);
            tv.setText(""+integer);
        }

        @Override
        public boolean isSelectItem(MultipleTypeAdapter multipleTypeAdapter, int position, Object item) {
            return item instanceof Integer;
        }

        @Override
        public void initViewHolder(CommonHolder ch) {
            super.initViewHolder(ch);
            Log.e("Item","Item initViewHolder");
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_test;
        }

        @Override
        public int getItemType() {
            return 1;
        }
    }
    class Item2 extends ItemHolder<MultipleTypeAdapter,String>{

        private int count;

        public Item2(int type) {
            super(type);
        }

        @Override
        public void setItemView(MultipleTypeAdapter multipleTypeAdapter, String integer, CommonHolder ch, int position) {
            TextView tv=ch.findViewById(R.id.tvItem);
            tv.setText(""+integer);
        }

        @Override
        public boolean isSelectItem(MultipleTypeAdapter multipleTypeAdapter, int position, Object item) {
            return item instanceof String;
        }

        @Override
        public int getItemLayoutId() {
            return R.layout.item_test;
        }

        @Override
        public void initViewHolder(CommonHolder ch) {
            super.initViewHolder(ch);
            TextView tv=ch.findViewById(R.id.tvItem);
            count++;
            if(count==1){
                tv.setTextColor(Color.RED);
            }else{
                tv.setTextColor(Color.BLUE);
            }
            Log.e("Item2","Item2 initViewHolder");
        }
    }
}
