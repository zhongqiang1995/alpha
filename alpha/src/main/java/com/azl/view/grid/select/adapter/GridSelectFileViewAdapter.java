package com.azl.view.grid.select.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;

import com.azl.view.grid.select.GridSelectFileView;
import com.azl.view.grid.select.adapter.diff.GridSelectFileViewDiff;
import com.azl.view.grid.select.adapter.holder.GridSelectFileViewAdapterHolderAdd;
import com.azl.view.grid.select.adapter.holder.GridSelectFileViewAdapterHolderImage;
import com.azl.view.grid.select.adapter.holder.GridSelectFileViewAdapterHolderOther;
import com.azl.view.grid.select.entity.GridAddEntity;
import com.azl.view.grid.select.entity.GridSelectEntity;
import com.azl.view.helper.adapter.multiple.BaseMultipleTypeAdapter;
import com.azl.view.helper.adapter.multiple.holder.ItemHolder;
import com.example.zhlib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhong on 2018/3/7.
 */

public class GridSelectFileViewAdapter extends BaseMultipleTypeAdapter {

    private int mMaxCount;
    private String mOpenType;
    private int mAddImageResId;
    private int mPlaceHolderImageResId;
    private int mResultCode;
    private int mColumn;

    public GridSelectFileViewAdapter(Context context, int maxCount, int resultCode, int column) {
        super(context);
        this.mMaxCount = maxCount;
        this.mResultCode = resultCode;
        this.mOpenType = GridSelectFileView.OPEN_TYPE_IMAGE;
        this.mColumn = column;
        mAddImageResId = R.drawable.alpha_img_rect_add;

    }

    @Override
    public ItemHolder[] getItemHolder() {
        return new ItemHolder[]{new GridSelectFileViewAdapterHolderAdd(0, mAddImageResId, mResultCode),
                new GridSelectFileViewAdapterHolderImage(1, mPlaceHolderImageResId),
                new GridSelectFileViewAdapterHolderOther(2)};
    }

    public void setPlaceHolderImageResId(int mPlaceHolderImageResId) {
        this.mPlaceHolderImageResId = mPlaceHolderImageResId;
    }

    public void setColumn(int mColumn) {
        this.mColumn = mColumn;
    }

    public int getColumn() {
        return mColumn;
    }

    public int getPlaceHolderImageResId() {
        return mPlaceHolderImageResId;
    }

    public int getAddImageResId() {
        return mAddImageResId;
    }

    public void setAddImageResId(int resId) {
        this.mAddImageResId = resId;
    }

    public void setMaxCount(int mMaxCount) {
        this.mMaxCount = mMaxCount;
        format();

    }

    public String getOpenType() {
        return mOpenType;
    }

    public void setOpenType(String mOpenType) {
        this.mOpenType = mOpenType;
    }

    public void addObj(String path) {
        GridSelectEntity entity = new GridSelectEntity(path);
        if (getListData().size() < mMaxCount) {
            diffInsertItem(getListData().size() - 1, entity);
        } else {
            getListData().add(entity);
            format();
        }
    }

    public void remove(int position) {
        if (position < getListData().size()) {
            if (getListData().get(position) instanceof GridAddEntity) {
                return;
            }
        }
        if (getListData().size() >= mMaxCount) {
            diffRemoveItem(position);
            format();
        } else {
            diffRemoveItem(position);
        }

    }

    public void removeRange(int start, int count) {
        int sStart = 0;
        int sCount = 0;
        if (sStart < 0 || count < 0) {
            return;
        }
        if (start >= getListData().size()) {
            return;
        }
        sStart = start;
        if (count + sStart >= getListData().size()) {
            sCount = getListData().size() - sStart;
        } else {
            sCount = count;
        }
        if (sCount < 0) {
            sCount = 0;
        }
        for (int i = (sStart + sCount) - 1; i >= sStart; i--) {
            Object obj = getListData().get(i);
            if (obj instanceof GridAddEntity) {
                sCount = i - sStart;
                break;
            }
            count++;
        }
        if (getListData().size() >= mMaxCount) {
            diffRemoveItems(sStart, sCount);
            format();
        } else {
            diffRemoveItems(sStart, sCount);
        }

    }

    public void setData(List<String> list) {
        if (getListData().size() == 1 && getListData().get(0) instanceof GridAddEntity) {
            addData(list);
            return;
        }
        getListData().clear();
        for (int i = 0; i < list.size(); i++) {
            String path = list.get(i);
            if (path != null) {
                getListData().add(new GridSelectEntity(path));
            }
            if (i >= mMaxCount - 1) {
                break;
            }
        }
        notifyDataSetChanged();
        format();
    }

    public void addData(List<String> list) {
        List<Object> newDataList = new ArrayList<>();
        int oldSize = getListData().size();
        int count = mMaxCount - oldSize;
        boolean isAdd = false;
        for (int i = 0; i < getListData().size(); i++) {
            if (getListData().get(i) instanceof GridAddEntity) {
                if (oldSize + list.size() > mMaxCount) {
                    count = mMaxCount - (oldSize - 1);
                }
                isAdd = true;
                break;
            }
        }

        for (int i = 0; i < count; i++) {
            if (list.size() - 1 < i) {
                break;
            }
            String path = list.get(i);
            GridSelectEntity selectEntity = new GridSelectEntity(path);
            newDataList.add(selectEntity);
        }

        if (isAdd) {
            diffInsertItems(getListData().size() - 1, newDataList);
        } else {
            diffInsertItems(getListData().size(), newDataList);
        }
        format();
    }


    private void format() {
        if (getListData().size() > mMaxCount) {
            for (int i = getListData().size() - 1; i >= 0; i--) {
                Object obj = getListData().get(i);
                if (obj instanceof GridAddEntity) {
                    diffRemoveItems(i, 1);
                    break;
                }
            }


            if (getListData().size() > mMaxCount) {
                diffRemoveItems(mMaxCount, getListData().size() - mMaxCount);
            }
        } else if (getListData().size() < mMaxCount) {
            boolean isExistsAdd = false;
            for (int i = getListData().size() - 1; i >= 0; i--) {
                Object entity = getListData().get(i);
                if (entity instanceof GridAddEntity) {
                    isExistsAdd = true;
                    break;
                }
            }
            if (!isExistsAdd) {
                diffInsertItem(getListData().size(), new GridAddEntity());
            }

        }
    }

    @Override
    protected DiffUtil.DiffResult getDiffUtil(List<Object> newList, List<Object> oldList) {
        return DiffUtil.calculateDiff(new GridSelectFileViewDiff(oldList, newList), true);
    }
}
