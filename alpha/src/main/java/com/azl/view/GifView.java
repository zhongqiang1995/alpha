package com.azl.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.zhlib.R;

import java.io.File;
import java.io.InputStream;

/**
 * Created by zhong on 2017/2/23.
 */

public class GifView extends ImageView {
    private Movie mMovie;
    //帧的时间间距
    private static final int SPACING_TIME = 16;
    private int mGifHeight;
    private int mGifWidth;
    //gif图片的时间长
    private int mDuration;
    //是否循环播放
    private boolean mIsCycle;
    //是否正在播放
    private boolean mIsPlay = true;
    //记录播放进度的时间
    private int mStartTime;
    //当前gif播放帧的时间
    private int mCurrentFrameTime;


    public GifView(Context context) {
        super(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GifView);
        if (ta != null) {
            mIsCycle = ta.getBoolean(R.styleable.GifView_isCycle, false);
            int srcId = ta.getResourceId(R.styleable.GifView_playSrc, -1);
            mIsPlay = ta.getBoolean(R.styleable.GifView_play, false);
            if (srcId != -1) {
                InputStream input = context.getResources().openRawResource(srcId);
                Movie movie = Movie.decodeStream(input);
                if (movie != null) {
                    init(movie);
                } else {
                    setImageResource(srcId);
                }

            }
            ta.recycle();
        }
    }

    public int getDuration() {
        return mDuration;
    }

    public void setIsCycle(boolean mIsCycle) {
        this.mIsCycle = mIsCycle;
    }

    public boolean getIsCycle() {
        return this.mIsCycle;
    }

    public boolean getIsPlay() {
        return mIsPlay;
    }

    private void setPlay(boolean isPlay) {
        if (isPlay == mIsPlay) return;
        mIsPlay = isPlay;
        if (mIsPlay) {
            if (mMovie == null) return;
            invalidate();
        }
    }

    private void init(Movie movie) {
        resetStatus();
        this.mMovie = movie;
        if (mMovie != null) {
            mGifHeight = mMovie.height();
            mGifWidth = mMovie.width();
            mDuration = mMovie.duration();
        }
        requestLayout();
        invalidate();
    }

    public void setGifFile(File gifFile) {
        if (gifFile == null || !gifFile.exists()) return;
        setGifPath(gifFile.getAbsolutePath());
    }

    public void setGifPath(String path) {
        Movie movie = Movie.decodeFile(path);
        if (movie == null) {
            try {
                setImageURI(Uri.parse(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            init(movie);
        }
    }

    public void setGifRes(int drawableId) {
        InputStream input = null;
        try {
            input = getResources().openRawResource(drawableId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        if (input == null) return;
        Movie movie = Movie.decodeStream(input);
        if (movie == null) return;
        init(movie);

    }


    public void setGifInputStream(InputStream input) {
        Movie movie = Movie.decodeStream(input);

        if (movie == null) return;
        init(movie);
    }

    public void setGifBytes(byte[] bytes, int offset, int end) {
        Movie movie = Movie.decodeByteArray(bytes, offset, end);
        if (movie == null) return;
        init(movie);
    }

    private void resetStatus() {
        mCurrentFrameTime = 0;
        mStartTime = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMovie != null) {
            int y;
            int x;
            int h = getMeasuredHeight() - mGifHeight;
            int w = getMeasuredWidth() - mGifWidth;
            y = h / 2;
            x = w / 2;
            if (!mIsPlay) {
                mMovie.setTime(mCurrentFrameTime);
                mMovie.draw(canvas, x, y);
                return;
            }
            boolean is = play(canvas, x, y);
            if (!is) {
                invalidate();
            } else {
                if (mIsCycle) {
                    mStartTime = 0;
                    mCurrentFrameTime = 0;
                    invalidate();
                } else {
                    mStartTime = 0;
                    mCurrentFrameTime = 0;
                    mIsPlay = false;
                }
            }
        } else {
            super.onDraw(canvas);
        }
    }

    public void play() {
        setPlay(true);
    }

    public void stop() {
        setPlay(false);
    }

    public boolean play(Canvas canvas, int x, int y) {
        boolean isEnd = false;
        mCurrentFrameTime = mStartTime;
        mMovie.setTime(mStartTime);
        mMovie.draw(canvas, x, y);
        if (mStartTime >= mDuration) {
            isEnd = true;
        }
        mStartTime += SPACING_TIME;
        return isEnd;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mMovie != null) {
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (widthMode == MeasureSpec.AT_MOST) {
                widthSize = Math.min(mGifWidth, widthSize);
            }

            if (heightMode == MeasureSpec.AT_MOST) {
                heightSize = Math.min(mGifHeight, heightSize);
            }
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
