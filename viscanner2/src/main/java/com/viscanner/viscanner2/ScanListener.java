package com.viscanner.viscanner2;

import androidx.annotation.Nullable;

import java.util.List;

public interface ScanListener {

    void scanItemFailed(int cause, @Nullable Exception e);

    void scanItemResult(int status, int totalAv, int positives, String jsnResult);

    void scanFinalResult(List<String> jsonResults);

}
