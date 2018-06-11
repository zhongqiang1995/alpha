package com.azl.view.grid.image.adapter.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by zhong on 2018/3/5.
 */

public class GridImageViewAdapterHolder extends RecyclerView.ViewHolder {
    private SimpleDraweeView mPhoto;


    public SimpleDraweeView getPhoto() {
        return mPhoto;
    }

    public GridImageViewAdapterHolder(View itemView, int controller) {
        super(itemView);
        mPhoto = (SimpleDraweeView) itemView;
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(itemView.getContext().getResources());
        if (controller != 0) {
            GenericDraweeHierarchy hierarchy = builder
                    .setFadeDuration(300)
                    .setPlaceholderImage(controller)
                    .build();
            mPhoto.setHierarchy(hierarchy);
        }
    }
}
