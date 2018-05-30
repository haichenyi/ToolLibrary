package com.haichenyi.aloe.Interface;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: ${date}
 * @Version: V1.0
 */

public interface BottomClickListener {
    void onItemClickListener(BaseQuickAdapter adapter, View view, int position);

    void onItemChildClickListener(BaseQuickAdapter adapter, View view, int position);
}
