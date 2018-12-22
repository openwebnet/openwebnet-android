package com.github.openwebnet.service;

import android.widget.TextView;

import java.util.Date;

public interface UtilityService {

    String getString(int id);

    boolean hasNetworkAccess();

    boolean hasInternetAccess();

    <T extends TextView> boolean isBlankText(T view);

    <T extends TextView> String sanitizedText(T view);

    String formatDate(Date date);

}
