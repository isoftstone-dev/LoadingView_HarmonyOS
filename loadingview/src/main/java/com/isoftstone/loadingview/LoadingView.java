package com.isoftstone.loadingview;

import ohos.agp.animation.Animator;
import ohos.agp.animation.AnimatorValue;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.render.Canvas;
import ohos.agp.utils.DimensFloat;
import ohos.agp.utils.Point;
import ohos.agp.utils.Rect;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class LoadingView extends Component implements Component.DrawTask {

    public enum LoadingViewType {
        // 支持的类型
        WATER, BALLOON, FISH, CIRCLE;
    }

    // 动画
    private AnimatorValue animatorValue;

    // 绘制类
    private LoadingRander loadingRander;


    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttrSet attrSet) {
        super(context, attrSet);
        init();
    }


    @Override
    public void onDraw(Component component, Canvas canvas) {
        // 获取组件的大小，进行绘制
        DimensFloat pt = getComponentSize();
        Rect rect = new Rect(0,0,pt.getSizeXToInt(),pt.getSizeYToInt());
        loadingRander.draw(canvas, rect);
    }

    // 动画侦听函数
    private final AnimatorValue.ValueUpdateListener mAnimatorUpdateListener
            = new AnimatorValue.ValueUpdateListener() {
        @Override
        public void onUpdate(AnimatorValue animatorValue, float v) {
            if (loadingRander != null) {
                loadingRander.setProgess(v);
            }
            invalidate();
        }
    };

    private void init()  {
        // 启动动画
        animatorValue = new AnimatorValue();
        animatorValue.setCurveType(Animator.CurveType.LINEAR);
        animatorValue.setDelay(100);
        animatorValue.setLoopedCount(Animator.INFINITE);
        animatorValue.setDuration(2000);
        animatorValue.setValueUpdateListener(mAnimatorUpdateListener);
        animatorValue.start();
    }

    // 设置动画的类型
    public boolean SetType(LoadingViewType type) {
        switch (type) {
            case WATER:
                loadingRander  = new LoadingRanderWatter();
                break;

            case BALLOON:
                loadingRander  = new LoadingRanderBalloon();
                break;

            case FISH:
                loadingRander  = new LoadingRanderFish();
                break;

            case CIRCLE:
                loadingRander  = new LoadingRanderCircle();
                break;

            default:
                return false;
        }

        return true;
    }


}
