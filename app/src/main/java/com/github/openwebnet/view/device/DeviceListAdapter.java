package com.github.openwebnet.view.device;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.openwebnet.R;
import com.github.openwebnet.model.DeviceModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<DeviceModel> mDevices;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // TODO
        @Bind(R.id.textViewCardDeviceTitle)
        TextView textViewCardDevice;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public DeviceListAdapter(List<DeviceModel> devices) {
        this.mDevices = devices;
    }

    @Override
    public DeviceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.device_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DeviceListAdapter.ViewHolder holder, int position) {
        holder.textViewCardDevice.setText(mDevices.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

}
