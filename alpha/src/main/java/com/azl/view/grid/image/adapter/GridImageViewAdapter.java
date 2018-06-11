package com.azl.view.grid.image.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azl.view.grid.image.adapter.diff.GridImageViewDiffCallBack;
import com.azl.view.grid.image.adapter.holder.GridImageViewAdapterHolder;
import com.azl.view.grid.image.util.FrescoTypeJumpUtil;
import com.example.zhlib.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhong on 2018/3/5.
 */

public class GridImageViewAdapter extends RecyclerView.Adapter<GridImageViewAdapterHolder> {

    private int mPlaceholderImageId;
    private List<Object> mDataList;
    private Context mContext;


    public GridImageViewAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    public void setPlaceholderImage(int mPlaceholderImageId) {
        this.mPlaceholderImageId = mPlaceholderImageId;
    }

    public List<Object> getDataList() {
        return mDataList;
    }

    public void refresh(List list) {
        DiffUtil.DiffResult callBack = DiffUtil.calculateDiff(new GridImageViewDiffCallBack(mDataList, list), true);
        mDataList = list;
        callBack.dispatchUpdatesTo(this);

    }

    @Override
    public GridImageViewAdapterHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.alpha_item_grid_image, parent, false);
        GridImageViewAdapterHolder vh = new GridImageViewAdapterHolder(view, mPlaceholderImageId);
        return vh;
    }

    @Override
    public void onBindViewHolder(GridImageViewAdapterHolder holder, int position) {
        Object obj = mDataList.get(position);
        FrescoTypeJumpUtil.DataType type = FrescoTypeJumpUtil.encodingType(obj);
        SimpleDraweeView imgPhoto = holder.getPhoto();
        Uri uri;
        if (type == FrescoTypeJumpUtil.DataType.ASSETS) {
            uri = Uri.parse(FrescoTypeJumpUtil.formatAssetsPath(obj + ""));
        } else if (type == FrescoTypeJumpUtil.DataType.LOCAL) {
            uri = Uri.parse("file://" + obj);
        } else if (type == FrescoTypeJumpUtil.DataType.URL) {
            uri = Uri.parse(obj + "");
        } else if (type == FrescoTypeJumpUtil.DataType.SRC) {
            int id = (int) obj;
            uri = Uri.parse("res:// /" + id);
        } else {
            uri = Uri.parse("");
        }
        imgPhoto.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


}
