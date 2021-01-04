package com.isoftstone.loadingview_harmonyos.slice;

import com.isoftstone.loadingview.LoadingView;
import com.isoftstone.loadingview_harmonyos.ResourceTable;
import com.isoftstone.precentpositionlayout.PrecentPositionLayout;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;

public class MainAbilitySlice extends AbilitySlice {
    PrecentPositionLayout precentPositionLayout;
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        LoadingView loadingView1 = (LoadingView)findComponentById(ResourceTable.Id_text_water);
        loadingView1.SetType(LoadingView.LoadingViewType.WATER);
        loadingView1.addDrawTask(loadingView1);

        LoadingView loadingView2 = (LoadingView)findComponentById(ResourceTable.Id_text_ballon);
        loadingView2.SetType(LoadingView.LoadingViewType.BALLOON);
        loadingView2.addDrawTask(loadingView2);

        LoadingView loadingView3 = (LoadingView)findComponentById(ResourceTable.Id_text_fish);
        loadingView3.SetType(LoadingView.LoadingViewType.FISH);
        loadingView3.addDrawTask(loadingView3);

        LoadingView loadingView4 = (LoadingView)findComponentById(ResourceTable.Id_text_circle);
        loadingView4.SetType(LoadingView.LoadingViewType.CIRCLE);
        loadingView4.addDrawTask(loadingView4);

        PrecentPositionLayout precentPositionLayout
                = (PrecentPositionLayout)findComponentById(ResourceTable.Id_layout_main);
        precentPositionLayout.AutoSize();
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
