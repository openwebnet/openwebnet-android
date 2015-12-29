package com.github.openwebnet.view.device;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.LightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static java.util.Objects.requireNonNull;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(DeviceListAdapter.class);

    @Inject
    LightService lightService;

    private List<RealmModel> mItems;

    public DeviceListAdapter(List<RealmModel> items) {
        Injector.getApplicationComponent().inject(this);

        requireNonNull(items, "items is null");
        this.mItems = items;
    }

    /**
     *
     */
    public static class DeviceViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEW_TYPE = 100;

        // TODO
        @Bind(R.id.textViewCardDeviceTitle)
        TextView textViewCardDevice;

        public DeviceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class LightViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEW_TYPE = 200;

        @Bind(R.id.textViewCardLightTitle)
        TextView textViewCardLightTitle;

        @Bind(R.id.imageButtonCardLightFavourite)
        ImageButton imageButtonCardLightFavourite;

        @Bind(R.id.imageButtonCardLightSend)
        ImageButton imageButtonCardLightSend;

        public LightViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.get(position) instanceof DeviceModel) {
            return DeviceViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof LightModel) {
            return LightViewHolder.VIEW_TYPE;
        }
        throw new IllegalStateException("invalid item position");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DeviceViewHolder.VIEW_TYPE:
                return new DeviceViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.device_card, parent, false));
            case LightViewHolder.VIEW_TYPE:
                return new LightViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.light_card, parent, false));
            default:
                throw new IllegalStateException("invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case DeviceViewHolder.VIEW_TYPE:
                initCardDevice((DeviceViewHolder) holder, (DeviceModel) mItems.get(position));
                break;
            case LightViewHolder.VIEW_TYPE:
                initCardLight((LightViewHolder) holder, (LightModel) mItems.get(position));
                break;
            default:
                throw new IllegalStateException("invalid item position");
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void initCardDevice(DeviceViewHolder holder, DeviceModel device) {
        //holder.textViewCardDevice.setText(device.getName());
    }

    private void initCardLight(LightViewHolder holder, LightModel light) {
        holder.textViewCardLightTitle.setText(light.getName());
        holder.imageButtonCardLightSend.setOnClickListener(v -> {
            log.debug("imageButtonCardDeviceSend {}", light.getName());
        });
    }

}
