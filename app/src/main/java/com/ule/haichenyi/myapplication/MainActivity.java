package com.ule.haichenyi.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haichenyi.aloe.interfaces.DownloadListener;
import com.haichenyi.aloe.interfaces.HttpCallback;
import com.haichenyi.aloe.interfaces.HttpsListener;
import com.haichenyi.aloe.interfaces.OnDismissListener;
import com.haichenyi.aloe.interfaces.PermissionListener;
import com.haichenyi.aloe.impl.AbstractBottomListener;
import com.haichenyi.aloe.impl.RecycleViewDivider;
import com.haichenyi.aloe.tools.FileUtils;
import com.haichenyi.aloe.tools.IndexBar;
import com.haichenyi.aloe.tools.LogUtils;
import com.haichenyi.aloe.tools.MemorySpaceUtils;
import com.haichenyi.aloe.tools.NetworkUtils;
import com.haichenyi.aloe.tools.OkHttpUtils;
import com.haichenyi.aloe.tools.PermissionUtils;
import com.haichenyi.aloe.tools.ThreadManager;
import com.haichenyi.aloe.tools.ToastUtils;
import com.haichenyi.aloe.tools.ToolsHttpsConnection;
import com.haichenyi.aloe.tools.ToolsUtils;
import com.haichenyi.aloe.tools.UiUtils;
import com.ule.haichenyi.myapplication.bean.BottomBean;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtils.v("wz", "打印日志v");
        LogUtils.e("wz", "打印日志e");
        String name = "123rewf fdfdasf fdsfdsvc bvcbvc qwe";
        LogUtils.i(LogUtils.TAG_Wz, name);
        String replace = name.replace(" ", ";");
        LogUtils.i(LogUtils.TAG_Wz, replace);
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
    }

    @SuppressWarnings("unused")
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                ToastUtils.showTipMsg("测试Toast类");
                break;
            case R.id.btn2:
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
                break;
            case R.id.btn3:
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e(LogUtils.TAG_Wz, "ThreadManager");
                    }
                });
                break;
            case R.id.btn4:
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showTipMsg("网络是否可用：" + NetworkUtils.isConnectIsNormal(MainActivity.this));
                    }
                });
                break;
            case R.id.btn5:
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        ToolsUtils.startSetting(MainActivity.this);
                    }
                });
                break;
            case R.id.btn6:
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
                break;
            case R.id.btn7:
                LogUtils.e(ToolsUtils.getFloat(432.123674324F, 3));
                break;
            case R.id.btn8:
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
                break;
            case R.id.btn9:
                LogUtils.i(LogUtils.TAG_Wz, DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()));
                break;
            case R.id.btn10:
                startActivity(new Intent(MainActivity.this, ImageActivity.class));
                break;
            case R.id.btn11:
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
                break;
            case R.id.btn12:
                PermissionUtils.requestPermission(MainActivity.this, new PermissionListener() {
                    @Override
                    public void onResult(boolean hasPermission) {
                        if (hasPermission) {
                            try {
                                File ule = FileUtils.createFileDirs(getCacheDir().getAbsolutePath(), "ule");
                                File newFile = FileUtils.createNewFile(ule.getAbsolutePath(), "HelloWorld.txt");
                                assert newFile != null;
                                FileUtils.writeFile(newFile.getAbsolutePath(), "this is a text!");
                                String s = FileUtils.readFile(newFile.getAbsolutePath());
                                LogUtils.i(LogUtils.TAG_Wz, "写入的消息为：" + s);
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
                break;
            case R.id.btn13:
                String path = "http://haichenyi.com/uploads/artistic_image/psb17.jpg";
                OkHttpUtils.getInstance().downLoad(path, getCacheDir().getAbsolutePath(), "", new DownloadListener() {
                    @Override
                    public void onFailed(Exception e) {
                        LogUtils.i(LogUtils.TAG_Wz, e.getMessage());
                    }

                    @Override
                    public void onProgress(long current, long total, int progress) {
                        LogUtils.i(LogUtils.TAG_Wz, current + "/" + total + "=" + progress);
                    }

                    @Override
                    public void onSuccess(String filePath) {
                        ToastUtils.showTipMsg(filePath);
                        LogUtils.i(LogUtils.TAG_Wz, filePath);
                    }
                });
                break;
            case R.id.btn14:
                ThreadManager.getDefault().execute(new Runnable() {
                    @Override
                    public void run() {
                        String path = getFilesDir().getAbsolutePath();
                        LogUtils.i(LogUtils.TAG_Wz, "path:" + path);
                        String name = "sanhuo";
                        String fileName = "sanhuo.zip";
                        LogUtils.i(LogUtils.TAG_Wz, !FileUtils.isNoEmpty(path, name));
                        if (!FileUtils.isNoEmpty(path, name)) {
                            try {
                                InputStream inputStream = getAssets().open(fileName);
                                LogUtils.i(LogUtils.TAG_Wz, inputStream != null);
                                if (inputStream != null) {
                                    File file = FileUtils.createFileDirs(path, name);
                                    FileUtils.UnZip(inputStream, file.getAbsolutePath());
                                } else {

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //sanhuo文件夹存在，并且长度不为0
                        }
                    }
                });
                break;
            case R.id.btn15:
                List<BottomBean> data = new ArrayList<>();
                for (int i = 0; i < 15; i++) {
                    data.add(new BottomBean().setLeft("第" + i + "栏左边item").setRight("第" + i + "栏右边item"));
                }
                BaseQuickAdapter<BottomBean, BaseViewHolder> adapter = new BaseQuickAdapter<BottomBean, BaseViewHolder>(R.layout.item_bottom, data) {
                    @Override
                    protected void convert(BaseViewHolder helper, BottomBean item) {
                        helper.setText(R.id.tv_content_left, item.left);
                        helper.setText(R.id.tv_content_right, item.right);
                        helper.addOnClickListener(R.id.tv_content_left)
                                .addOnClickListener(R.id.tv_content_right);
                    }
                };
                RecycleViewDivider divider = new RecycleViewDivider(MainActivity.this, LinearLayoutManager.HORIZONTAL, 1, Color.BLACK);
                UiUtils.showBottomDialog(MainActivity.this, divider, adapter, new AbstractBottomListener() {
                    @Override
                    public void onItemChildClickListener(BaseQuickAdapter adapter, View view, int position) {
                        switch (view.getId()) {
                            case R.id.tv_content_left:
                                ToastUtils.showTipMsg("第" + position + "栏左边item");
                                break;
                            case R.id.tv_content_right:
                                ToastUtils.showTipMsg("第" + position + "栏右边item");
                                break;
                            default:
                                break;
                        }
                    }
                });
                break;
            case R.id.btn16:
              View view1 = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_indexbar, null);
              RecyclerView recyclerView1 = view1.findViewById(R.id.recycler_view);
              IndexBar indexBar = view1.findViewById(R.id.index_bar);
              indexBar.bindRecyclerView(recyclerView1, null);
              List<DataBean> dataBeans = getData();
              BaseQuickAdapter<DataBean, BaseViewHolder> adapter1 = new BaseQuickAdapter<DataBean, BaseViewHolder>(android.R.layout.simple_list_item_1) {
                @Override
                protected void convert(BaseViewHolder helper, DataBean item) {
                  helper.setText(android.R.id.text1, item.name);
                }
              };
              recyclerView1.setAdapter(adapter1);
              adapter1.setNewData(dataBeans);
              indexBar.setSourceData(dataBeans).invalidate();
              new AlertDialog.Builder(view.getContext()).setView(view1).create().show();
                break;
            default:
                break;
        }
    }

    private List<DataBean> getData(){
      List<DataBean> dataBeans = new ArrayList<>();
      dataBeans.add(new DataBean("安陆", "A"));
      dataBeans.add(new DataBean("安庆", "A"));
      dataBeans.add(new DataBean("安乡", "A"));
      dataBeans.add(new DataBean("安阳", "A"));
      dataBeans.add(new DataBean("安义", "A"));

      dataBeans.add(new DataBean("北京", "B"));
      dataBeans.add(new DataBean("巴东", "B"));
      dataBeans.add(new DataBean("白城", "B"));
      dataBeans.add(new DataBean("宝鸡", "B"));

      dataBeans.add(new DataBean("蔡甸", "C"));
      dataBeans.add(new DataBean("长春", "C"));
      dataBeans.add(new DataBean("广水", "G"));
      dataBeans.add(new DataBean("广州", "G"));
      dataBeans.add(new DataBean("上海", "S"));
      dataBeans.add(new DataBean("三亚", "S"));
      return dataBeans;
    }

    private static class DataBean implements IndexBar.SuspensionHelper {
      private String name;
      private String tag;

      private DataBean(String name, String tag) {
        this.name = name;
        this.tag = tag;
      }

      public String getName() {
        return name;
      }

      @Override
      public String getTag() {
        return tag;
      }
    }
}
