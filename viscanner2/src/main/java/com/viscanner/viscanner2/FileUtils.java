package com.viscanner.viscanner2;

import android.util.Base64;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {

    private static String TAG = "FileUtils";

    public static String fileToSHA256(String filePath){
        byte[] buffer= new byte[8192];
        int count;
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
            while ((count = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
            bis.close();
            byte[] hash = digest.digest();
            return Base64.encodeToString(hash, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | IOException e) {
            Log.v(TAG, "SHA-256 Exc "+Log.getStackTraceString(e));
        }
        return null;
    }

    public static String fileToMD5(String filePath){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) { }
            }
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal.toUpperCase();
    }


}
