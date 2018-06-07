package com.haichenyi.aloe.interfaces;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: 2018/6/7
 * @Version: V1.0
 */
public interface BottomClickListener {
    /**
     * item点击事件
     *
     * @param adapter  adapter
     * @param view     view
     * @param position position
     */
    void onItemClickListener(BaseQuickAdapter adapter, View view, int position);

    /**
     * item的子view的点击事件
     *
     * @param adapter  adapter
     * @param view     view
     * @param position position
     */
    void onItemChildClickListener(BaseQuickAdapter adapter, View view, int position);
}
