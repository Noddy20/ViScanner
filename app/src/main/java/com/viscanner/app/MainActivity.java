package com.viscanner.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.viscanner.viscanner2.ScanListener;
import com.viscanner.viscanner2.ViScanBuilder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textview);

        String apiKey = "Your API Key";
        ViScanBuilder viScanBuilder = new ViScanBuilder(MainApplication.getAppContext(), apiKey);
        viScanBuilder.setScanListener(scanListener);

        List<String> urls = new ArrayList<>();
        urls.add("www.google.com");
        urls.add("www.youtube.com");
        urls.add("www.facebook.com");
        urls.add("www.blogger.net");
        urls.add("www.amazon.com");
        viScanBuilder.scanUrls(urls);
    }

    private ScanListener scanListener = new ScanListener() {

        @Override
        public void scanItemFailed(int cause, @Nullable Exception e) {
            Log.v(TAG, "Scan Failed "+cause+ " e "+e);
        }

        @Override
        public void scanItemResult(int status, int totalAv, int positives, String jsonResult) {
            textView.setText("Status "+status+" Total AV "+totalAv+ " Positives "+positives);
            Log.v(TAG, "Scan Status "+status+" Total AV "+totalAv+ " Positives "+positives);
        }

        @Override
        public void scanFinalResult(List<String> jsonResults) {
            Log.v(TAG, "json Res "+jsonResults.size());
        }
    };
}
