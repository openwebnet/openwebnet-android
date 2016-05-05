package com.github.openwebnet.service;

import android.widget.TextView;

public interface UtilityService {

    String getString(int id);

    boolean hasNetworkAccess();

    boolean hasInternetAccess();

    <T extends TextView> boolean isBlankText(T view);

    <T extends TextView> String sanitizeText(T view);

}
