package com.isoftstone.loadingview;

import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.Path;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.agp.utils.RectFloat;
import ohos.app.Context;

public class LoadingRanderBalloon extends LoadingRander {
    private static final String PERCENT_SIGN = "%";

    //private static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    private static final float START_INHALE_DURATION_OFFSET = 0.4f;

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 150.0f;
    private static final float DEFAULT_STROKE_WIDTH = 2.0f;
    private static final float DEFAULT_GAS_TUBE_WIDTH = 48;
    private static final float DEFAULT_GAS_TUBE_HEIGHT = 20;
    private static final float DEFAULT_CANNULA_WIDTH = 13;
    private static final float DEFAULT_CANNULA_HEIGHT = 37;
    private static final float DEFAULT_CANNULA_OFFSET_Y = 3;
    private static final float DEFAULT_CANNULA_MAX_OFFSET_Y = 15;
    private static final float DEFAULT_PIPE_BODY_WIDTH = 16;
    private static final float DEFAULT_PIPE_BODY_HEIGHT = 36;
    private static final float DEFAULT_BALLOON_WIDTH = 38;
    private static final float DEFAULT_BALLOON_HEIGHT = 48;
    private static final float DEFAULT_RECT_CORNER_RADIUS = 2;

    private static final int DEFAULT_BALLOON_COLOR = Color.getIntColor("#ffF3C211");
    private static final int DEFAULT_GAS_TUBE_COLOR = Color.getIntColor("#ff174469");
    private static final int DEFAULT_PIPE_BODY_COLOR = Color.getIntColor("#aa2369B1");
    private static final int DEFAULT_CANNULA_COLOR = Color.getIntColor("#ff174469");

    private static final float DEFAULT_TEXT_SIZE = 7.0f;

    private static final long ANIMATION_DURATION = 3333;

    private final Paint mPaint = new Paint();
    private final RectFloat mCurrentBounds = new RectFloat();
    private final RectFloat mGasTubeBounds = new RectFloat();
    private final RectFloat mPipeBodyBounds = new RectFloat();
    private final RectFloat mCannulaBounds = new RectFloat();
    private final RectFloat mBalloonBounds = new RectFloat();

    private final Rect mProgressBounds = new Rect();

    private float mTextSize;

    private String mProgressText;

    private float mGasTubeWidth;
    private float mGasTubeHeight;
    private float mCannulaWidth;
    private float mCannulaHeight;
    private float mCannulaMaxOffsetY;
    private float mCannulaOffsetY;
    private float mPipeBodyWidth;
    private float mPipeBodyHeight;
    private float mBalloonWidth;
    private float mBalloonHeight;
    private float mRectCornerRadius;
    private float mStrokeWidth;

    private Color mBalloonColor;
    private Color mGasTubeColor;
    private Color mCannulaColor;
    private Color mPipeBodyColor;

    public LoadingRanderBalloon() {
        super();
        init();
        setupPaint();
    }

    private void init() {
        mTextSize = DensityUtil.dip2px(null, DEFAULT_TEXT_SIZE);

        mWidth = DensityUtil.dip2px(null, DEFAULT_WIDTH);
        mHeight = DensityUtil.dip2px(null, DEFAULT_HEIGHT);
        mStrokeWidth = DensityUtil.dip2px(null, DEFAULT_STROKE_WIDTH);

        mGasTubeWidth = DensityUtil.dip2px(null, DEFAULT_GAS_TUBE_WIDTH);
        mGasTubeHeight = DensityUtil.dip2px(null, DEFAULT_GAS_TUBE_HEIGHT);
        mCannulaWidth = DensityUtil.dip2px(null, DEFAULT_CANNULA_WIDTH);
        mCannulaHeight = DensityUtil.dip2px(null, DEFAULT_CANNULA_HEIGHT);
        mCannulaOffsetY = DensityUtil.dip2px(null, DEFAULT_CANNULA_OFFSET_Y);
        mCannulaMaxOffsetY = DensityUtil.dip2px(null, DEFAULT_CANNULA_MAX_OFFSET_Y);
        mPipeBodyWidth = DensityUtil.dip2px(null, DEFAULT_PIPE_BODY_WIDTH);
        mPipeBodyHeight = DensityUtil.dip2px(null, DEFAULT_PIPE_BODY_HEIGHT);
        mBalloonWidth = DensityUtil.dip2px(null, DEFAULT_BALLOON_WIDTH);
        mBalloonHeight = DensityUtil.dip2px(null, DEFAULT_BALLOON_HEIGHT);
        mRectCornerRadius = DensityUtil.dip2px(null, DEFAULT_RECT_CORNER_RADIUS);

        mBalloonColor = new Color(DEFAULT_BALLOON_COLOR);
        mGasTubeColor = new Color(DEFAULT_GAS_TUBE_COLOR);
        mCannulaColor = new Color(DEFAULT_CANNULA_COLOR);
        mPipeBodyColor = new Color(DEFAULT_PIPE_BODY_COLOR);

        mProgressText = 10 + PERCENT_SIGN;

        //mDuration = ANIMATION_DURATION;
    }

