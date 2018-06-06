package com.haichenyi.aloe.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Title: NetworkUtils
 * @Description: 网络管理类
 * @Author: wz
 * @Date: 2018/5/18
 * @Version: V1.0
 */
@SuppressWarnings("unused")
public class NetworkUtils {
    public static final String TAG = "NetworkUtils";
    private static final byte CURRENT_NETWORK_TYPE_NONE = 0;
    /*
     * 根据APN区分网络类型.
     */
    private static final byte CURRENT_NETWORK_TYPE_WIFI = 1;// wifi
    private static final byte CURRENT_NETWORK_TYPE_CTNET = 2;// ctnet
    private static final byte CURRENT_NETWORK_TYPE_CTWAP = 3;// ctwap
    private static final byte CURRENT_NETWORK_TYPE_CMWAP = 4;// cmwap
    private static final byte CURRENT_NETWORK_TYPE_UNIWAP = 5;// uniwap,3gwap
    private static final byte CURRENT_NETWORK_TYPE_CMNET = 6;// cmnet
    private static final byte CURRENT_NETWORK_TYPE_UNIET = 7;// uninet,3gnet
    /**
     * 根据运营商区分网络类型.
     */
    private static final byte CURRENT_NETWORK_TYPE_CTC = 10;// ctwap,ctnet
    private static final byte CURRENT_NETWORK_TYPE_CUC = 11;// uniwap,3gwap,uninet,3gnet
    private static final byte CURRENT_NETWORK_TYPE_CM = 12;// cmwap,cmnet
    /**
     * apn值.
     */
    private static final String CONNECT_TYPE_WIFI = "wifi";
    private static final String CONNECT_TYPE_CTNET = "ctnet";
    private static final String CONNECT_TYPE_CTWAP = "ctwap";
    private static final String CONNECT_TYPE_CMNET = "cmnet";
    private static final String CONNECT_TYPE_CMWAP = "cmwap";
    private static final String CONNECT_TYPE_UNIWAP = "uniwap";
    private static final String CONNECT_TYPE_UNINET = "uninet";
    private static final String CONNECT_TYPE_UNI3GWAP = "3gwap";
    private static final String CONNECT_TYPE_UNI3GNET = "3gnet";
    private static final Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");

    private NetworkUtils() {
        throw new RuntimeException("工具类不允许创建对象");
    }

