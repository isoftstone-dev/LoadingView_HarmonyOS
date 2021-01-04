package com.isoftstone.loadingview;

import ohos.agp.render.*;
import ohos.agp.utils.Color;
import ohos.agp.utils.Matrix;
import ohos.agp.utils.Rect;
import ohos.agp.utils.RectFloat;

public class LoadingRanderFish extends LoadingRander {
    //private Interpolator FISH_INTERPOLATOR = new FishInterpolator();

    private static final float DEFAULT_PATH_FULL_LINE_SIZE = 7.0f;
    private static final float DEFAULT_PATH_DOTTED_LINE_SIZE = DEFAULT_PATH_FULL_LINE_SIZE / 2.0f;
    private static final float DEFAULT_RIVER_HEIGHT = DEFAULT_PATH_FULL_LINE_SIZE * 8.5f;
    private static final float DEFAULT_RIVER_WIDTH = DEFAULT_PATH_FULL_LINE_SIZE * 5.5f;

    private static final float DEFAULT_FISH_EYE_SIZE = DEFAULT_PATH_FULL_LINE_SIZE * 0.5f;
    private static final float DEFAULT_FISH_WIDTH = DEFAULT_PATH_FULL_LINE_SIZE * 3.0f;
    private static final float DEFAULT_FISH_HEIGHT = DEFAULT_PATH_FULL_LINE_SIZE * 4.5f;

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 150.0f;
    private static final float DEFAULT_RIVER_BANK_WIDTH = DEFAULT_PATH_FULL_LINE_SIZE;

    private static final long ANIMATION_DURATION = 800;
    private static final float DOTTED_LINE_WIDTH_COUNT = (8.5f + 5.5f - 2.0f) * 2.0f * 2.0f;
    private static final float DOTTED_LINE_WIDTH_RATE = 1.0f / DOTTED_LINE_WIDTH_COUNT;

    private final float[] FISH_MOVE_POINTS = new float[]{
            DOTTED_LINE_WIDTH_RATE * 3.0f, DOTTED_LINE_WIDTH_RATE * 6.0f,
            DOTTED_LINE_WIDTH_RATE * 15f, DOTTED_LINE_WIDTH_RATE * 18f,
            DOTTED_LINE_WIDTH_RATE * 27.0f, DOTTED_LINE_WIDTH_RATE * 30.0f,
            DOTTED_LINE_WIDTH_RATE * 39f, DOTTED_LINE_WIDTH_RATE * 42f,
    };

    private final float FISH_MOVE_POINTS_RATE = 1.0f / FISH_MOVE_POINTS.length;

    private static final int DEFAULT_COLOR = Color.getIntColor("#ff0090d6");

    private final Paint mPaint = new Paint();
    private final RectFloat mTempBounds = new RectFloat();

    private final float[] mFishHeadPos = new float[2];

    private Path mRiverPath;
    private PathMeasure mRiverMeasure;

    private float mFishRotateDegrees;

    private float mRiverBankWidth;
    private float mRiverWidth;
    private float mRiverHeight;
    private float mFishWidth;
    private float mFishHeight;
    private float mFishEyeSize;
    private float mPathFullLineSize;
    private float mPathDottedLineSize;

    private Color mColor;

    public LoadingRanderFish() {
        super();
        init();
        setupPaint();
    }

    private void init()  {
        mWidth = DensityUtil.dip2px(null, DEFAULT_WIDTH);
        mHeight = DensityUtil.dip2px(null, DEFAULT_HEIGHT);
        mRiverBankWidth = DensityUtil.dip2px(null, DEFAULT_RIVER_BANK_WIDTH);

        mPathFullLineSize = DensityUtil.dip2px(null, DEFAULT_PATH_FULL_LINE_SIZE);
        mPathDottedLineSize = DensityUtil.dip2px(null, DEFAULT_PATH_DOTTED_LINE_SIZE);
        mFishWidth = DensityUtil.dip2px(null, DEFAULT_FISH_WIDTH);
        mFishHeight = DensityUtil.dip2px(null, DEFAULT_FISH_HEIGHT);
        mFishEyeSize = DensityUtil.dip2px(null, DEFAULT_FISH_EYE_SIZE);
        mRiverWidth = DensityUtil.dip2px(null, DEFAULT_RIVER_WIDTH);
        mRiverHeight = DensityUtil.dip2px(null, DEFAULT_RIVER_HEIGHT);

        mColor = new Color(DEFAULT_COLOR);

        //mDuration = ANIMATION_DURATION;
    }

