package com.isoftstone.loadingview;

import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.render.Path;
import ohos.agp.utils.Color;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.agp.utils.RectFloat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadingRanderWatter extends LoadingRander{
    //private static final Interpolator MATERIAL_INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final float DEFAULT_WIDTH = 200.0f;
    private static final float DEFAULT_HEIGHT = 150.0f;
    private static final float DEFAULT_STROKE_WIDTH = 1.5f;
    private static final float DEFAULT_BOTTLE_WIDTH = 30;
    private static final float DEFAULT_BOTTLE_HEIGHT = 43;
    private static final float WATER_LOWEST_POINT_TO_BOTTLENECK_DISTANCE = 30;

    private static final int DEFAULT_WAVE_COUNT = 5;
    private static final int DEFAULT_WATER_DROP_COUNT = 25;

    private static final int MAX_WATER_DROP_RADIUS = 5;
    private static final int MIN_WATER_DROP_RADIUS = 1;

    private static final int DEFAULT_BOTTLE_COLOR = Color.getIntColor("#FFAACBCB");
    private static final int DEFAULT_WATER_COLOR = Color.getIntColor("#FF29E3F2");

    private static final float DEFAULT_TEXT_SIZE = 7.0f;

    private static final String LOADING_TEXT = "loading";
    private static final long ANIMATION_DURATION = 11111;


    private final Random mRandom = new Random();

    private final Paint mPaint = new Paint();
    private final RectFloat mCurrentBounds = new RectFloat();
    private final RectFloat mBottleBounds = new RectFloat();
    private final RectFloat mWaterBounds = new RectFloat();
    private final RectFloat mLoadingBounds = new RectFloat();
    private final List<LoadingRanderWatter.WaterDropHolder> mWaterDropHolders = new ArrayList<>();



    private float mBottleWidth;
    private float mBottleHeight;
    private float mStrokeWidth;
    private float mWaterLowestPointToBottleneckDistance;

    private int mBottleColor;
    private int mWaterColor;

    private int mWaveCount;

    public LoadingRanderWatter() {
        init();
        setupPaint();
    }

    private void init() {
        mTextSize = DensityUtil.dip2px(null, DEFAULT_TEXT_SIZE);

        mWidth = DensityUtil.dip2px(null, DEFAULT_WIDTH);
        mHeight = DensityUtil.dip2px(null, DEFAULT_HEIGHT);
        mStrokeWidth = DensityUtil.dip2px(null, DEFAULT_STROKE_WIDTH);

        mBottleWidth = DensityUtil.dip2px(null, DEFAULT_BOTTLE_WIDTH);
        mBottleHeight = DensityUtil.dip2px(null, DEFAULT_BOTTLE_HEIGHT);
        mWaterLowestPointToBottleneckDistance = DensityUtil.dip2px(null, WATER_LOWEST_POINT_TO_BOTTLENECK_DISTANCE);

        mBottleColor = DEFAULT_BOTTLE_COLOR;
        mWaterColor = DEFAULT_WATER_COLOR;

        mWaveCount = DEFAULT_WAVE_COUNT;

        //mDuration = ANIMATION_DURATION;
    }

    private void setupPaint() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeJoin(Paint.Join.ROUND_JOIN);
    }

    @Override
    protected void draw(Canvas canvas, Rect bounds) {
        int saveCount = canvas.save();

        String string = new String();
        String.format(string, "mProgress = %f", mProgress);
        canvas.drawText(mPaint, string, 0 , 100);

        RectFloat arcBounds = mCurrentBounds;
        arcBounds.modify(bounds);

        computeRender(mProgress);

        //draw bottle
        mPaint.setStyle(Paint.Style.STROKE_STYLE);
        mPaint.setColor(new Color(mBottleColor));
        canvas.drawPath(createBottlePath(mBottleBounds), mPaint);

        //draw water
        mPaint.setStyle(Paint.Style.FILLANDSTROKE_STYLE);
        mPaint.setColor(new Color(mWaterColor));
        canvas.drawPath(createWaterPath(mWaterBounds, mProgress), mPaint);

        //draw water drop
        mPaint.setStyle(Paint.Style.FILL_STYLE);
        mPaint.setColor(new Color(mWaterColor));
        for (LoadingRanderWatter.WaterDropHolder waterDropHolder : mWaterDropHolders) {
            if (waterDropHolder.mNeedDraw) {
                canvas.drawCircle(waterDropHolder.mInitX, waterDropHolder.mCurrentY, waterDropHolder.mRadius, mPaint);
            }
        }

        //draw loading text
        mPaint.setColor(new Color(mBottleColor));
        canvas.drawText(mPaint, LOADING_TEXT, mBottleBounds.getCenter().getPointX() - mLoadingBounds.getWidth() / 2.0f,
                mBottleBounds.bottom + mBottleBounds.getHeight() * 0.2f);

        canvas.restoreToCount(saveCount);
        //mProgress += 0.1f;
        //if (mProgress >= 1.0f) {
        //    mProgress = 0.0f;
        //}
        computeRender(mProgress);
    }

    protected void computeRender(float renderProgress) {
        if (mCurrentBounds.getWidth() <= 0) {
            return;
        }

        RectFloat arcBounds = mCurrentBounds;
        //compute gas tube bounds
        mBottleBounds.modify(arcBounds.getCenter().getPointX() - mBottleWidth / 2.0f, arcBounds.getCenter().getPointY() - mBottleHeight / 2.0f,
                arcBounds.getCenter().getPointX() + mBottleWidth / 2.0f, arcBounds.getCenter().getPointY() + mBottleHeight / 2.0f);
        //compute pipe body bounds
        mWaterBounds.modify(mBottleBounds.left + mStrokeWidth * 1.5f, mBottleBounds.top + mWaterLowestPointToBottleneckDistance,
                mBottleBounds.right - mStrokeWidth * 1.5f, mBottleBounds.bottom - mStrokeWidth * 1.5f);

        //compute wave progress
        float totalWaveProgress = renderProgress * mWaveCount;
        float currentWaveProgress = totalWaveProgress - ((int) totalWaveProgress);

        mProgress = renderProgress;
        if (currentWaveProgress > 0.5f) {
            //mProgress = 1.0f - MATERIAL_INTERPOLATOR.getInterpolation((currentWaveProgress - 0.5f) * 2.0f);
        } else {
            //mProgress = MATERIAL_INTERPOLATOR.getInterpolation(currentWaveProgress * 2.0f);
        }

        //init water drop holders
        if (mWaterDropHolders.isEmpty()) {
            initWaterDropHolders(mBottleBounds, mWaterBounds);
        }

        //compute the location of these water drops
        for (LoadingRanderWatter.WaterDropHolder waterDropHolder : mWaterDropHolders) {
            if (waterDropHolder.mDelayDuration < renderProgress
                    && waterDropHolder.mDelayDuration + waterDropHolder.mDuration > renderProgress) {
                float riseProgress = (renderProgress - waterDropHolder.mDelayDuration) / waterDropHolder.mDuration;
                riseProgress = riseProgress < 0.5f ? riseProgress * 2.0f : 1.0f - (riseProgress - 0.5f) * 2.0f;
                waterDropHolder.mCurrentY = waterDropHolder.mInitY -
                        riseProgress * waterDropHolder.mRiseHeight;
                waterDropHolder.mNeedDraw = true;
            } else {
                waterDropHolder.mNeedDraw = false;
            }
        }

        //measure loading text
        mPaint.setTextSize((int) mTextSize);
        //mPaint.getTextBounds(LOADING_TEXT, 0, LOADING_TEXT.length(), mLoadingBounds);
    }

    private Path createBottlePath(RectFloat bottleRect) {
        float bottleneckWidth = bottleRect.getWidth() * 0.3f;
        float bottleneckHeight = bottleRect.getHeight() * 0.415f;
        float bottleneckDecorationWidth = bottleneckWidth * 1.1f;
        float bottleneckDecorationHeight = bottleneckHeight * 0.167f;

        Path path = new Path();
        //draw the left side of the bottleneck decoration
        path.moveTo(bottleRect.getCenter().getPointX() - bottleneckDecorationWidth * 0.5f, bottleRect.top);
        path.quadTo(bottleRect.getCenter().getPointX() - bottleneckDecorationWidth * 0.5f - bottleneckWidth * 0.15f, bottleRect.top + bottleneckDecorationHeight * 0.5f,
                bottleRect.getCenter().getPointX() - bottleneckWidth * 0.5f, bottleRect.top + bottleneckDecorationHeight);
        path.lineTo(bottleRect.getCenter().getPointX() - bottleneckWidth * 0.5f, bottleRect.top + bottleneckHeight);

        //draw the left side of the bottle's body
        float radius = (bottleRect.getWidth() - mStrokeWidth) / 2.0f;
        float centerY = bottleRect.bottom - 0.86f * radius;
        RectFloat bodyRect = new RectFloat(bottleRect.left, centerY - radius, bottleRect.right, centerY + radius);
        path.addArc(bodyRect, 255, -135);

        //draw the bottom of the bottle
        float bottleBottomWidth = bottleRect.getWidth() / 2.0f;
        path.lineTo(bottleRect.getCenter().getPointX() - bottleBottomWidth / 2.0f, bottleRect.bottom);
        path.lineTo(bottleRect.getCenter().getPointX() + bottleBottomWidth / 2.0f, bottleRect.bottom);

        //draw the right side of the bottle's body
        path.addArc(bodyRect, 60, -135);

        //draw the right side of the bottleneck decoration
        path.lineTo(bottleRect.getCenter().getPointX() + bottleneckWidth * 0.5f, bottleRect.top + bottleneckDecorationHeight);
        path.quadTo(bottleRect.getCenter().getPointX() + bottleneckDecorationWidth * 0.5f + bottleneckWidth * 0.15f, bottleRect.top + bottleneckDecorationHeight * 0.5f,
                bottleRect.getCenter().getPointX() + bottleneckDecorationWidth * 0.5f, bottleRect.top);

        return path;
    }

    private Path createWaterPath(RectFloat waterRect, float progress) {
        Path path = new Path();

        path.moveTo(waterRect.left, waterRect.top);

        //Similar to the way draw the bottle's bottom sides
        float radius = (waterRect.getWidth() - mStrokeWidth) / 2.0f;
        float centerY = waterRect.bottom - 0.86f * radius;
        float bottleBottomWidth = waterRect.getWidth() / 2.0f;
        RectFloat bodyRect = new RectFloat(waterRect.left, centerY - radius, waterRect.right, centerY + radius);

        path.addArc(bodyRect, 187.5f, -67.5f);
        path.lineTo(waterRect.getCenter().getPointX() - bottleBottomWidth / 2.0f, waterRect.bottom);
        path.lineTo(waterRect.getCenter().getPointX() + bottleBottomWidth / 2.0f, waterRect.bottom);
        path.addArc(bodyRect, 60, -67.5f);

        //draw the water waves
        float cubicXChangeSize = waterRect.getWidth() * 0.35f * progress;
        float cubicYChangeSize = waterRect.getHeight() * 1.2f * progress;
        path.cubicTo(new Point(waterRect.left + waterRect.getWidth() * 0.80f - cubicXChangeSize,
                        waterRect.top - waterRect.getHeight() * 1.2f + cubicYChangeSize),
                new Point(waterRect.left + waterRect.getWidth() * 0.55f - cubicXChangeSize,
                        waterRect.top - cubicYChangeSize),
                new Point(waterRect.left,
                        waterRect.top - mStrokeWidth / 2.0f));

        path.lineTo(waterRect.left, waterRect.top);

        return path;
    }

    private void initWaterDropHolders(RectFloat bottleRect, RectFloat waterRect) {
        float bottleRadius = bottleRect.getWidth() / 2.0f;
        float lowestWaterPointY = waterRect.top;
        float twoSidesInterval = 0.2f * bottleRect.getWidth();
        float atLeastDelayDuration = 0.1f;

        float unitDuration = 0.1f;
        float delayDurationRange = 0.6f;
        int radiusRandomRange = MAX_WATER_DROP_RADIUS - MIN_WATER_DROP_RADIUS;
        float currentXRandomRange = bottleRect.getWidth() * 0.6f;

        for (int i = 0; i < DEFAULT_WATER_DROP_COUNT; i++) {
            LoadingRanderWatter.WaterDropHolder waterDropHolder = new LoadingRanderWatter.WaterDropHolder();
            waterDropHolder.mRadius = MIN_WATER_DROP_RADIUS + mRandom.nextInt(radiusRandomRange);
            waterDropHolder.mInitX = bottleRect.left + twoSidesInterval + mRandom.nextFloat() * currentXRandomRange;
            waterDropHolder.mInitY = lowestWaterPointY + waterDropHolder.mRadius / 2.0f;
            waterDropHolder.mRiseHeight = getMaxRiseHeight(bottleRadius, waterDropHolder.mRadius, waterDropHolder.mInitX - bottleRect.left)
                    * (0.2f + 0.8f * mRandom.nextFloat());
            waterDropHolder.mDelayDuration = atLeastDelayDuration + mRandom.nextFloat() * delayDurationRange;
            waterDropHolder.mDuration = waterDropHolder.mRiseHeight / bottleRadius * unitDuration;

            mWaterDropHolders.add(waterDropHolder);
        }
    }

    private float getMaxRiseHeight(float bottleRadius, float waterDropRadius, float currentX) {
        float coordinateX = currentX - bottleRadius;
        float bottleneckRadius = bottleRadius * 0.3f;
        if (coordinateX - waterDropRadius > -bottleneckRadius
                && coordinateX + waterDropRadius < bottleneckRadius) {
            return bottleRadius * 2.0f;
        }

        return (float) (Math.sqrt(Math.pow(bottleRadius, 2.0f) - Math.pow(coordinateX, 2.0f)) - waterDropRadius);
    }



    private class WaterDropHolder {
        public float mCurrentY;

        public float mInitX;
        public float mInitY;
        public float mDelayDuration;
        public float mRiseHeight;

        public float mRadius;
        public float mDuration;

        public boolean mNeedDraw;
    }

}
