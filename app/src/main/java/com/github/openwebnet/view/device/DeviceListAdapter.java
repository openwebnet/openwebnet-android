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

import com.github.niqdev.openwebnet.message.Heating;
import com.github.openwebnet.R;
import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.DomoticModel;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.RealmModel;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.model.TemperatureModel;
import com.github.openwebnet.service.AutomationService;
import com.github.openwebnet.service.CommonService;
import com.github.openwebnet.service.DeviceService;
import com.github.openwebnet.service.DomoticService;
import com.github.openwebnet.service.EnergyService;
import com.github.openwebnet.service.IpcamService;
import com.github.openwebnet.service.LightService;
import com.github.openwebnet.service.PreferenceService;
import com.github.openwebnet.service.ScenarioService;
import com.github.openwebnet.service.SoundService;
import com.github.openwebnet.service.TemperatureService;
import com.github.openwebnet.service.UtilityService;
import com.github.openwebnet.view.custom.TextViewCustom;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threeten.bp.temporal.ChronoUnit;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action0;
import rx.functions.Action2;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final Logger log = LoggerFactory.getLogger(DeviceListAdapter.class);

    @Inject
    DeviceService deviceService;

    @Inject
    LightService lightService;

    @Inject
    AutomationService automationService;

    @Inject
    IpcamService ipcamService;

    @Inject
    TemperatureService temperatureService;

    @Inject
    ScenarioService scenarioService;

    @Inject
    EnergyService energyService;

    @Inject
    SoundService soundService;

    @Inject
    PreferenceService preferenceService;

    @Inject
    CommonService commonService;

    @Inject
    UtilityService utilityService;

    // NO @Inject: need activity to show AppCompactDialog
    Context mContext;

    private final Integer mEnvironmentId;
    private List<DomoticModel> mItems;

    public DeviceListAdapter(Context context, Integer environmentId, List<DomoticModel> items) {
        Injector.getApplicationComponent().inject(this);

        checkNotNull(environmentId, "environmentId is null");
        checkNotNull(items, "items is null");
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

        @BindView(R.id.relativeLayoutCardDeviceStatus)
        RelativeLayout relativeLayoutCardDeviceStatus;

        @BindView(R.id.textViewCardDeviceTitle)
        TextView textViewCardDevice;

        @BindView(R.id.imageButtonCardDeviceSend)
        ImageButton imageButtonCardDeviceSend;

        /* card_device_debug */

        @BindView(R.id.linearLayoutCardDeviceDebug)
        LinearLayout linearLayoutCardDeviceDebug;

        @BindView(R.id.textViewCardDeviceValueDelay)
        TextView textViewCardDeviceValueDelay;

        @BindView(R.id.imageButtonCardDeviceCopy)
        ImageButton imageButtonCardDeviceCopy;

        @BindView(R.id.textViewCustomCardDeviceResponse)
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

        @BindView(R.id.cardViewLight)
        CardView cardViewLight;

        @BindView(R.id.textViewCardLightTitle)
        TextView textViewCardLightTitle;

        @BindView(R.id.imageButtonCardLightOff)
        ImageButton imageButtonCardLightOff;

        @BindView(R.id.imageButtonCardLightOn)
        ImageButton imageButtonCardLightOn;

        public LightViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class AutomationViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 300;

        @BindColor(R.color.white)
        int colorStatusStop;
        @BindColor(R.color.lime)
        int colorStatusUp;
        @BindColor(R.color.lime)
        int colorStatusDown;

        @BindView(R.id.cardViewAutomation)
        CardView cardViewAutomation;

        @BindView(R.id.textViewCardAutomationTitle)
        TextView textViewCardAutomationTitle;

        @BindView(R.id.imageButtonCardAutomationUp)
        ImageButton imageButtonCardAutomationUp;

        @BindView(R.id.imageButtonCardAutomationDown)
        ImageButton imageButtonCardAutomationDown;

        public AutomationViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class IpcamViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 400;

        @BindView(R.id.cardViewIpcam)
        CardView cardViewIpcam;

        @BindView(R.id.textViewCardIpcamTitle)
        TextView textViewCardIpcamTitle;

        @BindView(R.id.imageButtonCardIpcamPlay)
        ImageButton imageButtonCardIpcamPlay;

        public IpcamViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class TemperatureViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 500;

        @BindView(R.id.cardViewTemperature)
        CardView cardViewTemperature;

        @BindView(R.id.textViewCardTemperatureTitle)
        TextView textViewCardTemperatureTitle;

        @BindView(R.id.textViewCardTemperatureValue)
        TextView textViewCardTemperatureValue;

        @BindView(R.id.imageViewCardTemperatureScale)
        ImageView imageViewCardTemperatureScale;

        @BindView(R.id.linearLayoutCardTemperatureValue)
        LinearLayout linearLayoutCardTemperatureValue;

        public TemperatureViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class ScenarioViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 600;

        @BindColor(R.color.lime)
        int colorStatusStart;
        @BindColor(R.color.white)
        int colorStatusStop;

        @BindView(R.id.cardViewScenario)
        CardView cardViewScenario;

        @BindView(R.id.textViewCardScenarioTitle)
        TextView textViewCardScenarioTitle;

        @BindView(R.id.imageButtonCardScenarioStart)
        ImageButton imageButtonCardScenarioStart;

        @BindView(R.id.imageButtonCardScenarioStop)
        ImageButton imageButtonCardScenarioStop;

        @BindView(R.id.imageButtonCardScenarioDisabled)
        ImageButton imageButtonCardScenarioDisabled;

        public ScenarioViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class EnergyViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 700;

        @BindView(R.id.cardViewEnergy)
        CardView cardViewEnergy;

        @BindView(R.id.textViewCardEnergyTitle)
        TextView textViewCardEnergyTitle;

        @BindView(R.id.textViewEnergyInstantaneousPower)
        TextView textViewEnergyInstantaneousPower;

        @BindView(R.id.textViewEnergyDailyPower)
        TextView textViewEnergyDailyPower;

        @BindView(R.id.textViewEnergyMonthlyPower)
        TextView textViewEnergyMonthlyPower;

        @BindView(R.id.linearLayoutEnergyValues)
        LinearLayout linearLayoutEnergyValues;

        public EnergyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class SoundViewHolder extends CommonViewHolder {

        public static final int VIEW_TYPE = 800;

        @BindColor(R.color.amber)
        int colorStatusOn;
        @BindColor(R.color.white)
        int colorStatusOff;

        @BindView(R.id.cardViewSound)
        CardView cardViewSound;

        @BindView(R.id.textViewCardSoundTitle)
        TextView textViewCardSoundTitle;

        @BindView(R.id.imageButtonCardSoundOff)
        ImageButton imageButtonCardSoundOff;

        @BindView(R.id.imageButtonCardSoundOn)
        ImageButton imageButtonCardSoundOn;

        public SoundViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public static class CommonViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageButtonCardFavourite)
        ImageButton imageButtonCardFavourite;

        @BindView(R.id.imageButtonCardMenu)
        ImageButton imageButtonCardMenu;

        @BindView(R.id.imageViewCardAlert)
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
        if (mItems.get(position) instanceof IpcamModel) {
            return IpcamViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof TemperatureModel) {
            return TemperatureViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof ScenarioModel) {
            return ScenarioViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof EnergyModel) {
            return EnergyViewHolder.VIEW_TYPE;
        }
        if (mItems.get(position) instanceof SoundModel) {
            return SoundViewHolder.VIEW_TYPE;
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
            case IpcamViewHolder.VIEW_TYPE:
                return new IpcamViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_ipcam, parent, false));
            case TemperatureViewHolder.VIEW_TYPE:
                return new TemperatureViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_temperature, parent, false));
            case ScenarioViewHolder.VIEW_TYPE:
                return new ScenarioViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_scenario, parent, false));
            case EnergyViewHolder.VIEW_TYPE:
                return new EnergyViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_energy, parent, false));
            case SoundViewHolder.VIEW_TYPE:
                return new SoundViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_sound, parent, false));
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
            case IpcamViewHolder.VIEW_TYPE:
                initCardIpcam((IpcamViewHolder) holder, (IpcamModel) mItems.get(position));
                break;
            case TemperatureViewHolder.VIEW_TYPE:
                initCardTemperature((TemperatureViewHolder) holder, (TemperatureModel) mItems.get(position));
                break;
            case ScenarioViewHolder.VIEW_TYPE:
                initCardScenario((ScenarioViewHolder) holder, (ScenarioModel) mItems.get(position));
                break;
            case EnergyViewHolder.VIEW_TYPE:
                initCardEnergy((EnergyViewHolder) holder, (EnergyModel) mItems.get(position));
                break;
            case SoundViewHolder.VIEW_TYPE:
                initCardSound((SoundViewHolder) holder, (SoundModel) mItems.get(position));
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

        updateFavourite(holder, device.isFavourite());
        onFavouriteChange(holder, device, deviceService);

        holder.relativeLayoutCardDeviceStatus.setBackground(holder.drawableStatusWait);
        if (device.isRunOnLoad()) {
            showDeviceStatus(holder, device);
        }

        holder.imageButtonCardDeviceSend.setOnClickListener(v -> {
            if (device.isShowConfirmation()) {
                showConfirmationDialog(holder, device);
            } else {
                sendDeviceRequest(holder, device);
            }
        });

        onMenuClick(holder, deviceService, device.getUuid(), DeviceActivity.class);
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
            holder.imageButtonCardDeviceSend.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }

        holder.imageButtonCardDeviceSend.setVisibility(View.VISIBLE);
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

        updateFavourite(holder, light.isFavourite());
        onFavouriteChange(holder, light, lightService);
        updateLightStatus(holder, light.getStatus());

        holder.imageButtonCardLightOff.setOnClickListener(v -> turnLightOff(holder, light));
        holder.imageButtonCardLightOn.setOnClickListener(v -> turnLightOn(holder, light));

        onMenuClick(holder, lightService, light.getUuid(), LightActivity.class);
    }

    private void updateLightStatus(LightViewHolder holder, LightModel.Status status) {
        holder.imageButtonCardLightOff.setVisibility(View.VISIBLE);
        holder.imageButtonCardLightOn.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);
        if (status == null) {
            log.warn("light status is null: unable to update");
            holder.imageButtonCardLightOff.setVisibility(View.INVISIBLE);
            holder.imageButtonCardLightOn.setVisibility(View.INVISIBLE);
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

    /* Automation */

    private void initCardAutomation(AutomationViewHolder holder, AutomationModel automation) {
        holder.textViewCardAutomationTitle.setText(automation.getName());

        updateFavourite(holder, automation.isFavourite());
        onFavouriteChange(holder, automation, automationService);
        updateAutomationStatus(holder, automation.getStatus());

        holder.imageButtonCardAutomationUp.setOnClickListener(v -> sendAutomationRequestUp(holder, automation));
        holder.imageButtonCardAutomationDown.setOnClickListener(v -> sendAutomationRequestDown(holder, automation));

        onMenuClick(holder, automationService, automation.getUuid(), AutomationActivity.class);
    }

    private void updateAutomationStatus(AutomationViewHolder holder, AutomationModel.Status status) {
        holder.imageButtonCardAutomationUp.setVisibility(View.VISIBLE);
        holder.imageButtonCardAutomationDown.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);

        if (status == null) {
            log.warn("automation status is null: unable to update");
            holder.imageButtonCardAutomationUp.setVisibility(View.INVISIBLE);
            holder.imageButtonCardAutomationDown.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }
        switch (status) {
            case STOP:
                holder.cardViewAutomation.setCardBackgroundColor(holder.colorStatusStop);
                holder.imageButtonCardAutomationUp.setImageResource(R.drawable.arrow_up_bold_circle_outline);
                holder.imageButtonCardAutomationDown.setImageResource(R.drawable.arrow_down_bold_circle_outline);
                break;
            case UP:
                holder.cardViewAutomation.setCardBackgroundColor(holder.colorStatusUp);
                holder.imageButtonCardAutomationUp.setImageResource(R.drawable.arrow_up_bold_circle);
                holder.imageButtonCardAutomationDown.setImageResource(R.drawable.arrow_down_bold_circle_outline);
                break;
            case DOWN:
                holder.cardViewAutomation.setCardBackgroundColor(holder.colorStatusDown);
                holder.imageButtonCardAutomationUp.setImageResource(R.drawable.arrow_up_bold_circle_outline);
                holder.imageButtonCardAutomationDown.setImageResource(R.drawable.arrow_down_bold_circle);
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

    /* Ipcam */

    private void initCardIpcam(IpcamViewHolder holder, IpcamModel ipcam) {
        holder.textViewCardIpcamTitle.setText(ipcam.getName());

        updateFavourite(holder, ipcam.isFavourite());
        onFavouriteChange(holder, ipcam, ipcamService);
        onMenuClick(holder, ipcamService, ipcam.getUuid(), IpcamActivity.class);

        holder.imageButtonCardIpcamPlay.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);
        if (!utilityService.hasNetworkAccess()) {
            log.warn("ipcam has not network access");
            holder.imageButtonCardIpcamPlay.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }

        holder.imageButtonCardIpcamPlay.setOnClickListener(v -> {
            Intent intentIpcamStream = new Intent(mContext, IpcamStreamActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(RealmModel.FIELD_UUID, ipcam.getUuid());
            mContext.startActivity(intentIpcamStream);
        });
    }

    /* Temperature */

    private void initCardTemperature(TemperatureViewHolder holder, TemperatureModel temperature) {
        holder.textViewCardTemperatureTitle.setText(temperature.getName());

        updateFavourite(holder, temperature.isFavourite());
        onFavouriteChange(holder, temperature, temperatureService);
        onMenuClick(holder, temperatureService, temperature.getUuid(), TemperatureActivity.class);
        updateTemperatureValue(holder, temperature);
    }

    private void updateTemperatureValue(TemperatureViewHolder holder, TemperatureModel temperature) {
        holder.linearLayoutCardTemperatureValue.setVisibility(View.GONE);
        holder.imageViewCardTemperatureScale.setVisibility(View.INVISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);

        if (temperature.getValue() == null) {
            log.warn("temperature value is null or invalid: unable to update");
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }

        holder.linearLayoutCardTemperatureValue.setVisibility(View.VISIBLE);
        holder.textViewCardTemperatureValue.setText(temperature.getValue());

        Func1<Heating.TemperatureScale, Drawable> getImageTemperature = temperatureScale -> {
            switch (preferenceService.getDefaultTemperatureScale()) {
                case FAHRENHEIT: return mContext.getResources().getDrawable(R.drawable.temperature_fahrenheit);
                case KELVIN: return mContext.getResources().getDrawable(R.drawable.temperature_kelvin);
                default: return mContext.getResources().getDrawable(R.drawable.temperature_celsius);
            }
        };
        holder.imageViewCardTemperatureScale.setVisibility(View.VISIBLE);
        holder.imageViewCardTemperatureScale
            .setImageDrawable(getImageTemperature.call(preferenceService.getDefaultTemperatureScale()));
    }

    /* Scenario */

    private void initCardScenario(ScenarioViewHolder holder, ScenarioModel scenario) {
        holder.textViewCardScenarioTitle.setText(scenario.getName());

        updateFavourite(holder, scenario.isFavourite());
        onFavouriteChange(holder, scenario, scenarioService);
        updateScenarioStatus(holder, scenario.getStatus(), scenario.isEnable());

        holder.imageButtonCardScenarioStart.setOnClickListener(v -> sendScenarioRequestStart(holder, scenario));
        holder.imageButtonCardScenarioStop.setOnClickListener(v -> sendScenarioRequestStop(holder, scenario));

        onMenuClick(holder, scenarioService, scenario.getUuid(), ScenarioActivity.class);
    }

    private void updateScenarioStatus(ScenarioViewHolder holder, ScenarioModel.Status status, boolean enabled) {
        holder.imageButtonCardScenarioStart.setVisibility(View.VISIBLE);
        holder.imageButtonCardScenarioStop.setVisibility(View.VISIBLE);
        holder.imageButtonCardScenarioDisabled.setVisibility(View.INVISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);
        if (status == null) {
            log.warn("scenario status is null: unable to update");
            holder.imageButtonCardScenarioStart.setVisibility(View.INVISIBLE);
            holder.imageButtonCardScenarioStop.setVisibility(View.INVISIBLE);
            holder.imageButtonCardScenarioDisabled.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }
        if (!enabled) {
            log.warn("scenario status is disabled");
            holder.imageButtonCardScenarioStart.setVisibility(View.INVISIBLE);
            holder.imageButtonCardScenarioStop.setVisibility(View.INVISIBLE);
            holder.imageButtonCardScenarioDisabled.setVisibility(View.VISIBLE);
            holder.imageViewCardAlert.setVisibility(View.INVISIBLE);
            return;
        }
        switch (status) {
            case START: holder.cardViewScenario.setCardBackgroundColor(holder.colorStatusStart); break;
            case STOP: holder.cardViewScenario.setCardBackgroundColor(holder.colorStatusStop); break;
        }
    }

    private void sendScenarioRequestStart(ScenarioViewHolder holder, ScenarioModel scenario) {
        log.debug("start scenario {}", scenario.getUuid());
        if (scenario.getStatus() == null) {
            log.warn("scenario status is null: unable to start");
            return;
        }

        Action0 updateScenarioStatusAction = () -> updateScenarioStatus(holder, scenario.getStatus(), scenario.isEnable());
        scenarioService.start(scenario).doOnCompleted(updateScenarioStatusAction).subscribe();
    }

    private void sendScenarioRequestStop(ScenarioViewHolder holder, ScenarioModel scenario) {
        log.debug("stop scenario {}", scenario.getUuid());
        if (scenario.getStatus() == null) {
            log.warn("scenario status is null: unable to stop");
            return;
        }

        Action0 updateScenarioStatusAction = () -> updateScenarioStatus(holder, scenario.getStatus(), scenario.isEnable());
        scenarioService.stop(scenario).doOnCompleted(updateScenarioStatusAction).subscribe();
    }

    /* Energy */

    private void initCardEnergy(EnergyViewHolder holder, EnergyModel energy) {
        holder.textViewCardEnergyTitle.setText(energy.getName());

        updateFavourite(holder, energy.isFavourite());
        onFavouriteChange(holder, energy, energyService);
        onMenuClick(holder, energyService, energy.getUuid(), EnergyActivity.class);
        updateEnergyValues(holder, energy);
    }

    private void updateEnergyValues(EnergyViewHolder holder, EnergyModel energy) {
        holder.linearLayoutEnergyValues.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);

        if (energy.getInstantaneousPower() == null &&
                energy.getDailyPower() == null &&
                energy.getMonthlyPower() == null) {
            log.warn("energy values are null or invalid: unable to update");
            holder.linearLayoutEnergyValues.setVisibility(View.GONE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }

        Action2<TextView, String> updateEnergy = (textView, value) ->
            textView.setText(TextUtils.isEmpty(value) ? utilityService.getString(R.string.energy_none) :
                value + " " + utilityService.getString(R.string.energy_power_unit));

        updateEnergy.call(holder.textViewEnergyInstantaneousPower, energy.getInstantaneousPower());
        updateEnergy.call(holder.textViewEnergyDailyPower, energy.getDailyPower());
        updateEnergy.call(holder.textViewEnergyMonthlyPower, energy.getMonthlyPower());
    }

    /* Sound */

    private void initCardSound(SoundViewHolder holder, SoundModel sound) {
        holder.textViewCardSoundTitle.setText(sound.getName());

        updateFavourite(holder, sound.isFavourite());
        onFavouriteChange(holder, sound, soundService);
        updateSoundStatus(holder, sound.getStatus());

        holder.imageButtonCardSoundOff.setOnClickListener(v -> turnSoundOff(holder, sound));
        holder.imageButtonCardSoundOn.setOnClickListener(v -> turnSoundOn(holder, sound));

        onMenuClick(holder, soundService, sound.getUuid(), SoundActivity.class);
    }

    private void updateSoundStatus(SoundViewHolder holder, SoundModel.Status status) {
        holder.imageButtonCardSoundOff.setVisibility(View.VISIBLE);
        holder.imageButtonCardSoundOn.setVisibility(View.VISIBLE);
        holder.imageViewCardAlert.setVisibility(View.INVISIBLE);
        if (status == null) {
            log.warn("sound status is null: unable to update");
            holder.imageButtonCardSoundOff.setVisibility(View.INVISIBLE);
            holder.imageButtonCardSoundOn.setVisibility(View.INVISIBLE);
            holder.imageViewCardAlert.setVisibility(View.VISIBLE);
            return;
        }
        switch (status) {
            case ON: holder.cardViewSound.setCardBackgroundColor(holder.colorStatusOn); break;
            case OFF: holder.cardViewSound.setCardBackgroundColor(holder.colorStatusOff); break;
        }
    }

    private void turnSoundOff(SoundViewHolder holder, SoundModel sound) {
        log.debug("turn sound off {}", sound.getUuid());
        if (sound.getStatus() == null) {
            log.warn("sound status is null: unable to turn off");
            return;
        }

        Action0 updateSoundStatusAction = () -> updateSoundStatus(holder, sound.getStatus());
        soundService.turnOff(sound).doOnCompleted(updateSoundStatusAction).subscribe();
    }

    private void turnSoundOn(SoundViewHolder holder, SoundModel sound) {
        log.debug("turn sound on {}", sound.getUuid());
        if (sound.getStatus() == null) {
            log.warn("sound status is null: unable to turn on");
            return;
        }

        Action0 updateSoundStatusAction = () -> updateSoundStatus(holder, sound.getStatus());
        soundService.turnOn(sound).doOnCompleted(updateSoundStatusAction).subscribe();
    }

    /* commons */

    private void updateDeviceListEvent() {
        EventBus.getDefault().post(new DeviceListFragment.UpdateDeviceListEvent(mEnvironmentId));
    }

    private void updateFavourite(CommonViewHolder holder, boolean favourite) {
        int favouriteDrawable = favourite ? R.drawable.star : R.drawable.star_outline;
        holder.imageButtonCardFavourite.setImageResource(favouriteDrawable);
    }

    private <M extends DomoticModel, S extends DomoticService>
        void onFavouriteChange(CommonViewHolder holder, M model, S service) {

        holder.imageButtonCardFavourite.setOnClickListener(v -> {
            model.setFavourite(!model.isFavourite());
            service.update(model)
                .doOnCompleted(() -> updateFavourite(holder, model.isFavourite()))
                .subscribe();
        });
    }

    private <S extends DomoticService, A extends AbstractDeviceActivity>
        void onMenuClick(CommonViewHolder holder, S service, String uuid, Class<A> activity) {

        Action0 onMenuEdit = () -> {
            Intent intentEdit = new Intent(mContext, activity)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(RealmModel.FIELD_UUID, uuid);
            mContext.startActivity(intentEdit);
        };

        Action0 onMenuDelete = () ->
            service.delete(uuid)
                .doOnCompleted(this::updateDeviceListEvent)
                .subscribe();

        holder.imageButtonCardMenu.setOnClickListener(v ->
            showCardMenu(v, onMenuEdit, onMenuDelete));
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
