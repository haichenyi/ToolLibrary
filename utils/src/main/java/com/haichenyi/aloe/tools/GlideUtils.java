package com.haichenyi.aloe.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

/**
 * @Title: GlideUtils
 * @Description: glide 图片加载工具类，可以初始化一次init方法，统一加载占位图和错误图
 * @Author: wz
 * @Date: 2018/5/22
 * @Version: V1.0
 */
public class GlideUtils {
    private static RequestOptions options;

    /**
     * 加载图片
     *
     * @param activity  activity对象
     * @param url       图片url地址
     * @param imageView 需要显示图片的ImageView
     * @param isCircle  是否需要圆形显示。false：正常显示。true：圆形显示
     */
    @SuppressLint("CheckResult")
    public static void loadImg(Activity activity, String url, ImageView imageView,
                               boolean isCircle) {
        if (options != null) {
            if (isCircle) options.circleCrop();
            Glide.with(activity).load(url).apply(options).into(imageView);
        } else {
            throw new NullPointerException("options is null,Do you init before use?");
        }
    }

    @SuppressLint("CheckResult")
    public static void loadImg(Fragment fragment, String url, ImageView imageView,
                               boolean isCircle) {
        if (options != null) {
            if (isCircle) options.circleCrop();
            Glide.with(fragment).load(url).apply(options).into(imageView);
        } else {
            throw new NullPointerException("options is null,Do you init before use?");
        }
    }

    /**
     * 加载图片
     *
     * @param activity       activity对象
     * @param url            图片url地址
     * @param imageView      需要显示图片的ImageView
     * @param requestOptions 加载图片方式
     * @param isCircle       是否需要圆形显示。false：正常显示。true：圆形显示
     */
    @SuppressLint("CheckResult")
    public static void loadImg(Activity activity, String url, ImageView imageView,
                               RequestOptions requestOptions, boolean isCircle) {
        if (isCircle) requestOptions.circleCrop();
        Glide.with(activity).load(url).apply(requestOptions).into(imageView);
    }

    @SuppressLint("CheckResult")
    public static void loadImg(Fragment fragment, String url, ImageView imageView,
                               RequestOptions requestOptions, boolean isCircle) {
        if (isCircle) requestOptions.circleCrop();
        Glide.with(fragment).load(url).apply(requestOptions).into(imageView);
    }

    /**
     * 指定加载图片的宽高
     *
     * @param activity  activity对象
     * @param url       图片url地址
     * @param imageView 需要显示图片的ImageView
     * @param isCircle  是否需要圆形显示。false：正常显示。true：圆形显示
     * @param width     需要显示图片的宽。单位：dp
     * @param height    需要显示图片的高。单位：dp
     */
    @SuppressLint("CheckResult")
    public static void loadImg(Activity activity, String url, ImageView imageView, boolean isCircle,
                               float width, float height) {
        if (options != null) {
            options.override((int) ToolsUtils.digitValue(width, TypedValue.COMPLEX_UNIT_DIP),
                    (int) ToolsUtils.digitValue(height, TypedValue.COMPLEX_UNIT_DIP));
            if (isCircle) options.circleCrop();
            Glide.with(activity).load(url).apply(options).into(imageView);
        } else {
            throw new NullPointerException("options is null,Do you init before use?");
        }
    }

    @SuppressLint("CheckResult")
    public static void loadImg(Fragment fragment, String url, ImageView imageView, boolean isCircle,
                               float width, float height) {
        if (options != null) {
            options.override((int) ToolsUtils.digitValue(width, TypedValue.COMPLEX_UNIT_DIP),
                    (int) ToolsUtils.digitValue(height, TypedValue.COMPLEX_UNIT_DIP));
            if (isCircle) options.circleCrop();
            Glide.with(fragment).load(url).apply(options).into(imageView);
        } else {
            throw new NullPointerException("options is null,Do you init before use?");
        }
    }

    /**
     * 加载gif图
     *
     * @param activity   activity对象
     * @param resourceId 资源图片id
     * @param imageView  需要显示图片的ImageView
     */
    public static void loadGif(Activity activity, @DrawableRes int resourceId,
                               ImageView imageView) {
        Glide.with(activity).asGif().load(resourceId).into(imageView);
    }

    public static void loadGif(Fragment fragment, @DrawableRes int resourceId,
                               ImageView imageView) {
        Glide.with(fragment).asGif().load(resourceId).into(imageView);
    }

    /**
     * 把gif图当成bitmap加载
     *
     * @param activity   activity
     * @param resourceId 资源id
     * @param imageView  imageView
     */
    public static void loadGifAsBitmap(Activity activity, @DrawableRes int resourceId,
                                       ImageView imageView) {
        Glide.with(activity).asBitmap().load(resourceId).into(imageView);
    }

    public static void loadGifAsBitmap(Fragment fragment, @DrawableRes int resourceId,
                                       ImageView imageView) {
        Glide.with(fragment).asBitmap().load(resourceId).into(imageView);
    }

    /**
     * 初始化
     *
     * @param drawableBefore 占位图
     * @param drawableError  加载错误显示图片
     */
    public static void init(int drawableBefore, int drawableError) {
        options = new RequestOptions()
                .placeholder(drawableBefore)  //占位图
                .error(drawableError)  //加载错误显示图片
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);//缓存策略，默认：自动选择缓存方式
        // .override(300,200)//指定显示图片的宽高
        // .skipMemoryCache(true)//true表示禁止内存缓存功能，默认是false
        //  .fitCenter()//正中间显示，保持图片的原有长宽比例，不拉伸图片
        //          .centerCrop()//全屏显示，与fitXY类似
        //  .circleCrop()//圆形显示
    }
}