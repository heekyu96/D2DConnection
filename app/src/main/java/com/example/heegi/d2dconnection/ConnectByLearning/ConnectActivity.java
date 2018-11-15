package com.example.heegi.d2dconnection.ConnectByLearning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heegi.d2dconnection.Singleton.NetworkConnector;
import com.example.heegi.d2dconnection.R;
import com.example.heegi.d2dconnection.Singleton.SsidTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {
    private TextView logView;
    private Button sendButton;

    private WifiScanResultReceiver wifiScanResultReceiver;
    private static final IntentFilter wifiScanResultFilter = new IntentFilter("WifiScanResult");

    private LearingResultReceiver learingResultReceiver;
    private static final IntentFilter learningResultFilter = new IntentFilter("LearningResult");

    // TODO: 2018-11-14 this is for send Data to Server Query after Connection
    private WifiStateReciever wifiStateReciever;
    private static final IntentFilter stateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    private ConnectivityManager connectivityManager;
    private WifiManager wifiManager;

    private String currentSSID;

    private static int seqNo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_test);

        currentSSID = "defaultw";
        seqNo = 0;
        connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        logView = findViewById(R.id.log);
        logView.setText("");
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        sendButton = findViewById(R.id.send);

        Intent intent = new Intent(getApplicationContext(), WifiSearchService4Learning.class);
        startService(intent);

        wifiScanResultReceiver = new WifiScanResultReceiver();
        registerReceiver(wifiScanResultReceiver, wifiScanResultFilter);
        learingResultReceiver = new LearingResultReceiver();
//        registerReceiver(learingResultReceiver,learningResultFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(learingResultReceiver, learningResultFilter);

        wifiStateReciever = new WifiStateReciever();
        registerReceiver(wifiStateReciever, stateFilter);
    }

    private class WifiScanResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("wifiScanReceiver", "start");
            Intent learingService = new Intent(getApplicationContext(), LearningService.class);
            learingService.setAction("rssiResult");

            logView.append(intent.getIntExtra(SsidTable.getInstance().getSsid1Id(), -100) + "/");
            logView.append(intent.getIntExtra(SsidTable.getInstance().getSsid2Id(), -100) + "/");
            logView.append(intent.getIntExtra(SsidTable.getInstance().getSsid3Id(), -100) + "/");
            logView.append(intent.getIntExtra("AP9", -100) + "");
            logView.append("\n");

            Log.d("TestTEST1234", intent.getIntExtra(SsidTable.getInstance().getSsid1Id(), -100) + "");
            learingService.putExtra("AP1", intent.getIntExtra(SsidTable.getInstance().getSsid1Id(), -100));
            learingService.putExtra("AP2", intent.getIntExtra(SsidTable.getInstance().getSsid2Id(), -100));
            learingService.putExtra("AP3", intent.getIntExtra(SsidTable.getInstance().getSsid3Id(), -100));
            learingService.putExtra("AP4", intent.getIntExtra("AP4", -100));
            learingService.putExtra("AP5", intent.getIntExtra("AP5", -100));
            learingService.putExtra("AP6", intent.getIntExtra("AP6", -100));
            learingService.putExtra("AP7", intent.getIntExtra("AP7", -100));
            learingService.putExtra("AP8", intent.getIntExtra("AP8", -100));
            learingService.putExtra("AP9", intent.getIntExtra("AP9", -100));
            startService(learingService);
        }
    }

    private class LearingResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra("learningResult", 0);
            if (result == 1) {
                connect(SsidTable.getInstance().getSsid1Id(), SsidTable.getInstance().getSsid1Pw());
//                Toast.makeText(getApplicationContext(), result + "", Toast.LENGTH_SHORT).show();
            } else if (result == 2) {
                connect(SsidTable.getInstance().getSsid2Id(), SsidTable.getInstance().getSsid2Pw());
//                Toast.makeText(getApplicationContext(), result + "", Toast.LENGTH_SHORT).show();
            } else if (result == 3) {
                connect(SsidTable.getInstance().getSsid3Id(), SsidTable.getInstance().getSsid3Pw());
//                Toast.makeText(getApplicationContext(), result + "", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), result + "UNKNOWN", Toast.LENGTH_SHORT).show();
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo.getSSID().equals("\"" + currentSSID + "\"")) {
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                        Date mDate = new Date(System.currentTimeMillis());
                        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");

                        AddDataSend addDataSend = new AddDataSend();
                        addDataSend.execute(currentSSID, mFormat.format(mDate), String.valueOf(++seqNo));
                    }
                    return;
                }
            }

            // TODO: 2018-11-13 터지는부분
//            Date mDate = new Date(System.currentTimeMillis());
//            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");
//
//            AddDataSend addDataSend = new AddDataSend();
//            addDataSend.execute(currentSSID, mFormat.format(mDate), String.valueOf(++seqNo));

            Log.d("testLRR", result + "");
        }

    }

    private class WifiStateReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "connectivity", Toast.LENGTH_SHORT).show();
            NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            Log.d("TestSR", wifi.isConnected() + "");
            if (wifi.isConnected()) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                TimeStamp timeStamp = new TimeStamp();
//                timeStamp.execute(wifiInfo.getSSID());
                Date mDate = new Date(System.currentTimeMillis());
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");

                AddDataSend addDataSend = new AddDataSend();
                addDataSend.execute(currentSSID, mFormat.format(mDate), String.valueOf(++seqNo));
            }
        }
    }

    private class AddDataSend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String url = NetworkConnector.getInstance().getUrl() + "addData.php";
            strings[0] = strings[0].replaceAll("\"", "");
            url = url + "?ssid=" + strings[0];
            url = url + "&getTime="+strings[1];
            url = url + "&sequence=" + strings[2];
            Log.d("ADurl", url);
            String result = NetworkConnector.getInstance().get(url);
            Log.d("ADSending ", result);
            return null;
        }
    }

    public void connect(String ssid, String pw) {
        // TODO: 2018-11-13 Data Sending with Seq_No after Connection
        // 만약 같은 ssid를 받으면 그냥 센딩
        //연결후 데이터 센딩
        String TAG = "ConnectWifi";
        Log.d(TAG, "Inside addWifiConfig...");

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSSID().equals("\"" + ssid + "\"")) {
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                Date mDate = new Date(System.currentTimeMillis());
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");

                AddDataSend addDataSend = new AddDataSend();
                addDataSend.execute(currentSSID, mFormat.format(mDate), String.valueOf(++seqNo));
            }
            return;
        }

        Toast.makeText(getApplicationContext(), ssid, Toast.LENGTH_SHORT).show();
        currentSSID = ssid;

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
//        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.preSharedKey = "\"".concat(pw).concat("\"");

        //Connect to the network
        int networkId = wifiManager.addNetwork(conf);
        Log.v(TAG, "Add result " + networkId);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + ssid + "\"")) {
                Log.v(TAG, "WifiConfiguration SSID " + i.SSID);
                boolean isDisconnected = wifiManager.disconnect();
                Log.v(TAG, "isDisconnected : " + isDisconnected);
                boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                Log.v(TAG, "isEnabled : " + isEnabled);
                boolean isReconnected = wifiManager.reconnect();
                Log.v(TAG, "isReconnected : " + isReconnected);
                break;
            }
        }


    }
}