    private void setupPaint() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mRiverBankWidth);
        mPaint.setStyle(Paint.Style.STROKE_STYLE);
        mPaint.setStrokeJoin(Paint.Join.MITER_JOIN);
        //mPaint.setPathEffect(new DashPathEffect(new float[]{mPathFullLineSize, mPathDottedLineSize}, mPathDottedLineSize));
    }

    @Override
    protected void draw(Canvas canvas, Rect bounds) {
        int saveCount = canvas.save();
        RectFloat arcBounds = mTempBounds;
        arcBounds.modify(bounds);

        computeRender(mProgress);

        mPaint.setColor(mColor);

        //calculate fish clip bounds
        //clip the width of the fish need to increase mPathDottedLineSize * 1.2f
        RectFloat fishRectF = new RectFloat(mFishHeadPos[0] - mFishWidth / 2.0f - mPathDottedLineSize * 1.2f, mFishHeadPos[1] - mFishHeight / 2.0f,
                mFishHeadPos[0] + mFishWidth / 2.0f + mPathDottedLineSize * 1.2f, mFishHeadPos[1] + mFishHeight / 2.0f);
        Matrix matrix = new Matrix();
        matrix.postRotate(mFishRotateDegrees, fishRectF.getCenter().getPointX(), fishRectF.getCenter().getPointY());
        matrix.mapRect(fishRectF);

        //draw river
        int riverSaveCount = canvas.save();
        mPaint.setStyle(Paint.Style.STROKE_STYLE);
        canvas.clipRect(fishRectF, Canvas.ClipOp.DIFFERENCE);
        canvas.drawPath(createRiverPath(arcBounds), mPaint);
        canvas.restoreToCount(riverSaveCount);

        //draw fish
        int fishSaveCount = canvas.save();
        mPaint.setStyle(Paint.Style.FILL_STYLE);
        canvas.rotate(mFishRotateDegrees, mFishHeadPos[0], mFishHeadPos[1]);
        canvas.clipPath(createFishEyePath(mFishHeadPos[0], mFishHeadPos[1] - mFishHeight * 0.06f), Canvas.ClipOp.DIFFERENCE);
        canvas.drawPath(createFishPath(mFishHeadPos[0], mFishHeadPos[1]), mPaint);
        canvas.restoreToCount(fishSaveCount);

        canvas.restoreToCount(saveCount);
    }

    private float calculateRotateDegrees(float fishProgress) {
        if (fishProgress < FISH_MOVE_POINTS_RATE * 2) {
            return 90;
        }

        if (fishProgress < FISH_MOVE_POINTS_RATE * 4) {
            return 180;
        }

        if (fishProgress < FISH_MOVE_POINTS_RATE * 6) {
            return 270;
        }

        return 0.0f;
    }

    protected void computeRender(float renderProgress) {
        if (mRiverPath == null) {
            return;
        }

        if (mRiverMeasure == null) {
            mRiverMeasure = new PathMeasure(mRiverPath, false);
        }

        //float fishProgress = FISH_INTERPOLATOR.getInterpolation(renderProgress);
        float fishProgress = renderProgress;

        mRiverMeasure.getPosTan(mRiverMeasure.getLength() * fishProgress, mFishHeadPos, null);
        mFishRotateDegrees = calculateRotateDegrees(fishProgress);
    }



    private Path createFishEyePath(float fishEyeCenterX, float fishEyeCenterY) {
        Path path = new Path();
        path.addCircle(fishEyeCenterX, fishEyeCenterY, mFishEyeSize, Path.Direction.COUNTER_CLOCK_WISE);

        return path;
    }

    private Path createFishPath(float fishCenterX, float fishCenterY) {
        Path path = new Path();

        float fishHeadX = fishCenterX;
        float fishHeadY = fishCenterY - mFishHeight / 2.0f;

        //the head of the fish
        path.moveTo(fishHeadX, fishHeadY);
        //the left body of the fish
        path.quadTo(fishHeadX - mFishWidth * 0.333f, fishHeadY + mFishHeight * 0.222f, fishHeadX - mFishWidth * 0.333f, fishHeadY + mFishHeight * 0.444f);
        path.lineTo(fishHeadX - mFishWidth * 0.333f, fishHeadY + mFishHeight * 0.666f);
        path.lineTo(fishHeadX - mFishWidth * 0.5f, fishHeadY + mFishHeight * 0.8f);
        path.lineTo(fishHeadX - mFishWidth * 0.5f, fishHeadY + mFishHeight);

        //the tail of the fish
        path.lineTo(fishHeadX, fishHeadY + mFishHeight * 0.9f);

        //the right body of the fish
        path.lineTo(fishHeadX + mFishWidth * 0.5f, fishHeadY + mFishHeight);
        path.lineTo(fishHeadX + mFishWidth * 0.5f, fishHeadY + mFishHeight * 0.8f);
        path.lineTo(fishHeadX + mFishWidth * 0.333f, fishHeadY + mFishHeight * 0.666f);
        path.lineTo(fishHeadX + mFishWidth * 0.333f, fishHeadY + mFishHeight * 0.444f);
        path.quadTo(fishHeadX + mFishWidth * 0.333f, fishHeadY + mFishHeight * 0.222f, fishHeadX, fishHeadY);

        path.close();

        return path;
    }

    private Path createRiverPath(RectFloat arcBounds) {
        if (mRiverPath != null) {
            return mRiverPath;
        }

        mRiverPath = new Path();

        RectFloat rectF = new RectFloat(arcBounds.getCenter().getPointX() - mRiverWidth / 2.0f, arcBounds.getCenter().getPointY() - mRiverHeight / 2.0f,
                arcBounds.getCenter().getPointX() + mRiverWidth / 2.0f, arcBounds.getCenter().getPointY() + mRiverHeight / 2.0f);

        rectF.modify(rectF.left + mRiverBankWidth / 2.0f, rectF.top + mRiverBankWidth / 2.0f,
                rectF.right - mRiverBankWidth / 2.0f, rectF.bottom  - mRiverBankWidth / 2.0f);

        //rectF.inset(mRiverBankWidth / 2.0f, mRiverBankWidth / 2.0f);

        mRiverPath.addRect(rectF, Path.Direction.COUNTER_CLOCK_WISE);

        return mRiverPath;
    }


}
