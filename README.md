# ToolLibrary 工具类

## 依赖方式
```
compile 'com.haichenyi.aloe:utils:1.2.0'
```

## 简介和用法

### 1、ByteUtils 数组工具类
    
    内部目前包含两个类，一个数组复制，一个数组合并
    
    数组复制：拷贝给定的数组的数据，从给定起始位置到给定长度，返回拷贝之后的数组
    
    数组合并：合并给定的数组（数组个数>=2），返回合并后的数组
    
### 2、DateUtils 日期工具类

    内部目前包含两个类：一个格式化时间，一个判断是否是24小时
    
### 3、FileUtils 文件管理工具类

    内部包含多个方法，如：文件夹创建，文件创建，文件夹删除，文件读写
    
    文件夹删除：删除文件夹目录下的所有文件
    
    文件读写：包括任意位置的读写，和应用目录下file目录下的读写，覆盖写和追加写

### 4、GlideUtils 图片加载工具类

        通过Glide加载图片，内部包含多个方法，如是否需要圆形加载，是否加载指定长宽，
    加载gif图，把gif图当作静态图加载等等。
        需要提前初始化，指定占位图和加载失败图片。
```
GlideUtils.init(R.mipmap.ic_launcher,R.mipmap.ic_launcher_round);
```

### 5、LogUtils 日志打印工具类
    
        封装好的日志打印类，提供开关方法让用户确定是否需要打印，打印的日志一目了然，
    很显眼，  并且，造成的崩溃可以定位到具体代码。
```
LogUtils.setIsNeedLog(true);//true:打印，false:不打印
```

### 6、MemorySpaceUtils 存储空间工具类

    获取手机总存储空间，可用存储空间，获取网络文件大小，返回值long类型
    
    如果要返回以M,G为单位的容量的字符串：Formatter.formatFileSize(context, long)

### 7、NetworkUtils 网络管理类 

    内部包含多个方法，如：判断手机当前网络状态，网络类型，是否为5G网络，wifi管理等等

### 8、OkHttpUtils 网络请求类

    看名字就应该知道，封装的OkHttp的网络工具类，post方法和下载方法
    
    他们的回调都是在主线程当中，你不用再为切换线程而烦恼
    
### 9、PermissionUtils 权限请求管理类

    Android 6.0增加的运行时管理权限，一行代码请求权限，并且权限如果拒绝，
    
    提供方法，显示默认对话框，提示用户跳转应用设置页面去给权限
    
### 10、ScreenUtils 屏幕工具类
    
    内部包含多个方法，如：设置屏幕亮度，设置屏幕锁屏时间等等

### 11、StringUtils 字符串工具类

    内部包含多个方法，如：byte数组与16进制的相互转换，字符串是否相等，字符串是否为空等等

### 12、ThreadManager 线程管理工具类

    不用再为线程回收而烦恼
    
### 13、ToastUtils 吐司工具类
    
    实时更新的Toast，用之前需要初始化一次
```
ToastUtils.init(this);
```
    
### 14、ToolsHttpsConnection https网络请求的工具类

    封装的Android底层HttpsURLConnection类
    
### 15、ToolsUtils 其他工具类

    内部包含多个方法，如：根据资源名称获取资源id，跳转app安装界面等等

### 16、UiUtils Ui工具类

    内部包含多个方法，如：设置dialog的宽高，设置popup的背景颜色等等
    
***PS：以上类中方法的调用方式，demo里面都有示例。***