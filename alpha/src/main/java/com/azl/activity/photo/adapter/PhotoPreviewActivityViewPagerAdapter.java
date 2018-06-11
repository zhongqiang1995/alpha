package com.azl.activity.photo.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.azl.activity.photo.dialog.PhotoPreviewDialog;
import com.azl.bean.PermissionPackageBean;
import com.azl.handle.action.HandleMsg;
import com.azl.helper.AKXMarkList;
import com.azl.util.FileUtil;
import com.azl.util.ScreenUtil;
import com.azl.util.StreamHelper;
import com.azl.view.grid.image.util.FrescoTypeJumpUtil;
import com.example.zhlib.R;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.samples.zoomable.DoubleTapGestureListener;
import com.facebook.samples.zoomable.ZoomableDraweeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by zhong on 2018/4/2.
 */

public class PhotoPreviewActivityViewPagerAdapter extends PagerAdapter implements PhotoPreviewDialog.OnClickSaveListener {

    private Object[] mUrls;
    private Context mContext;
    private PhotoPreviewDialog mDialog;
    private ZoomableDraweeView mSelectDraweeView;
    private File mSaveImgFile;
    private String[] mPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};//需要的权限

    public PhotoPreviewActivityViewPagerAdapter(Object[] list, Context context, String mSaveImagePath) {
        this.mUrls = list;
        this.mContext = context;
        mSaveImgFile = new File(mSaveImagePath);
        if (!mSaveImgFile.exists()) {
            mSaveImgFile.mkdirs();

        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomableDraweeView zoomableDraweeView = getChildView();
        Uri uri = FrescoTypeJumpUtil.getUri(mUrls[position]);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(ScreenUtil.getScreenWidth(mContext), ScreenUtil.getScreenHeight(mContext)))
                .build();

        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(zoomableDraweeView.getController())
                .setCallerContext("Zoomable-PhotoPreviewActivityViewPagerAdapter")
                .build();

        zoomableDraweeView.setTag(uri);
        zoomableDraweeView.setController(controller);
        container.addView(zoomableDraweeView);
        zoomableDraweeView.requestLayout();

        return zoomableDraweeView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mSelectDraweeView == object) {
            mSelectDraweeView = null;
        }
        ZoomableDraweeView view = (ZoomableDraweeView) object;
        container.removeView(view);
        view.setController(null);
    }

    @Override
    public int getCount() {
        return mUrls.length;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }


    private ZoomableDraweeView getChildView() {

        ZoomableDraweeView draweeView = new ZoomableDraweeView(mContext);


        GenericDraweeHierarchyBuilder genericDraweeHierarchyBuilder = new GenericDraweeHierarchyBuilder(mContext.getResources());
        genericDraweeHierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        genericDraweeHierarchyBuilder.setProgressBarImage(new AutoRotateDrawable(mContext.getDrawable(R.drawable.alpha_img_loading), 2000));
        draweeView.setHierarchy(genericDraweeHierarchyBuilder.build());
        draweeView.setAllowTouchInterceptionWhileZoomed(false);
        draweeView.setIsLongpressEnabled(true);
        draweeView.setTapListener(new MDoubleTapGestureListener(draweeView));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        draweeView.setLayoutParams(params);

        return draweeView;
    }


    /**
     * 点击保存选项
     */
    @Override
    public void save() {
        if (mSelectDraweeView == null) return;

        boolean is = checkPermissions();
        if (!is) {
            //没有权限先请求权限
            Toast.makeText(mContext, mContext.getResources().getString(R.string.alpha_save_error_please_per), Toast.LENGTH_SHORT).show();
            HandleMsg.handleMark(AKXMarkList.MARK_PERMISSIONS, new PermissionPackageBean(mPermissions));
            mDialog.dismiss();
            return;
        }

        ZoomableDraweeView draweeView = mSelectDraweeView;
        Object tagObj = draweeView.getTag();
        Uri uri;
        if (tagObj == null || !(tagObj instanceof Uri)) {
            return;
        }
        uri = (Uri) tagObj;

        FrescoTypeJumpUtil.DataType type = FrescoTypeJumpUtil.decodingType(uri);
        String decodePath = FrescoTypeJumpUtil.decodingPath(uri);
        boolean isSuccess = false;
        try {
            if (type == FrescoTypeJumpUtil.DataType.URL) {
                isSuccess = copyUrlCache(uri);
            } else if (type == FrescoTypeJumpUtil.DataType.ASSETS) {
                isSuccess = copyAssets(decodePath);
            } else if (type == FrescoTypeJumpUtil.DataType.SRC) {
//                isSuccess = copyRes(decodePath);
                isSuccess = false;
            } else if (type == FrescoTypeJumpUtil.DataType.LOCAL) {
                isSuccess = copyFile(decodePath);
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.alpha_save_error), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, mContext.getResources().getString(R.string.alpha_save_error), Toast.LENGTH_LONG).show();
        }
        if (isSuccess) {
            //copy成功
            Toast.makeText(mContext, String.format(mContext.getResources().getString(R.string.alpha_photo_save_dir), mSaveImgFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
        } else {
            //copy失败
            if (type == FrescoTypeJumpUtil.DataType.SRC) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.alpha_save_img_format_local_error), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.alpha_save_error), Toast.LENGTH_SHORT).show();
            }
        }
        mDialog.dismiss();
    }

    /**
     * 拷贝本地文件中的内容
     *
     * @param decodePath 本地路径
     */
    private boolean copyFile(String decodePath) {
        String suffixName = FileUtil.getSuffixName(decodePath);

        File targetFile = new File(decodePath);
        File outFile = newOutFile(suffixName);

        return FileUtil.copy(outFile, targetFile);
    }

    /**
     * copy资源文件夹中的内容
     *
     * @param decodePath 资源id
     */
    private boolean copyRes(String decodePath) {
        boolean isSuccess = false;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {

            int id = Integer.valueOf(decodePath);
            File outFile = newOutFile("");
            inputStream = mContext.getResources().openRawResource(id);
            outputStream = new FileOutputStream(outFile);

            isSuccess = FileUtil.copy(inputStream, outputStream, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            StreamHelper.close(inputStream, outputStream);
        }
        return isSuccess;
    }

    /**
     * copy assets文件夹中的问价
     *
     * @param decodePath 文件路径
     */
    private boolean copyAssets(String decodePath) {
        boolean isSuccess = false;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            String suffixName = FileUtil.getSuffixName(decodePath);
            File outFile = newOutFile(suffixName);
            File inputFile = new File(decodePath);
            inputStream = mContext.getResources().getAssets().open(inputFile.getName());
            outputStream = new FileOutputStream(outFile);
            isSuccess = FileUtil.copy(inputStream, outputStream, false);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            StreamHelper.close(inputStream, outputStream);
        }
        return isSuccess;
    }

    /**
     * 当uri是远程服务器地址时用这个方法保存
     */
    private boolean copyUrlCache(Uri uri) {
        boolean isSuccess = false;
        FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainFileCache().getResource(new SimpleCacheKey(uri.toString()));
        if (resource != null) {
            File sourceFile = resource.getFile();
            String suffixName = FileUtil.getSuffixName(uri.toString());
            File targetFile = newOutFile(suffixName);
            isSuccess = FileUtil.copy(targetFile, sourceFile);

        }

        return isSuccess;
    }

    private File newOutFile(String suffixName) {
        if (TextUtils.isEmpty(suffixName)) {
            return new File(mSaveImgFile, System.currentTimeMillis()+"");
        } else {
            return new File(mSaveImgFile, System.currentTimeMillis() + "." + suffixName);
        }
    }


    public class MDoubleTapGestureListener extends DoubleTapGestureListener {

        private ZoomableDraweeView mZoomableDraweeView;

        public MDoubleTapGestureListener(ZoomableDraweeView zoomableDraweeView) {
            super(zoomableDraweeView);
            this.mZoomableDraweeView = zoomableDraweeView;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            //点击图片
            if (mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
            return super.onSingleTapConfirmed(e);

        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            Object tagObj = mZoomableDraweeView.getTag();

            if (tagObj == null || !(tagObj instanceof Uri)) {
                return;
            }
            Uri uri = (Uri) tagObj;
            //获取bitmap缓存判断图片是否加载完成
            boolean inMemoryCache = false;

            FrescoTypeJumpUtil.DataType type = FrescoTypeJumpUtil.decodingType(uri);
            if (type == FrescoTypeJumpUtil.DataType.ASSETS || type == FrescoTypeJumpUtil.DataType.SRC) {
                inMemoryCache = true;
            } else if (type == FrescoTypeJumpUtil.DataType.LOCAL) {
                String path = FrescoTypeJumpUtil.decodingPath(uri);
                File localFile = new File(path);
                if (localFile.exists()) {
                    inMemoryCache = true;
                }
            } else if (type == FrescoTypeJumpUtil.DataType.URL) {
                FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainFileCache().getResource(new SimpleCacheKey(uri.toString()));
                if (resource != null && resource.getFile() != null && resource.getFile().exists()) {
                    inMemoryCache = true;
                }
            }


            if (inMemoryCache) {
                showBottomDialog();
            }


        }

        private void showBottomDialog() {
            //长按图片
            mSelectDraweeView = mZoomableDraweeView;
            if (mDialog == null) {
                mDialog = new PhotoPreviewDialog(mContext);
                mDialog.setOnClickSaveListener(PhotoPreviewActivityViewPagerAdapter.this);
            }
            mDialog.show();
        }
    }


    /**
     * 判断是否有读取sd的权限
     *
     * @return
     */
    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
