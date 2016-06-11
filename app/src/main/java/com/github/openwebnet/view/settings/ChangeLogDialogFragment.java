package com.github.openwebnet.view.settings;

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

import com.github.openwebnet.R;

import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView;

public class ChangeLogDialogFragment extends DialogFragment {

    private static final String TAG_FRAGMENT = "changelog_dialog";

    public ChangeLogDialogFragment() {}

    public static void show(AppCompatActivity activity) {
        DialogFragment dialogFragment = new ChangeLogDialogFragment();
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

        ChangeLogRecyclerView chgList = (ChangeLogRecyclerView) LayoutInflater
            .from(getContext()).inflate(R.layout.changelog_fragment_dialog, null);

        return new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle)
            .setTitle(R.string.changelog_title)
            .setView(chgList)
            .setPositiveButton(R.string.button_close, (dialog, which) -> dialog.dismiss())
            .create();
    }
}
