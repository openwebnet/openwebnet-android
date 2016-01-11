package com.github.openwebnet.view.device;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.LightService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.functions.Action0;

import static java.util.Objects.requireNonNull;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(DeviceListAdapter.class);

    @Inject
    DeviceService deviceService;

    @Inject
    LightService lightService;

    @Inject
    Context mContext;

    private final Integer mEnvironmentId;
    private List<DomoticModel> mItems;

    public DeviceListAdapter(Integer environmentId, List<DomoticModel> items) {
        Injector.getApplicationComponent().inject(this);

        requireNonNull(environmentId, "environmentId is null");
        requireNonNull(items, "items is null");
        this.mEnvironmentId = environmentId;
        this.mItems = items;
    }

    /**
     *
     */
    public static class DeviceViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 100;

        @BindDrawable(R.drawable.triangle_wait)
        Drawable drawableStatusWait;

        @BindDrawable(R.drawable.triangle_success)
        Drawable drawableStatusSuccess;

        @BindDrawable(R.drawable.triangle_fail)
        Drawable drawableStatusFail;

        @Bind(R.id.relativeLayoutCardDeviceStatus)
        RelativeLayout relativeLayoutCardDeviceStatus;

        @Bind(R.id.textViewCardDeviceTitle)
        TextView textViewCardDevice;

        @Bind(R.id.imageViewCardDeviceMenu)
        ImageView imageViewCardDeviceMenu;

        public DeviceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class LightViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 200;

        @BindColor(R.color.yellow)
        int colorStatusOn;
        @BindColor(R.color.white)
        int colorStatusOff;

        @Bind(R.id.cardViewLight)
        CardView cardViewLight;

        @Bind(R.id.textViewCardLightTitle)
        TextView textViewCardLightTitle;

        @Bind(R.id.imageViewCardLightMenu)
        ImageView imageViewCardLightMenu;

        public LightViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class CommonViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.imageButtonCardFavourite)
        ImageButton imageButtonCardFavourite;

        @Bind(R.id.imageButtonCardSend)
        ImageButton imageButtonCardSend;

        @Bind(R.id.imageViewCardAlert)
        ImageView imageViewCardAlert;

        public CommonViewHolder(View view) {
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
                    .inflate(R.layout.card_device, parent, false));
            case LightViewHolder.VIEW_TYPE:
                return new LightViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_light, parent, false));
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

    /* Device */

    private void initCardDevice(DeviceViewHolder holder, DeviceModel device) {
        holder.textViewCardDevice.setText(device.getName());

        // TODO abstract
        updateFavourite(holder, device.isFavourite());
        holder.imageButtonCardFavourite.setOnClickListener(v -> {
            device.setFavourite(!device.isFavourite());
            deviceService.update(device)
                .doOnCompleted(() -> updateFavourite(holder, device.isFavourite()))
                .subscribe();
        });

        updateDeviceStatus(holder, device);
        holder.imageButtonCardSend.setOnClickListener(v -> updateDeviceStatus(holder, device));
    }

    private void updateDeviceStatus(DeviceViewHolder holder, DeviceModel device) {
        /* TODO
        if (status == null) {
            log.warn("light status is null: unable to update");
            holder.imageButtonCardSend.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }
        */

        holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusWait);
        if (device.isRunOnLoad()) {
            deviceService.sendRequest(device).subscribe(deviceModel -> {
                switch (deviceModel.getStatus()) {
                    case SUCCESS: holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusSuccess); break;
                    case FAIL: holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusFail); break;
                }
            });
        }
    }

    /* Light */

    private void initCardLight(LightViewHolder holder, LightModel light) {
        holder.textViewCardLightTitle.setText(light.getName());

        // TODO abstract
        updateFavourite(holder, light.isFavourite());
        holder.imageButtonCardFavourite.setOnClickListener(v -> {
            light.setFavourite(!light.isFavourite());
            lightService.update(light)
                .doOnCompleted(() -> updateFavourite(holder, light.isFavourite()))
                .subscribe();
        });

        updateLightStatus(holder, light.getStatus());
        holder.imageButtonCardSend.setOnClickListener(v -> toggleLight(holder, light));

        holder.imageViewCardLightMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_card, popupMenu.getMenu());
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                log.debug("CARD MENU selected [id={}]", id);

                switch (id) {
                    case R.id.action_card_edit:
                        Intent intentEditLight = new Intent(mContext, LightActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(RealmModel.FIELD_UUID, light.getUuid());
                        mContext.startActivity(intentEditLight);
                        break;
                    case R.id.action_card_delete:
                        lightService.delete(light.getUuid())
                            .doOnCompleted(() -> updateDeviceListEvent())
                            .subscribe();
                        break;
                }
                return true;
            });
        });
    }

    private void updateLightStatus(LightViewHolder holder, LightModel.Status status) {
        holder.imageButtonCardSend.setVisibility(View.VISIBLE);
        if (status == null) {
            log.warn("light status is null: unable to update");
            holder.imageButtonCardSend.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }
        switch (status) {
            case ON: holder.cardViewLight.setBackgroundColor(holder.colorStatusOn); break;
            case OFF: holder.cardViewLight.setBackgroundColor(holder.colorStatusOff); break;
        }
    }

    private void toggleLight(LightViewHolder holder, LightModel light) {
        log.debug("toggle light {}", light.getUuid());
        if (light.getStatus() == null) {
            log.warn("light status is null: unable to toggle");
            return;
        }
        Action0 updateLightStatusAction = () -> updateLightStatus(holder, light.getStatus());

        switch (light.getStatus()) {
            case ON: lightService.turnOff(light).doOnCompleted(updateLightStatusAction).subscribe(); break;
            case OFF: lightService.turnOn(light).doOnCompleted(updateLightStatusAction).subscribe(); break;
        }
    }

    /* commons */

    private void updateDeviceListEvent() {
        EventBus.getDefault().post(new DeviceListFragment.UpdateDeviceListEvent(mEnvironmentId));
    }

    private void updateFavourite(CommonViewHolder holder, boolean favourite) {
        int favouriteDrawable = favourite ? R.drawable.star : R.drawable.star_outline;
        holder.imageButtonCardFavourite.setImageResource(favouriteDrawable);
    }

}
