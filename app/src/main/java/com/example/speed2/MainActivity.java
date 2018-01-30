package com.example.speed2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;//位置控制器
    private String mlocation = "";
    private TextView t;
    private static final String TGA = "MainActivity";
    private Criteria criteria = new Criteria();
    private Handler handler = new Handler();
    private boolean LocationEnable = false;
    private  TextView sp;
    private  int  i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initGPS();
        initWidget();
        initLocation();
        handler.postDelayed(refresh,1000);
    }

    /* 检查gps状态并引导用户打开gps */

    private void initGPS() {
        LocationManager locationManagers = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则跳转至设置开启界面，设置完毕后返回到当前页面
        if (!locationManagers.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder da = new AlertDialog.Builder(this);
            da.setTitle("提示：");
            da.setMessage("本软件不消耗流量，但请您打开您的GPS!");
            da.setCancelable(false);
            //设置左边按钮监听

            da.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                           startActivityForResult(intent, 0); // 设置完成后返回到原来的界面

                }
            });
//            da.setNeutralButton("确定",
//                    new DialogInterface.OnClickListener(){
//                       // @Override
//                        public void onClick(DialogPreference arg0, int arg1) {
//
//                            // 转到手机设置界面，用户设置GPS
//                            Intent intent = new Intent(
//                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
//
//                        }
//                    });
            //设置右边按钮监听
            da.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    //MainActivity.super.onDestroy();
                }
            });
//            da.setPositiveButton("取消",
//                    new DialogInterface.OnClickListener() {
//                       // @Override
//                        public void onClick(DialogPreference arg0, int arg1) {
//                           // arg0.dismiss();
//                            MainActivity.super.onDestroy();
//                        }
//                    });
            da.show();
        } else {
        }
    }

    private void initWidget() {
        t = (TextView) findViewById(R.id.t1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(true);
    }

    private void initLocation() {
        String bestProvide = locationManager.getBestProvider(criteria, true);
        if (bestProvide == null)
            bestProvide = LocationManager.NETWORK_PROVIDER;
        if (locationManager.isProviderEnabled(bestProvide)) {
            t.setText("正在获取" + bestProvide + "定位对象,请稍等，请再稍等。。。。\n程序已经很努力了（QWQ）");
            mlocation = "定位类型=" + bestProvide;
            beginLocation(bestProvide);
            LocationEnable  = true;
        }
        else {
            t.setText("定位不可用，请打开GPS，在没有网络的情况下依旧可以定位");
            LocationEnable = false;
        }
    }

    private  void setLotionText(Location location){
        if (location!=null)
        {
            String desc = mlocation+"\n定位对象如下：\n经度："+location.getLongitude()+"\n纬度："+location.getLatitude() +
                "\n海拔："+Math.round(location.getAltitude())+"\n精度："+Math.round(location.getAccuracy())+"米";
            String sep = "速度:\n"+location.getSpeed()*3.6+"Km/h";
            if (location.getAccuracy()>100)
            {
                Toast.makeText(MainActivity.this,"精度大于100米，结果误差较大",Toast.LENGTH_SHORT).show();
            }
            t.setText(desc);
            sp = (TextView)findViewById(R.id.textView);
            sp.setText(sep);
        }
        else
            t.setText("正在获取定位对象，如果长时间没有获取到，说明设备所在地GPS信号弱，请耐心等候。");
    }
    private void beginLocation(String method) {
            locationManager.requestLocationUpdates(method,300,0,locationListener);
            Location location =locationManager.getLastKnownLocation(method);
            setLotionText(location);
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
                setLotionText(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private Runnable refresh = new Runnable() {
        @Override
        public void run() {
            if(LocationEnable==false)
            {
                initLocation();
                handler.postDelayed(this,1000);
            }
        }
    };
    private void onDestory()
    {
        if (locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
        super.onDestroy();
    }
    public void onBackPressed()
    {
        if(i==0)
        {
            AlertDialog.Builder ds = new AlertDialog.Builder(this);
            ds.setTitle("提示");
            ds.setMessage("本软件不会自动关闭GPS，为节省电量，请用户从后台关闭软件！");
            ds.setCancelable(false);
            ds.setNeutralButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.super.onBackPressed();
                }
            });
            ds.show();
        }
        //Toast.makeText(MainActivity.this,"danhu",Toast.LENGTH_SHORT).show();
        else  super.onBackPressed();
    }
}