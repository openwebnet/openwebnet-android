package com.github.openwebnet.view.device;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.niqdev.openwebnet.message.Automation;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.view.custom.TextViewCustom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.temporal.ChronoUnit;

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
    AutomationService automationService;

    @Inject
    PreferenceService preferenceService;

    // NO @Inject: need activity to show AppCompactDialog
    Context mContext;

    private final Integer mEnvironmentId;
    private List<DomoticModel> mItems;

    public DeviceListAdapter(Context context, Integer environmentId, List<DomoticModel> items) {
        Injector.getApplicationComponent().inject(this);

        requireNonNull(environmentId, "environmentId is null");
        requireNonNull(items, "items is null");
        this.mContext = context;
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

        /* card_device_debug */

        @Bind(R.id.linearLayoutCardDeviceDebug)
        LinearLayout linearLayoutCardDeviceDebug;

        @Bind(R.id.textViewCardDeviceValueDelay)
        TextView textViewCardDeviceValueDelay;

        @Bind(R.id.imageButtonCardDeviceCopy)
        ImageButton imageButtonCardDeviceCopy;

        @Bind(R.id.textViewCustomCardDeviceResponse)
        TextViewCustom textViewCustomCardDeviceResponse;

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

        @Bind(R.id.imageButtonCardOff)
        ImageButton imageButtonCardOff;

        @Bind(R.id.imageButtonCardOn)
        ImageButton imageButtonCardOn;

        public LightViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class AutomationViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEW_TYPE = 300;

        @BindColor(R.color.white)
        int colorStatusStop;
        @BindColor(R.color.lime)
        int colorStatusUp;
        @BindColor(R.color.lime)
        int colorStatusDown;

        @Bind(R.id.imageButtonCardFavourite)
        ImageButton imageButtonCardFavourite;

        @Bind(R.id.imageViewCardAlert)
        ImageView imageViewCardAlert;

        @Bind(R.id.cardViewAutomation)
        CardView cardViewAutomation;

        @Bind(R.id.textViewCardAutomationTitle)
        TextView textViewCardAutomationTitle;

        @Bind(R.id.imageViewCardAutomationMenu)
        ImageView imageViewCardAutomationMenu;

        @Bind(R.id.imageButtonCardUp)
        ImageButton imageButtonCardUp;

        @Bind(R.id.imageButtonCardDown)
        ImageView imageButtonCardDown;

        public AutomationViewHolder(View view) {
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

    /**
     *
     */
    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        public static final int VIEW_TYPE = -1;

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mItems.isEmpty()) {
            return EmptyViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof DeviceModel) {
            return DeviceViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof LightModel) {
            return LightViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof AutomationModel) {
            return AutomationViewHolder.VIEW_TYPE;
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
            case AutomationViewHolder.VIEW_TYPE:
                return new AutomationViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_automation, parent, false));
            case EmptyViewHolder.VIEW_TYPE:
                return new EmptyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_empty, parent, false));
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
            case AutomationViewHolder.VIEW_TYPE:
                initCardAutomation((AutomationViewHolder) holder, (AutomationModel) mItems.get(position));
                break;
            case EmptyViewHolder.VIEW_TYPE:
                break;
            default:
                throw new IllegalStateException("invalid item position");
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size() > 0 ? mItems.size() : 1;
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

        holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusWait);
        if (device.isRunOnLoad()) {
            showDeviceStatus(holder, device);
        }

        holder.imageButtonCardSend.setOnClickListener(v -> {
            if (device.isShowConfirmation()) {
                showConfirmationDialog(holder, device);
            } else {
                sendDeviceRequest(holder, device);
            }
        });

        holder.imageViewCardDeviceMenu.setOnClickListener(v -> showCardMenu(v,
            () -> {
                Intent intentEditDevice = new Intent(mContext, DeviceActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(RealmModel.FIELD_UUID, device.getUuid());
                mContext.startActivity(intentEditDevice);
            },
            () -> deviceService.delete(device.getUuid())
                    .doOnCompleted(() -> updateDeviceListEvent())
                    .subscribe()));

        handleDeviceDebug(holder, device);
    }

    private void sendDeviceRequest(DeviceViewHolder holder, DeviceModel device) {
        deviceService.sendRequest(device).subscribe(deviceModel -> {
            showDeviceStatus(holder, deviceModel);
            handleDeviceDebug(holder, deviceModel);
        });
    }

    private void showDeviceStatus(DeviceViewHolder holder, DeviceModel device) {
        if (device.getStatus() == null) {
            holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusWait);
            holder.imageButtonCardSend.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }

        holder.imageButtonCardSend.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);
        switch (device.getStatus()) {
            case SUCCESS: holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusSuccess); break;
            case FAIL: holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusFail); break;
        }
    }

    private void showConfirmationDialog(DeviceViewHolder holder, DeviceModel device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
            .setTitle(R.string.dialog_device_confirmation_title)
            .setMessage(R.string.dialog_device_confirmation_message)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(v -> {
                sendDeviceRequest(holder, device);
                dialog.dismiss();
            });
    }

    private void handleDeviceDebug(DeviceViewHolder holder, DeviceModel device) {
        holder.linearLayoutCardDeviceDebug.setVisibility(View.GONE);
        if (preferenceService.isDeviceDebugEnabled()) {
            holder.textViewCardDeviceValueDelay.setText("-");
            holder.textViewCustomCardDeviceResponse.setText(mContext.getString(R.string.card_device_debug_none));
            holder.linearLayoutCardDeviceDebug.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(device.getResponseDebug())) {
                long duration = ChronoUnit.MILLIS.between(device.getInstantRequestDebug(), device.getInstantResponseDebug());
                holder.textViewCardDeviceValueDelay.setText(String.valueOf(duration));
                holder.textViewCustomCardDeviceResponse.setText(device.getResponseDebug());

                handleDeviceDebugClipboard(holder, device);
            }
        }
    }

    private void handleDeviceDebugClipboard(DeviceViewHolder holder, DeviceModel device) {
        holder.imageButtonCardDeviceCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            String labelClipboard = mContext.getString(R.string.device_debug_label);
            clipboard.setPrimaryClip(ClipData.newPlainText(labelClipboard, device.getResponseDebug()));

            Toast.makeText(mContext, mContext.getString(R.string.card_device_debug_clipboard_copied),
                Toast.LENGTH_SHORT).show();
        });
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
        holder.imageButtonCardOff.setOnClickListener(v -> turnLightOff(holder, light));
        holder.imageButtonCardOn.setOnClickListener(v -> turnLightOn(holder, light));

        holder.imageViewCardLightMenu.setOnClickListener(v -> showCardMenu(v,
            () -> {
                Intent intentEditLight = new Intent(mContext, LightActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(RealmModel.FIELD_UUID, light.getUuid());
                mContext.startActivity(intentEditLight);
            },
            () -> lightService.delete(light.getUuid())
                    .doOnCompleted(() -> updateDeviceListEvent())
                    .subscribe()));
    }

    private void updateLightStatus(LightViewHolder holder, LightModel.Status status) {
        holder.imageButtonCardSend.setVisibility(View.VISIBLE);
        holder.imageButtonCardOff.setVisibility(View.VISIBLE);
        holder.imageButtonCardOn.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);
        if (status == null) {
            log.warn("light status is null: unable to update");
            holder.imageButtonCardSend.setVisibility(View.INVISIBLE);
            holder.imageButtonCardOff.setVisibility(View.INVISIBLE);
            holder.imageButtonCardOn.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }
        switch (status) {
            case ON: holder.cardViewLight.setCardBackgroundColor(holder.colorStatusOn); break;
            case OFF: holder.cardViewLight.setCardBackgroundColor(holder.colorStatusOff); break;
        }
    }

    private void turnLightOff(LightViewHolder holder, LightModel light) {
        log.debug("turn off light {}", light.getUuid());
        if (light.getStatus() == null) {
            log.warn("light status is null: unable to turn off");
            return;
        }

        Action0 updateLightStatusAction = () -> updateLightStatus(holder, light.getStatus());
        lightService.turnOff(light).doOnCompleted(updateLightStatusAction).subscribe();
    }

    private void turnLightOn(LightViewHolder holder, LightModel light) {
        log.debug("turn on light {}", light.getUuid());
        if (light.getStatus() == null) {
            log.warn("light status is null: unable to turn on");
            return;
        }

        Action0 updateLightStatusAction = () -> updateLightStatus(holder, light.getStatus());
        lightService.turnOn(light).doOnCompleted(updateLightStatusAction).subscribe();
    }

    private void toggleLight(LightViewHolder holder, LightModel light) {
        log.debug("toggle light {}", light.getUuid());
        if (light.getStatus() == null) {
            log.warn("light status is null: unable to toggle");
            return;
        }

        switch (light.getStatus()) {
            case ON: turnLightOff(holder, light); break;
            case OFF: turnLightOn(holder, light); break;
        }
    }

    /* Automation */

    private void initCardAutomation(AutomationViewHolder holder, AutomationModel automation) {
        holder.textViewCardAutomationTitle.setText(automation.getName());

        // TODO abstract
        updateFavourite(holder, automation.isFavourite());
        holder.imageButtonCardFavourite.setOnClickListener(v -> {
            automation.setFavourite(!automation.isFavourite());
            automationService.update(automation)
                    .doOnCompleted(() -> updateFavourite(holder, automation.isFavourite()))
                    .subscribe();
        });

        updateAutomationStatus(holder, automation.getStatus());

        holder.imageButtonCardUp.setOnClickListener(v -> sendAutomationRequestUp(holder, automation));
        holder.imageButtonCardDown.setOnClickListener(v -> sendAutomationRequestDown(holder, automation));

        holder.imageViewCardAutomationMenu.setOnClickListener(v -> showCardMenu(v,
                () -> {
                    Intent intentEditAutomation = new Intent(mContext, AutomationActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(RealmModel.FIELD_UUID, automation.getUuid());
                    mContext.startActivity(intentEditAutomation);
                },
                () -> automationService.delete(automation.getUuid())
                        .doOnCompleted(this::updateDeviceListEvent)
                        .subscribe()));
    }

    private void updateAutomationStatus(AutomationViewHolder holder, AutomationModel.Status status) {
        holder.imageButtonCardUp.setVisibility(View.VISIBLE);
        holder.imageButtonCardDown.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);

        if (status == null) {
            log.warn("automation status is null: unable to update");
            holder.imageButtonCardUp.setVisibility(View.INVISIBLE);
            holder.imageButtonCardDown.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }
        switch (status) {
            case STOP:
                holder.cardViewAutomation.setCardBackgroundColor(holder.colorStatusStop);
                holder.imageButtonCardUp.setImageResource(R.drawable.arrow_up_bold_circle_outline);
                holder.imageButtonCardDown.setImageResource(R.drawable.arrow_down_bold_circle_outline);
                break;
            case UP:
                holder.cardViewAutomation.setCardBackgroundColor(holder.colorStatusUp);
                holder.imageButtonCardUp.setImageResource(R.drawable.arrow_up_bold_circle);
                holder.imageButtonCardDown.setImageResource(R.drawable.arrow_down_bold_circle_outline);
                break;
            case DOWN:
                holder.cardViewAutomation.setCardBackgroundColor(holder.colorStatusDown);
                holder.imageButtonCardUp.setImageResource(R.drawable.arrow_up_bold_circle_outline);
                holder.imageButtonCardDown.setImageResource(R.drawable.arrow_down_bold_circle);
                break;
        }
    }

    private void sendAutomationRequestUp(AutomationViewHolder holder, AutomationModel automation) {
        log.debug("move automation up {}", automation.getUuid());
        if (automation.getStatus() == null) {
            log.warn("automation status is null: unable to move up");
            return;
        }
        Action0 updateAutomationStatusAction = () -> updateAutomationStatus(holder, automation.getStatus());

        switch (automation.getStatus()) {
            case STOP:
                automationService.moveUp(automation).doOnCompleted(updateAutomationStatusAction).subscribe();
                break;
            case UP:
            case DOWN:
                automationService.stop(automation).doOnCompleted(updateAutomationStatusAction).subscribe();
                break;
        }
    }

    private void sendAutomationRequestDown(AutomationViewHolder holder, AutomationModel automation) {
        log.debug("move automation down {}", automation.getUuid());
        if (automation.getStatus() == null) {
            log.warn("automation status is null: unable to move down");
            return;
        }
        Action0 updateAutomationStatusAction = () -> updateAutomationStatus(holder, automation.getStatus());

        switch (automation.getStatus()) {
            case STOP:
                automationService.moveDown(automation).doOnCompleted(updateAutomationStatusAction).subscribe();
                break;
            case UP:
            case DOWN:
                automationService.stop(automation).doOnCompleted(updateAutomationStatusAction).subscribe();
                break;
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

    private void updateFavourite(AutomationViewHolder holder, boolean favourite) {
        int favouriteDrawable = favourite ? R.drawable.star : R.drawable.star_outline;
        holder.imageButtonCardFavourite.setImageResource(favouriteDrawable);
    }

    private void showCardMenu(View view, Action0 onMenuEdit, Action0 onMenuDelete) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_card, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            log.debug("CARD MENU selected [id={}]", id);
            switch (id) {
                case R.id.action_card_edit: onMenuEdit.call(); break;
                case R.id.action_card_delete: onMenuDelete.call(); break;
            }
            return true;
        });
    }

}
