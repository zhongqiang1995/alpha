package com.azl.view.grid.select.adapter.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import com.azl.activity.photo.PhotoPreviewActivity;
import com.azl.util.ScreenUtil;
import com.azl.view.grid.image.util.FrescoTypeJumpUtil;
import com.azl.view.grid.select.GridSelectActionView;
import com.azl.view.grid.select.adapter.GridSelectFileViewAdapter;
import com.azl.view.grid.select.entity.GridSelectEntity;
import com.azl.view.helper.adapter.multiple.holder.ItemHolder;
import com.azl.view.helper.holder.CommonHolder;
import com.example.zhlib.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhong on 2018/3/7.
 */

public class GridSelectFileViewAdapterHolderImage extends ItemHolder<GridSelectFileViewAdapter, GridSelectEntity> {
    private GridSelectActionView mActionView;
    private SimpleDraweeView mPhoto;
    private MOnClickDeleteListener mOnClickDeleteListener;
    private MOnClickPhoto mOnClickPhoto;
    private int mPlaceholderImageResId;

    public GridSelectFileViewAdapterHolderImage(int type, int mPlaceholderImageResId) {
        super(type);
        this.mPlaceholderImageResId = mPlaceholderImageResId;
    }

    @Override
    public void initViewHolder(CommonHolder ch) {
        super.initViewHolder(ch);
        mOnClickPhoto = new MOnClickPhoto(ch.getItemView().getContext());
        mOnClickDeleteListener = new MOnClickDeleteListener();

        mActionView = (GridSelectActionView) ch.getItemView();
        mPhoto = ch.findViewById(R.id.imgPhoto);


        mPhoto.setOnClickListener(mOnClickPhoto);
        mActionView.setClickDeleteListener(mOnClickDeleteListener);
        Drawable drawable;
        if (mPlaceholderImageResId != 0) {
            drawable = ch.getItemView().getContext().getResources().getDrawable(mPlaceholderImageResId);
        } else {
            drawable = new ColorDrawable(Color.parseColor("#EDEDED"));
        }
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(ch.getItemView().getContext().getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(drawable)
                .build();
        mPhoto.setHierarchy(hierarchy);
    }

    @Override
    public void setItemView(GridSelectFileViewAdapter adapter, GridSelectEntity s, CommonHolder ch, int position) {
        mOnClickPhoto.setData(position, adapter);
        mOnClickDeleteListener.setData(position, s, adapter);
        FrescoTypeJumpUtil.DataType type = FrescoTypeJumpUtil.encodingType(s.getPath());
        String path = s.getPath();
        Uri uri;
        if (type == FrescoTypeJumpUtil.DataType.ASSETS) {
            uri = Uri.parse(FrescoTypeJumpUtil.formatAssetsPath(path));
        } else if (type == FrescoTypeJumpUtil.DataType.LOCAL) {
            uri = Uri.parse("file://" + path);
        } else if (type == FrescoTypeJumpUtil.DataType.URL) {
            uri = Uri.parse(path);
        } else if (type == FrescoTypeJumpUtil.DataType.SRC) {
            int id = Integer.valueOf(path);
            uri = Uri.parse("res:// /" + id);
        } else {
            uri = Uri.parse("");
        }


        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).
                setResizeOptions(new ResizeOptions(getChildWidth(adapter), getChildWidth(adapter))).build();

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder().setOldController(mPhoto.getController()).setImageRequest(request);
        mPhoto.setController(controller.build());
    }

    private int getChildWidth(GridSelectFileViewAdapter adapter) {
        return ScreenUtil.getScreenWidth(adapter.getContext()) / adapter.getColumn();
    }

    @Override
    public boolean isSelectItem(GridSelectFileViewAdapter adapter, int position, Object item) {
        if (item instanceof GridSelectEntity) {
            GridSelectEntity entity = (GridSelectEntity) item;
            if (entity.isImage()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.alpha_item_grid_view_image;
    }


    class MOnClickDeleteListener implements GridSelectActionView.OnClickDeleteListener {
        int mPosition;
        GridSelectEntity mEntity;
        GridSelectFileViewAdapter mAdapter;

        public void setData(int position, GridSelectEntity entity, GridSelectFileViewAdapter adapter) {
            this.mPosition = position;
            this.mEntity = entity;
            this.mAdapter = adapter;
        }

        @Override
        public void onDelete() {
            mAdapter.remove(mPosition);
        }
    }

    class MOnClickPhoto implements View.OnClickListener {

        private GridSelectFileViewAdapter mAdapter;
        private int mPosition;
        private Context mContext;

        public MOnClickPhoto(Context context) {
            this.mContext = context;
        }

        public void setData(int position, GridSelectFileViewAdapter adapter) {
            this.mPosition = position;
            this.mAdapter = adapter;
        }


        @Override
        public void onClick(View v) {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < mAdapter.getListData().size(); i++) {
                Object obj = mAdapter.getListData().get(i);
                if (obj instanceof GridSelectEntity) {
                    GridSelectEntity go = (GridSelectEntity) obj;
                    if (go.isImage()) {
                        list.add(go.getPath());
                    }
                }
            }
            if (list != null && !list.isEmpty()) {
                Intent intent = PhotoPreviewActivity.getJumpIntent(mContext, mPosition, Environment.getExternalStorageDirectory() + "/kuxiao/image", list.toArray());
                v.getContext().startActivity(intent);
            }

        }
    }

}
