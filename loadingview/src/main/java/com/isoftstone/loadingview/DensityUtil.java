package com.isoftstone.loadingview;

import ohos.app.Context;


import ohos.app.Context;

public class DensityUtil {

    public static float dip2px(Context context, float dpValue) {
        //float scale = context.getResources().getDisplayMetrics().density;
        //return dpValue * scale;
        return dpValue * 6.5f;
    }
}