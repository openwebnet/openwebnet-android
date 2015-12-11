package com.github.openwebnet.view.device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.openwebnet.OpenWebNetApplication;
import com.github.openwebnet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        OpenWebNetApplication.component(this).inject(this);
        ButterKnife.bind(this);

        /*
        actionMessage: *CHI*COSA*DOVE##
        statusMessage: *#CHI*DOVE##

        Messaggi di Comando / Stato
                *CHI*COSA*DOVE##
        Messaggi di Richiesta Stato
        *#CHI*DOVE##
        Messaggi di Richiesta/Lettura/Scrittura Grandezza
        Richiesta:
        *#CHI*DOVE*GRANDEZZA##
        Lettura:
        *#CHI*DOVE*GRANDEZZA*VALORE1*...*VALOREn##
        Scrittura:
        *#CHI*DOVE*#GRANDEZZA*VALORE1*...*VALOREn##
        Messaggi di Acknowledge
        ACK:
        *#*1##
        NACK:
        *#*0##
        * */
    }
}
