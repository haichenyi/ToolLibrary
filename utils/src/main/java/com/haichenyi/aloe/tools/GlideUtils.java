package com.haichenyi.aloe.tools;

import android.annotation.SuppressLint;
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
public final class GlideUtils {
    private GlideUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    private static RequestOptions options;

    /**
     * 加载图片
     *
     * @param url       图片url地址
     * @param imageView 需要显示图片的ImageView
     * @param isCircle  是否需要圆形显示。false：正常显示。true：圆形显示
     */
    @SuppressLint("CheckResult")
    public static void loadImg(final String url, final ImageView imageView, final boolean isCircle) {
        if (options != null) {
            if (isCircle) {
                options.circleCrop();
            }
            Glide.with(imageView.getContext()).load(url).apply(options).into(imageView);
        } else {
            throw new NullPointerException("options is null,Do you init before use?");
        }
    }

    /**
     * 加载图片
     *
     * @param url            图片url地址
     * @param imageView      需要显示图片的ImageView
     * @param requestOptions 加载图片方式
     * @param isCircle       是否需要圆形显示。false：正常显示。true：圆形显示
     */
    @SuppressLint("CheckResult")
    public static void loadImg(final String url, final ImageView imageView,
                               final RequestOptions requestOptions, final boolean isCircle) {
        if (isCircle) {
            requestOptions.circleCrop();
        }
        Glide.with(imageView.getContext()).load(url).apply(requestOptions).into(imageView);
    }

    /**
     * 指定加载图片的宽高
     *
     * @param url       图片url地址
     * @param imageView 需要显示图片的ImageView
     * @param isCircle  是否需要圆形显示。false：正常显示。true：圆形显示
     * @param width     需要显示图片的宽。单位：dp
     * @param height    需要显示图片的高。单位：dp
     */
    @SuppressLint("CheckResult")
    public static void loadImg(final String url, final ImageView imageView, final boolean isCircle,
                               final float width, final float height) {
        if (options != null) {
            options.override((int) ToolsUtils.digitValue(width, TypedValue.COMPLEX_UNIT_DIP),
                    (int) ToolsUtils.digitValue(height, TypedValue.COMPLEX_UNIT_DIP));
            if (isCircle) {
                options.circleCrop();
            }
            Glide.with(imageView.getContext()).load(url).apply(options).into(imageView);
        } else {
            throw new NullPointerException("options is null,Do you init before use?");
        }
    }

    /**
     * 加载gif图
     *
     * @param resourceId 资源图片id
     * @param imageView  需要显示图片的ImageView
     */
    public static void loadGif(final @DrawableRes int resourceId, final ImageView imageView) {
        Glide.with(imageView.getContext()).asGif().load(resourceId).into(imageView);
    }

    /**
     * 把gif图当成bitmap加载
     *
     * @param resourceId 资源id
     * @param imageView  imageView
     */
    public static void loadGifAsBitmap(final @DrawableRes int resourceId, final ImageView imageView) {
        Glide.with(imageView.getContext()).asBitmap().load(resourceId).into(imageView);
    }

    /**
     * 初始化
     *
     * @param drawableBefore 占位图
     * @param drawableError  加载错误显示图片
     */
    public static void init(final int drawableBefore, final int drawableError) {
        options = new RequestOptions()
                //占位图
                .placeholder(drawableBefore)
                //加载错误显示图片
                .error(drawableError)
                //缓存策略，默认：自动选择缓存方式
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        //指定显示图片的宽高
        // .override(300,200)
        //true表示禁止内存缓存功能，默认是false
        // .skipMemoryCache(true)
        //正中间显示，保持图片的原有长宽比例，不拉伸图片
        //  .fitCenter()
        //全屏显示，与fitXY类似
        //  .centerCrop()
        //圆形显示
        //  .circleCrop()
    }
}
