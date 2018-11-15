package com.example.heegi.d2dconnection;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.heegi.d2dconnection.Collect4Learning.CollectActivity;
import com.example.heegi.d2dconnection.ConnectByLearning.ConnectActivity;
import com.example.heegi.d2dconnection.Singleton.SsidTable;

public class MainActivity extends AppCompatActivity {
    private Button collect;
    private Button learning;
    private Button notlearning;

    private ClickToRouter clickToRouter;

    private EditText ssid1_Id;
    private EditText ssid1_pw;
    
    private EditText ssid2_Id;
    private EditText ssid2_pw;
    
    private EditText ssid3_Id;
    private EditText ssid3_pw;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickToRouter = new ClickToRouter();
        
        ssid1_Id = findViewById(R.id.ssid1_id);
        ssid2_Id = findViewById(R.id.ssid2_id);
        ssid3_Id = findViewById(R.id.ssid3_id);

        ssid1_pw = findViewById(R.id.ssid1_pw);
        ssid2_pw = findViewById(R.id.ssid2_pw);
        ssid3_pw = findViewById(R.id.ssid3_pw);

        collect = findViewById(R.id.collect);
        collect.setOnClickListener(clickToRouter);

        learning = findViewById(R.id.learning);
        learning.setOnClickListener(clickToRouter);

        notlearning = findViewById(R.id.not_learning);
        notlearning.setOnClickListener(clickToRouter);

    }
    private class ClickToRouter implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(ssid1_Id.getText().toString().equals("")){
                SsidTable.getInstance().setSsid1Id("SSID_1");
            }else{
                SsidTable.getInstance().setSsid1Id(ssid1_Id.getText().toString());
            }
            if(ssid2_Id.getText().toString().equals("")){
                SsidTable.getInstance().setSsid2Id("SSID_2");
            }else {
                SsidTable.getInstance().setSsid2Id(ssid2_Id.getText().toString());
            }
            if(ssid3_Id.getText().toString().equals("")){
                SsidTable.getInstance().setSsid3Id("SSID_3");
            }else {
                SsidTable.getInstance().setSsid3Id(ssid3_Id.getText().toString());
            }
            SsidTable.getInstance().setSsid1Pw(ssid1_pw.getText().toString());
//            SsidTable.getInstance().setSsid1Pw("12345678");
            SsidTable.getInstance().setSsid2Pw(ssid2_pw.getText().toString());
//            SsidTable.getInstance().setSsid2Pw("41597838");
            SsidTable.getInstance().setSsid3Pw(ssid3_pw.getText().toString());
//            SsidTable.getInstance().setSsid3Pw("12345678");

            Intent intent;
            switch (v.getId()){
                case R.id.collect:
                    intent = new Intent(MainActivity.this,CollectActivity.class);
                    startActivity(intent);
                    break;
                case R.id.learning:
                    intent = new Intent(MainActivity.this,ConnectActivity.class);
                    startActivity(intent);
                    break;
                case R.id.not_learning:
                    intent = new Intent(MainActivity.this, com.example.heegi.d2dconnection.ConnectNormal.ConnectActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }
}