    private void setupPaint() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
    }

    @Override
    protected void draw(Canvas canvas, Rect bounds) {
        int saveCount = canvas.save();

        RectFloat arcBounds = mCurrentBounds;
        arcBounds.modify(bounds);

        computeRender(mProgress);

        //draw draw gas tube
        mPaint.setColor(mGasTubeColor);
        mPaint.setStyle(Paint.Style.STROKE_STYLE);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawPath(createGasTubePath(mGasTubeBounds), mPaint);

        //draw balloon
        mPaint.setColor(mBalloonColor);
        mPaint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
        canvas.drawPath(createBalloonPath(mBalloonBounds, mProgress), mPaint);

        //draw progress
        mPaint.setColor(mGasTubeColor);
        mPaint.setTextSize((int) mTextSize);
        mPaint.setStrokeWidth(mStrokeWidth / 5.0f);
        canvas.drawText(mPaint, mProgressText, arcBounds.getCenter().getPointX() - mProgressBounds.getWidth() / 2.0f,
                mGasTubeBounds.getCenter().getPointY() + mProgressBounds.getHeight() / 2.0f);

        //draw cannula
        mPaint.setColor(mCannulaColor);
        mPaint.setStyle(Paint.Style.STROKE_STYLE);
        mPaint.setStrokeWidth(mStrokeWidth);
        canvas.drawPath(createCannulaHeadPath(mCannulaBounds), mPaint);
        mPaint.setStyle(Paint.Style.FILL_STYLE);
        canvas.drawPath(createCannulaBottomPath(mCannulaBounds), mPaint);

        //draw pipe body
        mPaint.setColor(mPipeBodyColor);
        mPaint.setStyle(Paint.Style.FILL_STYLE);
        canvas.drawRoundRect(mPipeBodyBounds, mRectCornerRadius, mRectCornerRadius, mPaint);

        canvas.restoreToCount(saveCount);
    }

    protected void computeRender(float renderProgress) {
        RectFloat arcBounds = mCurrentBounds;
        //compute gas tube bounds
        mGasTubeBounds.modify(arcBounds.getCenter().getPointX() - mGasTubeWidth / 2.0f, arcBounds.getCenter().getPointY(),
                arcBounds.getCenter().getPointX() + mGasTubeWidth / 2.0f, arcBounds.getCenter().getPointY() + mGasTubeHeight);
        //compute pipe body bounds
        mPipeBodyBounds.modify(arcBounds.getCenter().getPointX() + mGasTubeWidth / 2.0f - mPipeBodyWidth / 2.0f, arcBounds.getCenter().getPointY() - mPipeBodyHeight,
                arcBounds.getCenter().getPointX() + mGasTubeWidth / 2.0f + mPipeBodyWidth / 2.0f, arcBounds.getCenter().getPointY());
        //compute cannula bounds
        mCannulaBounds.modify(arcBounds.getCenter().getPointX() + mGasTubeWidth / 2.0f - mCannulaWidth / 2.0f, arcBounds.getCenter().getPointY() - mCannulaHeight - mCannulaOffsetY,
                arcBounds.getCenter().getPointX() + mGasTubeWidth / 2.0f + mCannulaWidth / 2.0f, arcBounds.getCenter().getPointY() - mCannulaOffsetY);
        //compute balloon bounds
        float insetX = mBalloonWidth * 0.333f * (1 - mProgress);
        float insetY = mBalloonHeight * 0.667f * (1 - mProgress);
        mBalloonBounds.modify(arcBounds.getCenter().getPointX() - mGasTubeWidth / 2.0f - mBalloonWidth / 2.0f + insetX, arcBounds.getCenter().getPointY() - mBalloonHeight + insetY,
                arcBounds.getCenter().getPointX() - mGasTubeWidth / 2.0f + mBalloonWidth / 2.0f - insetX, arcBounds.getCenter().getPointY());


        mCannulaBounds.modify(mCannulaBounds.left, mCannulaBounds.top, mCannulaBounds.right,
                mCannulaBounds.bottom -mCannulaMaxOffsetY * renderProgress / START_INHALE_DURATION_OFFSET);


        //mProgress = 0.0f;
        mProgressText = (int)(mProgress * 100) + PERCENT_SIGN;

        //mPaint.setTextSize(mTextSize);
        //mPaint.getTextBounds(mProgressText, 0, mProgressText.length(), mProgressBounds);

    }

    private int adjustProgress(int progress) {
        progress = progress / 10 * 10;
        progress = 100 - progress + 10;
        if (progress > 100) {
            progress = 100;
        }

        return progress;
    }

    private Path createGasTubePath(RectFloat gasTubeRect) {
        Path path = new Path();
        path.moveTo(gasTubeRect.left, gasTubeRect.top);
        path.lineTo(gasTubeRect.left, gasTubeRect.bottom);
        path.lineTo(gasTubeRect.right, gasTubeRect.bottom);
        path.lineTo(gasTubeRect.right, gasTubeRect.top);
        return path;
    }

    private Path createCannulaHeadPath(RectFloat cannulaRect) {
        Path path = new Path();
        path.moveTo(cannulaRect.left, cannulaRect.top);
        path.lineTo(cannulaRect.right, cannulaRect.top);
        path.moveTo(cannulaRect.getCenter().getPointX(), cannulaRect.top);
        path.lineTo(cannulaRect.getCenter().getPointX(), cannulaRect.bottom - 0.833f * cannulaRect.getWidth());
        return path;
    }

    private Path createCannulaBottomPath(RectFloat cannulaRect) {
        RectFloat cannulaHeadRect = new RectFloat(cannulaRect.left, cannulaRect.bottom - 0.833f * cannulaRect.getWidth(),
                cannulaRect.right, cannulaRect.bottom);

        Path path = new Path();
        path.addRoundRect(cannulaHeadRect, mRectCornerRadius, mRectCornerRadius, Path.Direction.COUNTER_CLOCK_WISE);
        return path;
    }

    /**
     * Coordinates are approximate, you have better cooperate with the designer's design draft
     */
    private Path createBalloonPath(RectFloat balloonRect, float progress) {

        Path path = new Path();
        path.moveTo(balloonRect.getCenter().getPointX(), balloonRect.bottom);

        float progressWidth = balloonRect.getWidth() * progress;
        float progressHeight = balloonRect.getHeight() * progress;
        //draw left half
        float leftIncrementX1 = progressWidth * -0.48f;
        float leftIncrementY1 = progressHeight * 0.75f;
        float leftIncrementX2 = progressWidth * -0.03f;
        float leftIncrementY2 = progressHeight * -1.6f;
        float leftIncrementX3 = progressWidth * 0.9f;
        float leftIncrementY3 = progressHeight * -1.0f;

        path.cubicTo(new Point(balloonRect.left + balloonRect.getWidth() * 0.25f + leftIncrementX1, balloonRect.getCenter().getPointY() - balloonRect.getHeight() * 0.4f + leftIncrementY1),
                new Point(balloonRect.left - balloonRect.getWidth() * 0.20f + leftIncrementX2, balloonRect.getCenter().getPointY() + balloonRect.getHeight() * 1.15f + leftIncrementY2),
                new Point(balloonRect.left - balloonRect.getWidth() * 0.4f + leftIncrementX3, balloonRect.bottom + leftIncrementY3));

//        the results of the left final transformation
//        path.cubicTo(balloonRect.left - balloonRect.width() * 0.13f, balloonRect.centerY() + balloonRect.height() * 0.35f,
//                balloonRect.left - balloonRect.width() * 0.23f, balloonRect.centerY() - balloonRect.height() * 0.45f,
//                balloonRect.left + balloonRect.width() * 0.5f, balloonRect.bottom － balloonRect.height());

        //draw right half
        float rightIncrementX1 = progressWidth * 1.51f;
        float rightIncrementY1 = progressHeight * -0.05f;
        float rightIncrementX2 = progressWidth * 0.03f;
        float rightIncrementY2 = progressHeight * 0.5f;
        float rightIncrementX3 = 0.0f;
        float rightIncrementY3 = 0.0f;

        path.cubicTo(new Point(balloonRect.left - balloonRect.getWidth() * 0.38f + rightIncrementX1, balloonRect.getCenter().getPointY() - balloonRect.getHeight() * 0.4f + rightIncrementY1),
                new Point(balloonRect.left + balloonRect.getWidth() * 1.1f + rightIncrementX2, balloonRect.getCenter().getPointY() - balloonRect.getHeight() * 0.15f + rightIncrementY2),
                new Point(balloonRect.left + balloonRect.getWidth() * 0.5f + rightIncrementX3, balloonRect.bottom + rightIncrementY3));

//        the results of the right final transformation
//        path.cubicTo(balloonRect.left + balloonRect.width() * 1.23f, balloonRect.centerY() - balloonRect.height() * 0.45f,
//                balloonRect.left + balloonRect.width() * 1.13f, balloonRect.centerY() + balloonRect.height() * 0.35f,
//                balloonRect.left + balloonRect.width() * 0.5f, balloonRect.bottom);

        return path;
    }
}
