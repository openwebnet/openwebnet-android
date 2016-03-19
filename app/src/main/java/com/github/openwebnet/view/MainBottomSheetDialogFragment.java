package com.github.openwebnet.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.github.openwebnet.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private static final Logger log = LoggerFactory.getLogger(MainBottomSheetDialogFragment.class);

    @Bind(R.id.gridViewBottomSheet)
    GridView gridView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_main, container, false);
        ButterKnife.bind(this, view);

        BottomSheetDialogAdapter mAdapter = new BottomSheetDialogAdapter(this.getContext());
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            log.debug("position={} id={}", position, id);
        });

        return view;
    }
}
