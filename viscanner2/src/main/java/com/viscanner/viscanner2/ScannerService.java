package com.viscanner.viscanner2;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScannerService extends Service {

    private String TAG = "ScannerService";
    private static ScannerService mInstance = null;
    private HttpURLConnection urlConnection;
    private int scanType = 0;
    private String apiKey = "";
    private List<String> scanUrls, scanFilePaths, scanResults;
    private static ScanListener scanListener;
    private Handler uiHandler;

    @Override
    public void onCreate() {
        //Log.v(TAG,"Contact Sync Service created.");
        mInstance = this;
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        //Log.v(TAG,"Contact Sync Service started.");
        mInstance = this;
        Log.v(TAG, "Scan Service Created.");

        if (intent != null){
            apiKey = intent.getStringExtra("apiKey");
            scanType = intent.getIntExtra("scanType", 0);
            if (scanType == 1){
                scanUrls = intent.getStringArrayListExtra("scanUrls");
            }
            if (scanType == 2){
                scanFilePaths = intent.getStringArrayListExtra("scanFiles");
            }
        }

        uiHandler = new Handler();
        scanResults = new ArrayList<>();

        if (apiKey != null && scanType != 0){
            if ((scanType == 1 && scanUrls != null) || (scanType == 2 && scanFilePaths != null)){
                ScannerTask contactsLoader = new ScannerTask();
                contactsLoader.execute();
            }
        }

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"Scan Service Destroyed");
        mInstance = null;
    }

    public static boolean isServiceCreated() {
        try {
            // If instance was not cleared but the service was destroyed an Exception will be thrown
            return mInstance != null && mInstance.ping();
        } catch (NullPointerException e) {
            // destroyed/not-started
            return false;
        }
    }

    /**
     * Simply returns true. If the service is still active, this method will be accessible.
     * @return
     */
    private boolean ping() {
        return true;
    }

    public static void setScanListener(ScanListener listener){
        scanListener = listener;
    }

    /** An AsyncTask class to retrieve and load recyclerview with contacts */
    private class ScannerTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            //Log.v(TAG, "ContactLoader 1");
            if (scanType == 1){
                for (String url: scanUrls){
                    scanSingleItem(url);
                }
            }else {
                for (String path: scanUrls){
                    File file = new File(path);
                    if (file.exists()) {
                        String fileHash = FileUtils.fileToMD5(path);
                        scanSingleItem(fileHash);
                    }else {
                        sendScanFailed(ViScanBuilder.FILE_NOT_FOUND, null);
                        scanResults.add(null);
                    }
                }
            }
            if (scanListener != null) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        scanListener.scanFinalResult(scanResults);
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // Setting the cursor containing contacts to listview
            if (mInstance != null){
                mInstance.stopSelf();
            }
        }
    }

    private void scanSingleItem(String scanData){
        String url;
        if (scanType == 1){
            url = "https://www.virustotal.com/vtapi/v2/url/report?apikey="
                    + apiKey + "&resource=" + scanData +"&scan=1";
        }else {
            url = "https://www.virustotal.com/vtapi/v2/file/report?apikey="
                    + apiKey+ "&resource=" + scanData;
        }
        String result = getJSONFromURLConnection(url);
        if (result != null) {
            Log.v(TAG, "Scan Result: "+result);
            try {
                JSONObject obj = new JSONObject(result);
                final int response = obj.getInt("response_code");
                final int total = obj.getInt("total");
                final int positives = obj.getInt("positives");
                if (scanListener != null) {
                    final String finalResult = result;
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            scanListener.scanItemResult(response, total, positives, finalResult);
                        }
                    });
                }
                Log.v(TAG, "Response "+response + " tot "+total+ " pos "+positives+(scanListener != null));
            } catch (JSONException e) {
                Log.v(TAG, "Response Exc "+Log.getStackTraceString(e));
                sendScanFailed(ViScanBuilder.OTHER_REASON, e);
                result = null;
            }
        }
        scanResults.add(result);
    }

    private String getJSONFromURLConnection(String urlString) {
        int timeout = 100;
        HttpURLConnection c = null;
        try {
            URL u = new URL(urlString);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
           // c.setConnectTimeout(timeout);
           // c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 204:
                    sendScanFailed(ViScanBuilder.REQUEST_LIMIT_EXCEEDED, null);
                    Log.v(TAG, "Request rate limit exceeded");
                    break;
                case 400:
                    sendScanFailed(ViScanBuilder.BAD_REQUEST, null);
                    Log.v(TAG, "Bad Request");
                    break;
                case 403:
                    sendScanFailed(ViScanBuilder.INVALID_API_KEY, null);
                    Log.v(TAG, "Forbidden or Wrong API Key.");
                    break;
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();

            }

        } catch (MalformedURLException ex) {
            sendScanFailed(ViScanBuilder.OTHER_REASON, ex);
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            sendScanFailed(ViScanBuilder.OTHER_REASON, ex);
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    private void sendScanFailed(final int cause, final Exception e){
        if (scanListener != null) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    scanListener.scanItemFailed(cause, e);
                }
            });
        }
    }

}