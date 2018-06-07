package com.ule.haichenyi.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.haichenyi.aloe.tools.GlideUtils;

/**
 * @Title:
 * @Description:
 * @Author: wz
 * @Date: 2018/5/22
 * @Version: V1.0
 */
public class ImageActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ImageView img1 = findViewById(R.id.img1);
        String url1 = "http://haichenyi.com/uploads/artistic_image/psb17.jpg";
        GlideUtils.loadImg(url1, img1, true);
        ImageView img2 = findViewById(R.id.img2);
        String url2 = "https://upload-images.jianshu.io/upload_images/7041675-eafe9e456931ea0c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
        GlideUtils.loadImg(url2, img2, true);
        ImageView img3 = findViewById(R.id.img3);
        String url3 = "https://upload-images.jianshu.io/upload_images/7041675-ea281190daab14cc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
        GlideUtils.loadImg(url3, img3, true);
        ImageView img4 = findViewById(R.id.img4);
        String url4 = "https://upload-images.jianshu.io/upload_images/7041675-cf5bf556f2f397c8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
        GlideUtils.loadImg(url4, img4, true);
        ImageView img5 = findViewById(R.id.img5);
        String url5 = "https://upload-images.jianshu.io/upload_images/7041675-d697288155ec68af.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
        GlideUtils.loadImg(url5, img5, true);
        ImageView img6 = findViewById(R.id.img6);
        String url6 = "https://upload-images.jianshu.io/upload_images/7041675-bdc96f1dee8ba801.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240";
        GlideUtils.loadImg(url6, img6, true);
//        TextView textView = null;
//        textView.setText("");
    }
}
