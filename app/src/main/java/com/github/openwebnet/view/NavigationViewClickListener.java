package com.github.openwebnet.view;

import android.view.View;
import android.view.View.OnClickListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationViewClickListener implements OnClickListener {

    private static final Logger log = LoggerFactory.getLogger(NavigationViewClickListener.class);

    private final int environmentId;

    public NavigationViewClickListener(int environmentId) {
        this.environmentId = environmentId;
    }

    @Override
    public void onClick(View v) {
        log.debug("onClick: {}", environmentId);
    }
}
