package com.github.openwebnet.iabutil;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.github.openwebnet.R;

public class DonationDialogFragment extends DialogFragment {

    private static final String TAG_FRAGMENT = "donation_dialog";

    public DonationDialogFragment() {}

    public static void show(AppCompatActivity activity) {
        DialogFragment dialogFragment = new DonationDialogFragment();
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG_FRAGMENT);
        if (prev != null) {
            ft.remove(prev);
        }
        dialogFragment.show(ft, TAG_FRAGMENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LinearLayout layout = (LinearLayout) LayoutInflater
            .from(getContext()).inflate(R.layout.dialog_donation, null);

        ListView list = (ListView) layout.findViewById(R.id.listViewDialogDonation);
        list.setAdapter(new DonationAdapter(getContext(), IabUtil.getInstance().getDonationEntries()));

        return new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
            .setTitle(R.string.dialog_donation_title)
            .setView(layout)
            .setPositiveButton(R.string.button_close, (dialog, which) -> dialog.dismiss())
            .create();
    }

}
