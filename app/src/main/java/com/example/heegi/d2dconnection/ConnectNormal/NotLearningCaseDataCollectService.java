package com.example.heegi.d2dconnection.ConnectNormal;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.heegi.d2dconnection.Singleton.NetworkConnector;
import com.example.heegi.d2dconnection.Singleton.SsidTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NotLearningCaseDataCollectService extends Service {


    private WifiManager wifiManager;
    private WifiScanReceiver wifiScanReceiver;
    private List<ScanResult> scanResults;

    private static final IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiScanReceiver = new WifiScanReceiver();
        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
        Log.d("WifiService", "OnCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WifiService", "OnStartConmmand");
//        D_AP1 = Integer.valueOf(intent.getStringExtra("D_AP1"));
//        D_AP2 = Integer.valueOf(intent.getStringExtra("D_AP2"));
//        target = Integer.valueOf(intent.getStringExtra("target"));
//        Log.d("IntentTest", D_AP1 + "/" + D_AP2 + " : " + target);

        getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);
        wifiManager.startScan();
        String ssid;
//        ssid = intent.getStringExtra("target");


        return super.onStartCommand(intent, flags, startId);
    }

    private class WifiScanReceiver extends BroadcastReceiver {
        int AP1Rssi;
        int AP2Rssi;
        int AP3Rssi;

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            AP1Rssi = -100;
            AP2Rssi = -100;
            AP3Rssi = -100;

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = wifiManager.getScanResults();
                for (int i = 0; i < scanResults.size(); i++) {
                    ScanResult result = scanResults.get(i);
                    if (result.SSID.equals(SsidTable.getInstance().getSsid1Id())) {
                        Log.d("ScanResultLog", (i + 1) + ". SSID : " + result.SSID
                                + "RSSI : " + result.level + " dBm\n");
                        AP1Rssi = result.level;
                        if(AP1Rssi<-60){
                            AP1Rssi= -100;
                        }
                    }

                    if (result.SSID.equals(SsidTable.getInstance().getSsid2Id())) {
                        Log.d("ScanResultLog", (i + 1) + ". SSID : " + result.SSID
                                + "RSSI : " + result.level + " dBm\n");
                        AP2Rssi = result.level;
                        if(AP2Rssi<-60){
                            AP2Rssi= -100;
                        }
                    }

                    if (result.SSID.equals(SsidTable.getInstance().getSsid3Id())) {
                        Log.d("ScanResultLog", (i + 1) + ". SSID : " + result.SSID
                                + " RSSI : " + result.level + " dBm\n");
                        AP3Rssi = result.level;
                        if(AP3Rssi<-60){
                            AP3Rssi= -100;
                        }
                    }

                    Log.d("TestTTT",scanResults.get(i).toString());

                }

            }
            //Data for Server
//            CollectingData collectingData = new CollectingData();
//            collectingData.execute(target + "", AP1Rssi + "", AP2Rssi + "", AP3Rssi + "");
//            wifiManager.startScan();
            //Data for DL
            Intent local  = new Intent();
            local.setAction("WifiScanResult");
            local.putExtra(SsidTable.getInstance().getSsid1Id(),AP1Rssi);
            local.putExtra(SsidTable.getInstance().getSsid2Id(),AP2Rssi);
            local.putExtra(SsidTable.getInstance().getSsid3Id(),AP3Rssi);
            context.sendBroadcast(local);
            wifiManager.startScan();
        }

    }

    public static int getConnectivity(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            return 1;
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ServiceDistoy", "");
    }



}
