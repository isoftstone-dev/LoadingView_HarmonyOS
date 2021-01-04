package com.isoftstone.loadingview;

import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Color;
import ohos.agp.utils.Rect;

public class LoadingRanderCircle extends LoadingRander {
    //private static final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private static final long ANIMATION_DURATION = 2500;

    private static final int DEFAULT_CIRCLE_COUNT = 5;

    private static final float DEFAULT_BALL_RADIUS = 7.5f;
    private static final float DEFAULT_WIDTH = 10.0f * 11;
    private static final float DEFAULT_HEIGHT = 10.0f * 5;
    private static final float DEFAULT_STROKE_WIDTH = 1.5f;

    private static final Color DEFAULT_COLOR = Color.BLUE;

    private final Paint mPaint = new Paint();

    private int mColor;

    private int mSwapIndex;
    private int mBallCount;

    private float mBallSideOffsets;
    private float mBallCenterY;
    private float mBallRadius;
    private float mBallInterval;
    private float mSwapBallOffsetX;
    private float mSwapBallOffsetY;
    private float mASwapThreshold;

    private float mStrokeWidth;

    public LoadingRanderCircle()  {
        super();
        init();
        adjustParams();
        setupPaint();
    }

    private void init() {
        mWidth = DensityUtil.dip2px(null, DEFAULT_WIDTH);
        mHeight = DensityUtil.dip2px(null, DEFAULT_HEIGHT);
        mBallRadius = DensityUtil.dip2px(null, DEFAULT_BALL_RADIUS);
        mStrokeWidth = DensityUtil.dip2px(null, DEFAULT_STROKE_WIDTH);

        //mColor = DEFAULT_COLOR;
        //mDuration = ANIMATION_DURATION;
        mBallCount = DEFAULT_CIRCLE_COUNT;

        mBallInterval = mBallRadius;
    }

    private void adjustParams() {
        mBallCenterY = mHeight ;
        mBallSideOffsets = (mWidth - mBallRadius * 2 * mBallCount - mBallInterval * (mBallCount - 1)) / 2.0f;

        mASwapThreshold = 1.0f / mBallCount;
    }

    private void setupPaint() {
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void draw(Canvas canvas, Rect bounds) {
        int saveCount = canvas.save();

        computeRender(mProgress);

        for (int i = 0; i < mBallCount; i++) {
            if (i == mSwapIndex) {
                mPaint.setStyle(Paint.Style.FILL_STYLE);
                canvas.drawCircle(mBallSideOffsets + mBallRadius * (i * 2 + 1) + i * mBallInterval + mSwapBallOffsetX
                        , mBallCenterY - mSwapBallOffsetY, mBallRadius, mPaint);
            } else if (i == (mSwapIndex + 1) % mBallCount) {
                mPaint.setStyle(Paint.Style.STROKE_STYLE);
                canvas.drawCircle(mBallSideOffsets + mBallRadius * (i * 2 + 1) + i * mBallInterval - mSwapBallOffsetX
                        , mBallCenterY + mSwapBallOffsetY, mBallRadius - mStrokeWidth / 2, mPaint);
            } else {
                mPaint.setStyle(Paint.Style.STROKE_STYLE);
                canvas.drawCircle(mBallSideOffsets + mBallRadius * (i * 2 + 1) + i * mBallInterval, mBallCenterY
                        , mBallRadius - mStrokeWidth / 2, mPaint);
            }
        }

        canvas.restoreToCount(saveCount);
    }


    protected void computeRender(float renderProgress) {
        mSwapIndex = (int) (renderProgress / mASwapThreshold);

        // Swap trace : x^2 + y^2 = r ^ 2
        float swapTraceProgress = (
                (renderProgress - mSwapIndex * mASwapThreshold) / mASwapThreshold);

        float swapTraceRadius = mSwapIndex == mBallCount - 1
                ? (mBallRadius * 2 * (mBallCount - 1) + mBallInterval * (mBallCount - 1)) / 2
                : (mBallRadius * 2 + mBallInterval) / 2;

        // Calculate the X offset of the swap ball
        mSwapBallOffsetX = mSwapIndex == mBallCount - 1
                ? -swapTraceProgress * swapTraceRadius * 2
                : swapTraceProgress * swapTraceRadius * 2;

        // if mSwapIndex == mBallCount - 1 then (swapTraceRadius, swapTraceRadius) as the origin of coordinates
        // else (-swapTraceRadius, -swapTraceRadius) as the origin of coordinates
        float xCoordinate = mSwapIndex == mBallCount - 1
                ? mSwapBallOffsetX + swapTraceRadius
                : mSwapBallOffsetX - swapTraceRadius;

        // Calculate the Y offset of the swap ball
        mSwapBallOffsetY =  (float) (mSwapIndex % 2 == 0 && mSwapIndex != mBallCount - 1
                ? Math.sqrt(Math.pow(swapTraceRadius, 2.0f) - Math.pow(xCoordinate, 2.0f))
                : -Math.sqrt(Math.pow(swapTraceRadius, 2.0f) - Math.pow(xCoordinate, 2.0f)));

    }

}