    /*
     *
     * 获取网络类型.
     *
     */
    public static int getNetType(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo == null) {
            return -1;
        } else
            return networkInfo.getType();
    }

    /**
     * 判断当前网络类型。WIFI,NET,WAP.
     */
    public static byte getCurrentNetType(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        byte type = CURRENT_NETWORK_TYPE_NONE;
        if (networkInfo != null) {
            String typeName = networkInfo.getExtraInfo();
            if (TextUtils.isEmpty(typeName)) {
                typeName = networkInfo.getTypeName();
            }
            if (!TextUtils.isEmpty(typeName)) {
                String temp = typeName.toLowerCase();
                if (temp.contains(CONNECT_TYPE_WIFI)) {
                    type = CURRENT_NETWORK_TYPE_WIFI;
                } else if (temp.contains(CONNECT_TYPE_CTNET)) {
                    type = CURRENT_NETWORK_TYPE_CTNET;
                } else if (temp.contains(CONNECT_TYPE_CTWAP)) {
                    type = CURRENT_NETWORK_TYPE_CTWAP;
                } else if (temp.contains(CONNECT_TYPE_CMNET)) {
                    type = CURRENT_NETWORK_TYPE_CMNET;
                } else if (temp.contains(CONNECT_TYPE_CMWAP)) {
                    type = CURRENT_NETWORK_TYPE_CMWAP;
                } else if (temp.contains(CONNECT_TYPE_UNIWAP)) {
                    type = CURRENT_NETWORK_TYPE_UNIWAP;
                } else if (temp.contains(CONNECT_TYPE_UNI3GWAP)) {
                    type = CURRENT_NETWORK_TYPE_UNIWAP;
                } else if (temp.contains(CONNECT_TYPE_UNINET)) {
                    type = CURRENT_NETWORK_TYPE_UNIET;
                } else if (temp.contains(CONNECT_TYPE_UNI3GNET)) {
                    type = CURRENT_NETWORK_TYPE_UNIET;
                }
            }
        }
        if (type == CURRENT_NETWORK_TYPE_NONE) {
            String apnType = getApnType(context);
            if (apnType != null && apnType.equals(CONNECT_TYPE_CTNET)) {// ctnet
                type = CURRENT_NETWORK_TYPE_CTNET;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_CTWAP)) {// ctwap
                type = CURRENT_NETWORK_TYPE_CTWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_CMWAP)) {// cmwap
                type = CURRENT_NETWORK_TYPE_CMWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_CMNET)) {// cmnet
                type = CURRENT_NETWORK_TYPE_CMNET;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNIWAP)) {// uniwap
                type = CURRENT_NETWORK_TYPE_UNIWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNI3GWAP)) {// 3gwap
                type = CURRENT_NETWORK_TYPE_UNIWAP;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNINET)) {// uninet
                type = CURRENT_NETWORK_TYPE_UNIET;
            } else if (apnType != null && apnType.equals(CONNECT_TYPE_UNI3GNET)) {// 3gnet
                type = CURRENT_NETWORK_TYPE_UNIET;
            }
        }
        return type;
    }

    /**
     * 判断APNTYPE.
     */
    public static String getApnType(Context context) {
        String apnType = "nomatch";
        Cursor c = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                String user = c.getString(c.getColumnIndex("user"));
                if (user != null && user.startsWith(CONNECT_TYPE_CTNET)) {
                    apnType = CONNECT_TYPE_CTNET;
                } else if (user != null && user.startsWith(CONNECT_TYPE_CTWAP)) {
                    apnType = CONNECT_TYPE_CTWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_CMWAP)) {
                    apnType = CONNECT_TYPE_CMWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_CMNET)) {
                    apnType = CONNECT_TYPE_CMNET;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNIWAP)) {
                    apnType = CONNECT_TYPE_UNIWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNINET)) {
                    apnType = CONNECT_TYPE_UNINET;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNI3GWAP)) {
                    apnType = CONNECT_TYPE_UNI3GWAP;
                } else if (user != null && user.startsWith(CONNECT_TYPE_UNI3GNET)) {
                    apnType = CONNECT_TYPE_UNI3GNET;
                }
            }
            c.close();
        }
        return apnType;
    }

    /**
     * 判断网络是否连接
     * 需要<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     */
    public static boolean isConnectIsNormal(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                String name = info.getTypeName();
                LogUtils.e(TAG, "当前网络名称：" + name);
                return true;
            } else {
                LogUtils.e(TAG, "没有可用网络");
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 此判断不可靠.
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 获取可用的网络信息.
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            return null;
        }
        return cm.getActiveNetworkInfo();
    }

    public static boolean isWifiOr3G(Context context) {
        return isWifi(context);
    }

    public static boolean is2G(Context context) {
        return !isWifiOr3G(context);
    }

    public static boolean is3G(Context context) {
        int type = getNetworkClass(context);
        return type == NETWORK_CLASS_3_G || type == NETWORK_CLASS_4_G;
    }

    /**
     * 当前网络是否是wifi网络.
     * true 代表是wifi
     * false 代表不是wifi
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isAvailable() && ni.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public static boolean getNetworkConnectionStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return null != tm && ((tm.getDataState() == TelephonyManager.DATA_CONNECTED || tm.getDataState() == TelephonyManager.DATA_ACTIVITY_NONE) && info.isAvailable());
    }

    public static boolean isCtwap(Context context) {
        return getApnType(context).equals(CONNECT_TYPE_CTWAP);
    }

    public static boolean isUniwap(Context context) {
        return getApnType(context).equals(CONNECT_TYPE_UNIWAP);
    }

    public static boolean isCmwap(Context context) {
        return getApnType(context).equals(CONNECT_TYPE_CMWAP);
    }

    /**
     * 判断是否是电信网络(ctwap,ctnet).
     */
    public static boolean isCtcNetwork(Context context) {
        byte type = getCurrentNetType(context);
        return isCtcNetwork(type);
    }

    public static boolean isCtcNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }
        return apnName.equals(CONNECT_TYPE_CTWAP) || apnName.equals(CONNECT_TYPE_CTNET);
    }

    public static boolean isCtcNetwork(byte type) {
        return type == CURRENT_NETWORK_TYPE_CTWAP || type == CURRENT_NETWORK_TYPE_CTNET;
    }

    /**
     * 判断是否是联通网络(uniwap,uninet,3gwap,3gnet).
     */
    public static boolean isCucNetwork(Context context) {
        byte type = getCurrentNetType(context);
        return isCucNetwork(type);
    }

    public static boolean isCucNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }
        return apnName.equals(CONNECT_TYPE_UNIWAP) || apnName.equals(CONNECT_TYPE_UNINET) || apnName.equals(CONNECT_TYPE_UNI3GWAP) || apnName.equals(CONNECT_TYPE_UNI3GNET);
    }

    public static boolean isCucNetwork(byte type) {
        return type == CURRENT_NETWORK_TYPE_UNIWAP || type == CURRENT_NETWORK_TYPE_UNIET;
    }

    /**
     * 判断是否是移动网络(cmwap,cmnet).
     */
    public static boolean isCmbNetwork(Context context) {
        byte type = getCurrentNetType(context);
        return isCmbNetwork(type);
    }

    public static boolean isCmbNetwork(String apnName) {
        if (apnName == null) {
            return false;
        }
        return apnName.equals(CONNECT_TYPE_CMWAP) || apnName.equals(CONNECT_TYPE_CMNET);
    }

    public static boolean isCmbNetwork(byte type) {
        return type == CURRENT_NETWORK_TYPE_CMWAP || type == CURRENT_NETWORK_TYPE_CMNET;
    }

    /**
     * 获取网络运营商类型(中国移动,中国联通,中国电信,wifi).
     */
    public static byte getNetworkOperators(Context context) {
        if (isWifi(context)) {
            return CURRENT_NETWORK_TYPE_WIFI;
        } else if (isCtcNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CTC;
        } else if (isCmbNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CM;
        } else if (isCucNetwork(context)) {
            return CURRENT_NETWORK_TYPE_CUC;
        } else {
            return CURRENT_NETWORK_TYPE_NONE;
        }
    }

    public static byte getNetworkOperators(byte type) {
        if (type == CURRENT_NETWORK_TYPE_NONE) {
            return CURRENT_NETWORK_TYPE_NONE;
        } else if (type == CURRENT_NETWORK_TYPE_WIFI) {
            return CURRENT_NETWORK_TYPE_WIFI;
        } else if (type == CURRENT_NETWORK_TYPE_CTNET || type == CURRENT_NETWORK_TYPE_CTWAP) {
            return CURRENT_NETWORK_TYPE_CTC;
        } else if (type == CURRENT_NETWORK_TYPE_CMWAP || type == CURRENT_NETWORK_TYPE_CMNET) {
            return CURRENT_NETWORK_TYPE_CM;
        } else if (type == CURRENT_NETWORK_TYPE_UNIWAP || type == CURRENT_NETWORK_TYPE_UNIET) {
            return CURRENT_NETWORK_TYPE_CUC;
        } else {
            return CURRENT_NETWORK_TYPE_NONE;
        }
    }

    /**
     * 是否需要设置代理(网络请求,一般用于wap网络,但有些机型设置代理会导致系统异常).
     */
    public static boolean isNeedSetProxyForNetRequest() { // #00044 +
        return !Build.MODEL.equals("SCH-N719") && !Build.MODEL.equals("SCH-I939D");
    }

    /**
     * get mac address of wifi if wifi is active.
     * 需要
     */

    @SuppressLint("HardwareIds")
    public static String getActiveMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        }
        return "";
    }

    public static String getNetworkInfo(Context context) {
        String info = "";
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();
            if (activeNetInfo != null) {
                if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    info = activeNetInfo.getTypeName();
                } else {
                    StringBuilder sb = new StringBuilder();
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    sb.append(activeNetInfo.getTypeName());
                    sb.append(" [");
                    if (tm != null) {
                        // Result may be unreliable on CDMA networks
                        sb.append(tm.getNetworkOperatorName());
                        sb.append("#");
                    }
                    sb.append(activeNetInfo.getSubtypeName());
                    sb.append("]");
                    info = sb.toString();
                }
            }
        }
        return info;
    }

    public enum NetworkSpeedMode {
        LOW, NORMAL, HIGH, UNKNOWN
    }

    /**
     * 网络类型.
     */
    private static final int NETWORK_CLASS_UNKNOWN = 0;

    private static final int NETWORK_CLASS_2_G = 1;

    private static final int NETWORK_CLASS_3_G = 2;

    private static final int NETWORK_CLASS_4_G = 3;

    private static final int NETWORK_CLASS_WIFI = 10;

    /**
     * 仅判断Mobile网络的慢速.蓝牙等其他网络不做判断.
     */
    public static NetworkSpeedMode getNetworkSpeedModeInMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    switch (networkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_IDEN: // ~25 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_CDMA: // ~ 14-64 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_EDGE: // ~ 50-100 kbps
                            return NetworkSpeedMode.LOW;
                        case TelephonyManager.NETWORK_TYPE_GPRS: // ~ 100 kbps
                            return NetworkSpeedMode.LOW;

                        case TelephonyManager.NETWORK_TYPE_EVDO_0: // ~ 400-1000
                            // kbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // ~ 600-1400
                            // kbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_HSPA: // ~ 700-1700 kbps
                            return NetworkSpeedMode.NORMAL;
                        case TelephonyManager.NETWORK_TYPE_UMTS: // ~ 400-7000 kbps
                            return NetworkSpeedMode.NORMAL;
                        case 14: // TelephonyManager.NETWORK_TYPE_EHRPD: // ~ 1-2
                            // Mbps
                            return NetworkSpeedMode.NORMAL;
                        case 12: // TelephonyManager.NETWORK_TYPE_EVDO_B: // ~ 5
                            // Mbps
                            return NetworkSpeedMode.NORMAL;

                        case TelephonyManager.NETWORK_TYPE_HSDPA: // ~ 2-14 Mbps
                            return NetworkSpeedMode.HIGH;
                        case TelephonyManager.NETWORK_TYPE_HSUPA: // ~ 1-23 Mbps
                            return NetworkSpeedMode.HIGH;
                        case 15: // TelephonyManager.NETWORK_TYPE_HSPAP: // ~ 10-20
                            // Mbps
                            return NetworkSpeedMode.HIGH;
                        case 13: // TelephonyManager.NETWORK_TYPE_LTE: // ~ 10+ Mbps
                            return NetworkSpeedMode.HIGH;

                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            return NetworkSpeedMode.NORMAL;
                        default:
                            break;
                    }
                }
            }
        }
        return NetworkSpeedMode.UNKNOWN;
    }

    /**
     * 获取在Mobile网络下的网络类型. 2G,3G,4G.
     */
    public static int getNetworkClass(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    switch (networkInfo.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NETWORK_CLASS_2_G;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case 12: // TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case 14: // TelephonyManager.NETWORK_TYPE_EHRPD:
                        case 15: // TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NETWORK_CLASS_3_G;
                        case 13: // TelephonyManager.NETWORK_TYPE_LTE:
                            return NETWORK_CLASS_4_G;
                        default:
                            return NETWORK_CLASS_UNKNOWN;
                    }
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_CLASS_WIFI;
                }
            }
        }
        return NETWORK_CLASS_UNKNOWN;
    }

    public static String getNetworkTypeName(Context context) {
        String networkName = "UNKNOWN";
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                networkName = getNetworkTypeName(networkInfo.getType());
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    networkName += "#" + getNetworkTypeNameInMobile(networkInfo.getSubtype());
                }
            }
        }
        return networkName;
    }

    private static String getNetworkTypeNameInMobile(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "CDMA - EvDo rev. 0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "CDMA - EvDo rev. A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "CDMA - EvDo rev. B";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "CDMA - 1xRTT";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "CDMA - eHRPD";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "iDEN";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            default:
                return "UNKNOWN";
        }
    }

    private static String getNetworkTypeName(int type) {
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE:
                return "MOBILE";
            case ConnectivityManager.TYPE_WIFI:
                return "WIFI";
            case ConnectivityManager.TYPE_MOBILE_MMS:
                return "MOBILE_MMS";
            case ConnectivityManager.TYPE_MOBILE_SUPL:
                return "MOBILE_SUPL";
            case ConnectivityManager.TYPE_MOBILE_DUN:
                return "MOBILE_DUN";
            case ConnectivityManager.TYPE_MOBILE_HIPRI:
                return "MOBILE_HIPRI";
            case ConnectivityManager.TYPE_WIMAX:
                return "WIMAX";
            case ConnectivityManager.TYPE_BLUETOOTH:
                return "BLUETOOTH";
            case ConnectivityManager.TYPE_DUMMY:
                return "DUMMY";
            case ConnectivityManager.TYPE_ETHERNET:
                return "ETHERNET";
            case 10: // ConnectivityManager.TYPE_MOBILE_FOTA:
                return "MOBILE_FOTA";
            case 11: // ConnectivityManager.TYPE_MOBILE_IMS:
                return "MOBILE_IMS";
            case 12: // ConnectivityManager.TYPE_MOBILE_CBS:
                return "MOBILE_CBS";
            case 13: // ConnectivityManager.TYPE_WIFI_P2P:
                return "WIFI_P2P";
            default:
                return Integer.toString(type);
        }
    }

    //中国电信
    public static final int ISP_CTCC = 0;
    //中国联通
    public static final int ISP_CUCC = 1;
    //中国移动
    public static final int ISP_CMCC = 2;
    //中国铁通
    public static final int ISP_CTT = 3;
    //其他
    public static final int ISP_OTHERS = -1;

    public static String getSimOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getSimOperator();
        }
        return null;
    }

    public static String getNetworkOperator(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            return tm.getNetworkOperator();
        }
        return null;
    }


    public interface LinkNetWorkType {
        int UNKNOWN = 0;
        int WIFI = 1;
        int WWAN = 2;
        int _2G = 3;
        int _3G = 4;
        int _4G = 5;
    }

    public static int getNetworkTypeForLink(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni.getType() == ConnectivityManager.TYPE_WIFI) {
                    return LinkNetWorkType.WIFI;
                } else {
                    if (ni.getType() == ConnectivityManager.TYPE_MOBILE) {
                        switch (ni.getSubtype()) {
                            case TelephonyManager.NETWORK_TYPE_GPRS:
                            case TelephonyManager.NETWORK_TYPE_EDGE:
                            case TelephonyManager.NETWORK_TYPE_CDMA:
                            case TelephonyManager.NETWORK_TYPE_1xRTT:
                            case TelephonyManager.NETWORK_TYPE_IDEN:
                                return LinkNetWorkType._2G;
                            case TelephonyManager.NETWORK_TYPE_UMTS:
                            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                            case TelephonyManager.NETWORK_TYPE_HSDPA:
                            case TelephonyManager.NETWORK_TYPE_HSUPA:
                            case TelephonyManager.NETWORK_TYPE_HSPA:
                            case 12: // TelephonyManager.NETWORK_TYPE_EVDO_B:
                            case 14: // TelephonyManager.NETWORK_TYPE_EHRPD:
                            case 15: // TelephonyManager.NETWORK_TYPE_HSPAP:
                                return LinkNetWorkType._3G;
                            case 13: // TelephonyManager.NETWORK_TYPE_LTE:
                                return LinkNetWorkType._4G;
                            default:
                                return LinkNetWorkType._2G;
                        }
                    }
                }
            }
        } catch (Exception e) {
            return LinkNetWorkType.UNKNOWN;
        }
        return LinkNetWorkType.UNKNOWN;
    }

    /**
     * 获取正在连接的Wifi的名称
     *
     * @return 返回Wifi的名称，未连接时返回空
     */
    public static String getSsId(Context context) {
        String ssId = "";
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (isConnectIsNormal(context) && null != wifiMgr) {
            WifiInfo info = wifiMgr.getConnectionInfo();
            info.getRssi();
            ssId = info.getSSID();
            String reg = "\".+\"";
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(ssId);
            boolean isPattern = matcher.matches();
            if (!TextUtils.isEmpty(ssId) && isPattern) {
                ssId = ssId.substring(1, ssId.length() - 1);
            }
        }
        return ssId;
    }

    public static String getBssId(Context context) {
        String bssId = "";
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (isConnectIsNormal(context) && null != wifiMgr) {
            WifiInfo info = wifiMgr.getConnectionInfo();
            info.getRssi();
            bssId = info.getBSSID();

        }
        return bssId;
    }

    /**
     * 获取Wifi密码，需要Root权限，否则获取失败
     *
     * @param wifiName Wifi名称
     * @return 返回Wifi密码
     */
    public static String getWifiPassword(String wifiName) {
        return "";
    }

    public static void connectWifi(WifiManager wifiManager, String ssid, String pwd) {
        int network = wifiManager.addNetwork(createWifiInfo(wifiManager, ssid, pwd, 3));
        wifiManager.enableNetwork(network, true);
    }

    /**
     * 创建WifiConfiguration之后连接wifi
     * @param wifiManager wifiManager
     * @param ssid ssid 帐号
     * @param pwd pwd 密码
     * @param type wifi类型
     * @return WifiConfiguration
     */
    private static WifiConfiguration createWifiInfo(WifiManager wifiManager, String ssid, String pwd, int type) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.allowedAuthAlgorithms.clear();
        wifiConfiguration.allowedPairwiseCiphers.clear();
        wifiConfiguration.allowedKeyManagement.clear();
        wifiConfiguration.allowedProtocols.clear();
        wifiConfiguration.allowedGroupCiphers.clear();
        wifiConfiguration.SSID = "\"" + ssid + "\"";
        //避免列表中重复的SSID
        WifiConfiguration tempConfig = isExsitsWifi(wifiManager, ssid);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        //wifi没有密码的
        if (type == 1) {
            wifiConfiguration.wepKeys[0] = "";
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.wepTxKeyIndex = 0;
        }
        //采用wep加密
        if (type == 2) {
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.wepKeys[0] = "\"" + pwd + "\"";
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.wepTxKeyIndex = 0;
        }
        //采用wpa/wpa2加密
        if (type == 3) {
            wifiConfiguration.preSharedKey = "\"" + pwd + "\"";
            wifiConfiguration.hiddenSSID = true;
            wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED;
        }
        return wifiConfiguration;
    }

    private static WifiConfiguration isExsitsWifi(WifiManager wifiManager, String ssid) {
        if (wifiManager == null) {
            return null;
        }
        List<WifiConfiguration> lists = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wcf : lists) {
            if (wcf.SSID.equals(ssid)) {
                return wcf;
            }
        }
        return null;
    }

    /**
     * 判断 wifi 是否是 5G 频段.
     * 需要权限:
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    public static boolean isWifi5G(Context context) {
        int freq = 0;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null == wifiManager) {
            return true;
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            String ssId = wifiInfo.getSSID();
            if (ssId != null && ssId.length() > 2) {
                String ssIdTemp = ssId.substring(1, ssId.length() - 1);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssIdTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }
}
