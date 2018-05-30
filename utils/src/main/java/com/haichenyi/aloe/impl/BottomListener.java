package com.haichenyi.aloe.impl;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haichenyi.aloe.Interface.BottomClickListener;


/**
 * @Title: BottomListener
 * @Description: 需要哪个，实现哪个
 * @Author: wz
 * @Date: 2018/5/30
 * @Version: V1.0
 */
public abstract class BottomListener implements BottomClickListener {
    @Override
    public void onItemClickListener(BaseQuickAdapter adapter, View view, int position) {

    }

    @Override
    public void onItemChildClickListener(BaseQuickAdapter adapter, View view, int position) {

    }
}
