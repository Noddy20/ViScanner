package com.viscanner.viscanner2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class ViScanBuilder {

    public static final int RESULT_NOT_FOUND = 0, RESULT_OK = 1, RESULT_SUBMITTED = 2,
                       FILE_NOT_FOUND = 3, INVALID_URL = 4, REQUEST_LIMIT_EXCEEDED = 5,
                       BAD_REQUEST = 6, INVALID_API_KEY = 7, OTHER_REASON = 8;

    private String apiKey;
    private Context context;
    private ScanListener scanListener;

    public ViScanBuilder(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    public void scanUrl(String url){
        List<String> urls = new ArrayList<>();
        urls.add(url);
        scanUrls(urls);
    }

    public void scanUrls(List<String> urls){
        if (context != null) {
            if (urls == null) {
                Toast.makeText(context, R.string.empty_urls, Toast.LENGTH_SHORT).show();
            } else if (urls.isEmpty()) {
                Toast.makeText(context, R.string.empty_urls, Toast.LENGTH_SHORT).show();
            } else {
                if (!ScannerService.isServiceCreated()) {
                    Intent scanServc = new Intent(context, ScannerService.class);
                    scanServc.putExtra("apiKey", apiKey);
                    scanServc.putExtra("scanType", 1);  // url scan
                    scanServc.putStringArrayListExtra("scanUrls", (ArrayList<String>) urls);
                    context.startService(scanServc);
                    if (scanListener != null) {
                        ScannerService.setScanListener(scanListener);
                    }
                }else {
                    Toast.makeText(context, R.string.scan_running, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void scanFile(String filePath){
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        scanFiles(filePaths);
    }

    public void scanFiles(List<String> filePaths){
        if (context != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (filePaths == null) {
                    Toast.makeText(context, R.string.empty_file_paths, Toast.LENGTH_SHORT).show();
                } else if (filePaths.isEmpty()) {
                    Toast.makeText(context, R.string.empty_file_paths, Toast.LENGTH_SHORT).show();
                } else {
                    if (!ScannerService.isServiceCreated()){
                    Intent scanServc = new Intent(context, ScannerService.class);
                    scanServc.putExtra("apiKey", apiKey);
                    scanServc.putExtra("scanType", 2);  // file scan
                    scanServc.putStringArrayListExtra("scanFiles", (ArrayList<String>) filePaths);
                    context.startService(scanServc);
                    if (scanListener != null){
                        ScannerService.setScanListener(scanListener);
                    }
                    }else {
                        Toast.makeText(context, R.string.scan_running, Toast.LENGTH_SHORT).show();
                    }
                }
            }else {
                Toast.makeText(context, R.string.grant_storage_perm, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void setScanListener(ScanListener scanListener){
        this.scanListener = scanListener;
    }

}
