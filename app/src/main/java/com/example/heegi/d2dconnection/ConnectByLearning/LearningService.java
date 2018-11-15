package com.example.heegi.d2dconnection.ConnectByLearning;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

public class LearningService extends Service {
    private final static int inputNodes = 9;
    private static NeuralNetwork neuralNetwork;
    private static boolean isInitialized = false;

    private int result;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
//            neuralNetwork = new NeuralNetwork(inputNodes, 9, 9, getAssets().open("case_Short_distance/wih.csv"), getAssets().open("case_Short_distance/who.csv"));
            neuralNetwork = new NeuralNetwork(inputNodes, 9, 9, getAssets().open("case_Long_distance/wih.csv"), getAssets().open("case_Long_distance/who.csv"));
            isInitialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LearningService", "onStartCommand");
        double[] input = new double[inputNodes];
//        input[0]=-55.0;
//        input[1]=-46.0;
//        input[2]=-22.0;
        Log.d("TestTEST",intent.getIntExtra("AP1", -100)+"");
        input[0] = intent.getIntExtra("AP1", -100);
        input[1] = intent.getIntExtra("AP2", -100);
        input[2] = intent.getIntExtra("AP3", -100);
        input[3] = intent.getIntExtra("AP4", -100);
        input[4] = intent.getIntExtra("AP5", -100);
        input[5] = intent.getIntExtra("AP6", -100);
        input[6] = intent.getIntExtra("AP7", -100);
        input[7] = intent.getIntExtra("AP8", -100);
        input[8] = intent.getIntExtra("AP9", -100);

        for (int i = 0; i < inputNodes; i++) {
            input[i] = ((input[i] / 100.0) * (0.99)) + 0.99;
        }
        Log.d("inputToGray", input[0] + "/" + input[1] + "/" + input[2] + "/" + input[3] + "/" + input[8]);

        if (isInitialized) {
//            Log.d("beforeQuery",input[0]+"/"+input[1]+"/"+input[2]+"/"+input[3]+"/"+input[4]+"/"+input[5]+"/"+input[6]+"/"+input[7]+"/"+input[8]);

            result = neuralNetwork.query(input);
            sendMessage(result);
//            Toast.makeText(this, "" + result, Toast.LENGTH_SHORT).show();

        } else {
            onCreate();
        }

        Intent wifiScan = new Intent(getApplicationContext(), WifiSearchService4Learning.class);
        startService(wifiScan);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sendMessage(int result) {
        Log.d("messageService", "Broadcasting message");
        Intent intent = new Intent("LearningResult");
        intent.putExtra("learningResult", result);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private class WifiRssiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent local = new Intent();
            local.setAction("learningResult");
            local.putExtra("result", result);
            context.sendBroadcast(intent);
        }
    }
}
