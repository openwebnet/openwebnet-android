package com.github.openwebnet.view.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.IpcamService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IpcamStreamActivity extends AppCompatActivity {

    private static final Logger log = LoggerFactory.getLogger(IpcamStreamActivity.class);

    @Inject
    IpcamService ipcamService;

    @Bind(R.id.mjpegSurfaceViewIpcam)
    MjpegView mjpegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_stream);

        Injector.getApplicationComponent().inject(this);
        ButterKnife.bind(this);
    }

    private void loadIpCam() {
        ipcamService
            .findById(getIntent().getStringExtra(RealmModel.FIELD_UUID))
            .flatMap(ipcam -> Mjpeg.newInstance()
                .credential(ipcam.getUsername(), ipcam.getPassword())
                .open(ipcam.getUrl()))
            .subscribe(
                inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                },
                throwable -> {
                    log.error("mjpeg error", throwable);
                    Toast.makeText(this, R.string.ipcam_stream_error, Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIpCam();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mjpegView.stopPlayback();
    }

}
