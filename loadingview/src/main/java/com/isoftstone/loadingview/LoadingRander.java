package com.isoftstone.loadingview;
import ohos.agp.render.Canvas;
import ohos.agp.utils.Rect;


public class LoadingRander {

    protected float mProgress;
    protected float mWidth;
    protected float mHeight;
    protected float mTextSize;

    public LoadingRander() {

    }

    // 设置进度
    public void setProgess(float progress) {
        mProgress = progress;
    }

    // 绘制
    protected void draw(Canvas canvas, Rect bounds) {
        return;
    }
}
