package com.haichenyi.aloe.tools;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haichenyi.aloe.Interface.BottomClickListener;
import com.haichenyi.aloe.impl.RecycleViewDivider;

/**
 * @Title: UiUtils
 * @Description: UI工具类
 * implementation "com.android.support:recyclerview-v7:27.1.1"
 * implementation "com.android.support:design:27.1.1"
 * implementation 'com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.34'
 * @Author: wz
 * @Date: 2018/5/30
 * @Version: V1.0
 */
public final class UiUtils {

    private UiUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    /**
     * 底部上滑的dialog
     *
     * @param context  context
     * @param divider  RecyclerView分割线
     * @param adapter  BaseQuickAdapter
     * @param listener 回调监听
     * @param <T>      泛型
     */
    public static <T> void showBottomDialog(final Context context, final RecycleViewDivider divider,
                                            final BaseQuickAdapter<T, BaseViewHolder> adapter,
                                            final BottomClickListener listener) {
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        RecyclerView recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        if (divider != null) {
            recyclerView.addItemDecoration(divider);
        }
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                dialog.dismiss();
                if (null != listener) {
                    listener.onItemClickListener(adapter, view, position);
                }
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                dialog.dismiss();
                if (null != listener) {
                    listener.onItemChildClickListener(adapter, view, position);
                }
            }
        });
        recyclerView.setAdapter(adapter);
        dialog.setContentView(recyclerView);
        dialog.show();
    }

    /**
     * 设置Dialog的长宽与当前屏幕长宽属性，必须要在show之后调用
     * 如：你想设置高度是当前屏幕高度的一半，这里heightF就传0.5
     *
     * @param dialog  dialog
     * @param widthF  宽度float
     * @param heightF 高度float
     */
    public static void setDialogParams(final AlertDialog dialog, final float widthF,
                                       final float heightF) {
        Window window = dialog.getWindow();
        if (null != window) {
            int width = dialog.getContext().getResources().getDisplayMetrics().widthPixels;
            int height = dialog.getContext().getResources().getDisplayMetrics().heightPixels;
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = (int) (width * widthF);
            params.height = (int) (height * heightF);
            dialog.getWindow().setAttributes(params);
        }
    }

    /**
     * 设置PopupWindow的背景颜色，让背景变暗
     * 在show之前调用
     *
     * @param popupWindow popupWindow
     * @param activity    popupWindow所在的activity
     */
    public static void setPopupParams(final PopupWindow popupWindow, final Activity activity) {
        final Window window = activity.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = 0.5F;
        window.setAttributes(lp);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lp.alpha = 1F;
                window.setAttributes(lp);
            }
        });
    }

}
