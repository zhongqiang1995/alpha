package com.azl.view.grid.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.azl.activity.photo.PhotoPreviewActivity;
import com.azl.util.ScreenUtil;
import com.azl.view.grid.image.util.FrescoTypeJumpUtil;
import com.azl.view.grid.select.adapter.GridSelectFileViewAdapter;
import com.example.zhlib.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.List;

/**
 * Created by zhong on 2018/3/5.
 */

public class GridImageView extends android.support.v7.widget.GridLayout {


    private List<Object> mDataList;
    private int mSpec;

    public GridImageView(Context context) {
        this(context, null);
    }

    public GridImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColumnCount(3);
        mSpec = ScreenUtil.dip2px(getContext(), 5);
    }


    public void setDataIds(List list) {
        this.mDataList = list;
        setupData();


    }

    private void setupData() {

        int columnCount = getColumnCount();

        if (mDataList == null || mDataList.isEmpty()) {
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                childView.setVisibility(View.GONE);
            }
            return;
        } else {
            int count = mDataList.size();

            if (count > getChildCount()) {
                while ((count > getChildCount()) || getChildCount() < columnCount) {
                    addView(getPhotoLayout());
                }
            }
            if (count < getChildCount()) {
                for (int i = count; i < getChildCount(); i++) {
                    if (i < columnCount) {
                        getChildAt(i).setVisibility(View.INVISIBLE);
                    } else {
                        getChildAt(i).setVisibility(View.GONE);
                    }
                }
            }
        }

        int maxRow = 0;
        float f = mDataList.size() / (columnCount * 1.0f);
        if (f > (int) f) {
            maxRow = (int) f + 1;
        } else {
            maxRow = (int) f;
        }
        for (int i = 0; i < mDataList.size(); i++) {
            int row;
            float fRow = ((i + 1) / (columnCount * 1.0f));
            if (fRow > (int) fRow) {
                row = (int) (fRow + 1);
            } else {
                row = (int) fRow;
            }
            if (row < 0) {
                row = 1;
            }


            SimpleDraweeView view = (SimpleDraweeView) getChildAt(i);
            GridLayout.LayoutParams params = (LayoutParams) view.getLayoutParams();
            if ((i + 1) % columnCount != 0 && (i != mDataList.size() - 1 || i + 1 > columnCount)) {
                params.rightMargin = mSpec;
            } else {
                params.rightMargin = 0;
            }
            if (row != maxRow) {
                params.bottomMargin = mSpec;
            } else {
                params.bottomMargin = 0;
            }
            view.setLayoutParams(params);
            Object obj = mDataList.get(i);
            setImageData(view, obj);
            view.setVisibility(View.VISIBLE);

            OnClickItem onClickItem = (OnClickItem) view.getTag();
            onClickItem.position = i;

        }

    }

    private void setImageData(SimpleDraweeView view, Object obj) {
        FrescoTypeJumpUtil.DataType type = FrescoTypeJumpUtil.encodingType(obj);
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
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).
                setResizeOptions(new ResizeOptions(getChildWidth(), getChildWidth())).build();

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder().setOldController(view.getController()).setImageRequest(request);

        view.setController(controller.build());
    }

    private int getChildWidth() {
        return ScreenUtil.getScreenWidth(getContext()) / getColumnCount();
    }

    private SimpleDraweeView getPhotoLayout() {
        SimpleDraweeView view = (SimpleDraweeView) LayoutInflater.from(getContext()).inflate(R.layout.alpha_item_grid_image, null);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f)
                , GridLayout.spec(GridLayout.UNDEFINED, 1f));
        view.setLayoutParams(params);


        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(view.getContext().getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(new ColorDrawable(Color.parseColor("#EDEDED")))//设置默认图
                .build();
        view.setHierarchy(hierarchy);
        OnClickItem onClickItem = new OnClickItem();
        view.setOnClickListener(onClickItem);
        view.setTag(onClickItem);

        return view;
    }


    class OnClickItem implements OnClickListener {


        private int position;

        @Override
        public void onClick(View v) {
            Intent intent = PhotoPreviewActivity.getJumpIntent(getContext(), position, Environment.getExternalStorageDirectory() + "/kuxiao/image", mDataList.toArray());
            v.getContext().startActivity(intent);
        }
    }
}
