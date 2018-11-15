package com.example.heegi.d2dconnection.ConnectNormal;

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
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heegi.d2dconnection.R;
import com.example.heegi.d2dconnection.Singleton.NetworkConnector;
import com.example.heegi.d2dconnection.Singleton.SsidTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConnectActivity extends AppCompatActivity {
    private TextView logView;
    private Button sendButton;
    private EditText editText;

    private WifiScanResultReceiver wifiScanResultReceiver;
    private static final IntentFilter wifiScanResultFilter = new IntentFilter("WifiScanResult");

    private WifiStateReciever wifiStateReciever;
    private static final IntentFilter stateFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);


    ConnectivityManager connectivityManager;
    WifiManager wifiManager;

    static int seqNo;

    int max;
    int ap1;
    int ap2;
    int ap3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_learning);

        logView = findViewById(R.id.log);
        logView.setText("");
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        sendButton = findViewById(R.id.send);
        editText = findViewById(R.id.targetAP);

        connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        seqNo = 0;
//        max = 1;

        wifiScanResultReceiver = new WifiScanResultReceiver();
        registerReceiver(wifiScanResultReceiver, wifiScanResultFilter);

        wifiStateReciever = new WifiStateReciever();
        registerReceiver(wifiStateReciever, stateFilter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotLearningCaseDataCollectService.class);
                intent.putExtra("target", editText.getText().toString());
                startService(intent);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), NotLearningCaseDataCollectService.class);
        stopService(intent);
    }

    private class WifiScanResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("wifiScanReceiver", "start");
//            Intent learingService = new Intent(getApplicationContext(), LearningService.class);
//            learingService.setAction("rssiResult");
            logView.append(intent.getIntExtra(SsidTable.getInstance().getSsid1Id(), -100) + "/");
            logView.append(intent.getIntExtra(SsidTable.getInstance().getSsid2Id(), -100) + "/");
            logView.append(intent.getIntExtra(SsidTable.getInstance().getSsid3Id(), -100) + "/");
            logView.append(intent.getIntExtra("AP9", -100) + "");
            logView.append("\n");
            ap1 = intent.getIntExtra(SsidTable.getInstance().getSsid1Id(), -100);
            ap2 = intent.getIntExtra(SsidTable.getInstance().getSsid2Id(), -100);
            ap3 = intent.getIntExtra(SsidTable.getInstance().getSsid3Id(), -100);


            NetworkInfo wifi = connectivityManager.getActiveNetworkInfo();
            if (wifi == null) {
                //끊어지면 다음으로 센거 잡기
                max = 1;
                if (ap1 < ap2) {
                    max = 2;
                }
                if (ap2 < ap3) {
                    max = 3;
                }
                Toast.makeText(context, "connectivitynuill" + max, Toast.LENGTH_SHORT).show();
                switch (max) {
                    case 1:
                        connect(SsidTable.getInstance().getSsid1Id(), SsidTable.getInstance().getSsid1Pw());
                        break;
                    case 2:
                        connect(SsidTable.getInstance().getSsid2Id(), SsidTable.getInstance().getSsid2Pw());
                        break;
                    case 3:
                        connect(SsidTable.getInstance().getSsid3Id(), SsidTable.getInstance().getSsid3Pw());
                        break;
                }

            }else{
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                Date mDate = new Date(System.currentTimeMillis());
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");
                AddDataSend addDataSend = new AddDataSend();
                addDataSend.execute(wifiInfo.getSSID().replace("\"", ""), mFormat.format(mDate), String.valueOf(++seqNo));

                if(wifiInfo.getRssi()<-60){
                    wifiManager.disconnect();
                }
            }

        }
    }

    private class WifiStateReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context, "connectivity", Toast.LENGTH_SHORT).show();
            NetworkInfo wifi = connectivityManager.getActiveNetworkInfo();
//            Log.d("TestSR", wifi.isConnected() + "");
            if (wifi == null) {
                //끊어지면 다음으로 센거 잡기
                max = 1;
                if (ap1 < ap2) {
                    max = 2;
                }
                if (ap2 < ap3) {
                    max = 3;
                }
                Toast.makeText(context, "connectivitynuill" + max, Toast.LENGTH_SHORT).show();
                switch (max) {
                    case 1:
                        connect(SsidTable.getInstance().getSsid1Id(), SsidTable.getInstance().getSsid1Pw());
                        break;
                    case 2:
                        connect(SsidTable.getInstance().getSsid2Id(), SsidTable.getInstance().getSsid2Pw());
                        break;
                    case 3:
                        connect(SsidTable.getInstance().getSsid3Id(), SsidTable.getInstance().getSsid3Pw());
                        break;
                }

            } else {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//                TimeStamp timeStamp = new TimeStamp();
//                timeStamp.execute(wifiInfo.getSSID());
                Date mDate = new Date(System.currentTimeMillis());
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");

                AddDataSend addDataSend = new AddDataSend();
                addDataSend.execute(wifiInfo.getSSID().replace("\"", ""), mFormat.format(mDate), String.valueOf(++seqNo));
            }
        }
    }

    private class AddDataSend extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String url = NetworkConnector.getInstance().getUrl() + "addData.php";
            strings[0] = strings[0].replaceAll("\"", "");
            url = url + "?ssid=" + strings[0];
            url = url + "&getTime=" + strings[1];
            url = url + "&sequence=" + strings[2];
            Log.d("ADurl", url);
            String result = NetworkConnector.getInstance().get(url);
//            Log.d("ADSending ", result);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public void connect(String ssid, String pw) {
        // TODO: 2018-11-13 Data Sending with Seq_No after Connection
        // 만약 같은 ssid를 받으면 그냥 센딩
        //연결후 데이터 센딩
        String TAG = "ConnectWifi";
        Log.d(TAG, "Inside addWifiConfig...");

//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        if (wifiInfo.getSSID().equals("\"" + ssid + "\"")) {
//            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
//                Date mDate = new Date(System.currentTimeMillis());
//                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd:hh:mm:ss");
//
//                AddDataSend addDataSend = new AddDataSend();
//                addDataSend.execute(currentSSID, mFormat.format(mDate), String.valueOf(++seqNo));
//            }
//            return;
//        }

        Toast.makeText(getApplicationContext(), ssid, Toast.LENGTH_SHORT).show();
//        currentSSID = ssid;

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
