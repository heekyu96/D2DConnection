package com.example.heegi.d2dconnection.Collect4Learning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.heegi.d2dconnection.R;
import com.example.heegi.d2dconnection.Singleton.SsidTable;

public class CollectActivity extends AppCompatActivity {
    private TextView logView;
    private Button sendButton;
    private EditText editText;

    private WifiScanResultReceiver wifiScanResultReceiver;
    private static final IntentFilter wifiScanResultFilter = new IntentFilter("WifiScanResult");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        logView = findViewById(R.id.log);
        logView.setText("");
        logView.setMovementMethod(ScrollingMovementMethod.getInstance());
        sendButton = findViewById(R.id.send);
        editText = findViewById(R.id.targetAP);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WifiSearchService4Collect.class);
                intent.putExtra("target",editText.getText().toString());
                startService(intent);
            }
        });



        wifiScanResultReceiver = new WifiScanResultReceiver();
        registerReceiver(wifiScanResultReceiver, wifiScanResultFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(getApplicationContext(), WifiSearchService4Collect.class);
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
        }
    }
}
