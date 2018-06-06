package com.haichenyi.aloe.tools;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.haichenyi.aloe.Interface.OnDismissListener;
import com.haichenyi.aloe.Interface.PermissionListener;
import com.haichenyi.aloe.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import io.reactivex.functions.Consumer;


/**
 * @Title: PermissionUtils
 * @Description: 动态请求权限
 * 需要依赖：implementation 'com.tbruyelle.rxpermissions2:rxpermissions:0.9.4@aar'
 * @Author: wz
 * @Date: 2018/5/17
 * @Version: V1.0
 */
public final class PermissionUtils {

    private PermissionUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    /**
     * 请求权限的封装.
     *
     * @param activity    activity
     * @param listener    listener
     * @param permissions 需要请求的权限名称
     */
    public static void requestPermission(final Activity activity, final PermissionListener listener,
                                         final String... permissions) {
        new RxPermissions(activity).request(permissions).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (null != listener) {
                    listener.onResult(aBoolean);
                }
            }
        });
    }

    /**
     * 用户拒绝给权限之后，调用该方法提示用户去设置页面给权限
     *
     * @param activity       activity
     * @param permissionName 权限名称
     * @param msg            给这个权限的理由
     * @param listener       diaolog消失的回调方法
     */
    public static void setupPermission(final AppCompatActivity activity, final String permissionName,
                                       final String msg, final OnDismissListener listener) {

        new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Dialog).setTitle("权限申请")
                .setMessage(String.format(Locale.getDefault(), "请在“权限”中开启“%1s权限”，以正常使用%2s", permissionName, msg))
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (null != listener) {
                            listener.onDismiss();
                        }
                    }
                }).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setPermissionsSetting(activity);
            }
        }).create().show();
    }

    /**
     * 跳转小米系统的设置页面.
     *
     * @param activity activity
     */
    public static void setPermissionsSetting(final Activity activity) {
        if (isMiUi()) {
            try {
                // MIUI 8
                activity.startActivityForResult(new Intent("miui.intent.action.APP_PERM_EDITOR")
                        .setClassName("com.miui.securitycenter",
                                "com.miui.permcenter.permissions.PermissionsEditorActivity")
                        .putExtra("extra_pkgname", activity.getPackageName()), 1000);
            } catch (Exception e) {
                try {
                    // MIUI 5/6/7
                    activity.startActivityForResult(new Intent("miui.intent.action.APP_PERM_EDITOR")
                            .setClassName("com.miui.securitycenter",
                                    "com.miui.permcenter.permissions.AppPermissionsEditorActivity")
                            .putExtra("extra_pkgname", activity.getPackageName()), 1000);
                } catch (Exception e1) {
                    // 否则跳转到应用详情
                    activity.startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", activity.getPackageName(),
                                    null)), 1000);
                }
            }
        } else {
            activity.startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", activity.getPackageName(), null)), 1000);
        }
    }

    /**
     * 判断是否是MiUi系统.
     *
     * @return boolean
     */
    private static boolean isMiUi() {
        String device = Build.MANUFACTURER;
        if (TextUtils.equals(device, "Xiaomi")) {
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(new File(Environment.getRootDirectory(),
                        "build.prop")));
                return (prop.getProperty("ro.miui.ui.version.code", null) != null
                        || prop.getProperty("ro.miui.ui.version.name", null) != null
                        || prop.getProperty("ro.miui.internal.storage", null) != null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
