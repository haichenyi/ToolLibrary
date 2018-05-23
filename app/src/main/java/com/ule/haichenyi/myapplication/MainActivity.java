package com.ule.haichenyi.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;

import com.haichenyi.aloe.Interface.HttpCallback;
import com.haichenyi.aloe.Interface.HttpsListener;
import com.haichenyi.aloe.Interface.OnDismissListener;
import com.haichenyi.aloe.Interface.PermissionListener;
import com.haichenyi.aloe.tools.FileUtils;
import com.haichenyi.aloe.tools.GlideUtils;
import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.MemorySpaceUtils;
import com.haichenyi.aloe.tools.NetworkUtils;
import com.haichenyi.aloe.tools.OkHttpUtils;
import com.haichenyi.aloe.tools.PermissionUtils;
import com.haichenyi.aloe.tools.ThreadManager;
import com.haichenyi.aloe.tools.ToastUtils;
import com.haichenyi.aloe.tools.ToolsHttpsConnection;
import com.haichenyi.aloe.tools.ToolsUtils;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.v("wz", "打印日志v");
        LogUtils.i("wz", "打印日志i");
        LogUtils.d("wz", "打印日志d");
        LogUtils.e("wz", "打印日志e");
        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showTipMsg("测试Toast类");
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtils.requestPermission(MainActivity.this, new PermissionListener() {
                    @Override
                    public void onResult(boolean hasPermission) {
                        if (hasPermission) {
                            ToastUtils.showTipMsg("权限获取成功");
                        } else {
                            PermissionUtils.setupPermission(MainActivity.this,
                                    "相机功能",
                                    "拍照功能需要获取相机功能和额外存储功能",
                                    new OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            ToastUtils.showTipMsg("权限获取失败");
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE);
            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e("wz", "ThreadManager");
                    }
                });
            }
        });
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showTipMsg("网络是否可用：" + NetworkUtils.isConnectIsNormal(MainActivity.this));
                    }
                });
            }
        });
        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        ToolsUtils.startSetting(MainActivity.this);
                    }
                });
            }
        });
        findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        ThreadManager.getDefault().execute(new Runnable() {
                            @Override
                            public void run() {
                                String url = "";
                                String token = "";
                                ArrayMap<String, String> map = new ArrayMap<>();
                                ToolsHttpsConnection.post(MainActivity.this, url, token, map, new HttpsListener() {
                                    @Override
                                    public void onSuccess(String success) {
                                        ToastUtils.showTipMsg(success);
                                    }

                                    @Override
                                    public void onFailed(String failed) {
                                        ToastUtils.showTipMsg(failed);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
        findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogUtils.e(ToolsUtils.getFloat(432.123674324F, 3));
            }
        });

        //1、必须要有activity
        new Activity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showTipMsg("主线程回调");
            }
        });
        //2、
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showTipMsg("主线程回调");
            }
        });
        findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "";
                OkHttpUtils.getInstance().post(url, new ArrayMap<String, Object>(),
                        new ArrayMap<String, Object>(), new HttpCallback() {
                            @Override
                            public void onStart() {
                                ToastUtils.showTipMsg("开始请求");
                            }

                            @Override
                            public void onSuccess(String success) {
                                ToastUtils.showTipMsg("请求成功");
                            }

                            @Override
                            public void onError(String failed) {
                                ToastUtils.showTipMsg("请求失败");
                            }
                        });
            }
        });

        findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i(LogUtils.TAG_Wz, DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
            }
        });
        findViewById(R.id.btn10).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ImageActivity.class));
            }
        });

        findViewById(R.id.btn11).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ThreadManager.getDefault().execute(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                    @Override
                    public void run() {
                        try {
                            String path = "http://haichenyi.com/uploads/artistic_image/psb17.jpg";
                            LogUtils.i(LogUtils.TAG_Wz, Formatter.formatFileSize(MainActivity.this, MemorySpaceUtils.FileSize(path)));
                            LogUtils.i(LogUtils.TAG_Wz, "手机可用外部存储空间：" + MemorySpaceUtils.getAvailableExternalMemorySize());
                            LogUtils.i(LogUtils.TAG_Wz, "手机可用内部存储空间：" + MemorySpaceUtils.getAvailableInternalMemorySize());
                            LogUtils.i(LogUtils.TAG_Wz, "手机内部存储空间：" + MemorySpaceUtils.getExternalMemorySize());
                            LogUtils.i(LogUtils.TAG_Wz, "手机内部存储空间：" + MemorySpaceUtils.getInternalMemorySize());
                            LogUtils.i(LogUtils.TAG_Wz, "手机SD卡是否可用：" + MemorySpaceUtils.isExternalStorageAvailable());
                            LogUtils.i(LogUtils.TAG_Wz, "getCacheDir()目录的可用空间：" + MemorySpaceUtils.getFileMemorySize(getCacheDir()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            LogUtils.i(LogUtils.TAG_Wz, e.getMessage());
                        }

                    }
                });
            }
        });

        findViewById(R.id.btn12).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.requestPermission(MainActivity.this, new PermissionListener() {
                    @Override
                    public void onResult(boolean hasPermission) {
                        if (hasPermission) {
                            try {
                                File ule = FileUtils.createFileDirs(getCacheDir().getAbsolutePath(), "ule");
                                File newFile = FileUtils.createNewFile(ule.getAbsolutePath(), "HelloWorld.txt");
                                FileUtils.writeFile(newFile.getAbsolutePath(), "this is a text!");
                                String s = FileUtils.readFile(newFile.getAbsolutePath());
                                LogUtils.i(LogUtils.TAG_Wz, "写入的消息为："+s);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ToastUtils.showTipMsg("创建成功");
                        } else {
                            PermissionUtils.setupPermission(MainActivity.this,
                                    "文件操作", "文件操作需要读写权限", new OnDismissListener() {
                                        @Override
                                        public void onDismiss() {
                                            ToastUtils.showTipMsg("获取权限失败");
                                        }
                                    });
                        }
                    }
                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
    }
}
