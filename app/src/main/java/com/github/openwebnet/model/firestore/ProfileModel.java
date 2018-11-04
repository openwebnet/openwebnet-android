package com.github.openwebnet.model.firestore;

import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.database.DatabaseRealmConfig;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.DeviceModel;
import com.github.openwebnet.model.EnergyModel;
import com.github.openwebnet.model.EnvironmentModel;
import com.github.openwebnet.model.GatewayModel;
import com.github.openwebnet.model.IpcamModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.ScenarioModel;
import com.github.openwebnet.model.SoundModel;
import com.github.openwebnet.model.TemperatureModel;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ProfileModel {

    private String name;

    @ServerTimestamp
    private Date createdAt;

    private String userId;

    private int databaseVersion;

    private int appVersionCode;

    private String appVersionName;

    // FIXME use maps ;-( due to serialization and inheritance issues
    // https://realm.io/docs/java/latest/#other-libraries
    // https://github.com/realm/realm-java/issues/761
    // https://firebase.google.com/docs/firestore/manage-data/add-data
    private List<Map<String, Object>> automations;
    private List<Map<String, Object>> devices;
    private List<Map<String, Object>> energies;
    private List<Map<String, Object>> environments;
    private List<Map<String, Object>> gateways;
    private List<Map<String, Object>> ipcams;
    private List<Map<String, Object>> lights;
    private List<Map<String, Object>> scenarios;
    private List<Map<String, Object>> sounds;
    private List<Map<String, Object>> temperatures;

    // required by Firestore
    public ProfileModel() {}

    private ProfileModel(Builder builder) {
        this.name = builder.name;
        this.userId = builder.userId;
        this.createdAt = builder.createdAt;
        this.databaseVersion = builder.databaseVersion;
        this.appVersionCode = builder.appVersionCode;
        this.appVersionName = builder.appVersionName;

        this.automations = builder.automations;
        this.devices = builder.devices;
        this.energies = builder.energies;
        this.environments = builder.environments;
        this.gateways = builder.gateways;
        this.ipcams = builder.ipcams;
        this.lights = builder.lights;
        this.scenarios = builder.scenarios;
        this.sounds = builder.sounds;
        this.temperatures = builder.temperatures;
    }

    public static class Builder {

        private String name;
        private String userId;
        private Date createdAt;
        private int databaseVersion;
        private int appVersionCode;
        private String appVersionName;

        private List<Map<String, Object>> automations;
        private List<Map<String, Object>> devices;
        private List<Map<String, Object>> energies;
        private List<Map<String, Object>> environments;
        private List<Map<String, Object>> gateways;
        private List<Map<String, Object>> ipcams;
        private List<Map<String, Object>> lights;
        private List<Map<String, Object>> scenarios;
        private List<Map<String, Object>> sounds;
        private List<Map<String, Object>> temperatures;

        public Builder() {
            this.createdAt = new Date();
            this.databaseVersion = DatabaseRealmConfig.DATABASE_VERSION;
            this.appVersionCode = BuildConfig.VERSION_CODE;
            this.appVersionName = BuildConfig.VERSION_NAME;

            this.automations = new ArrayList<>();
            this.devices = new ArrayList<>();
            this.energies = new ArrayList<>();
            this.environments = new ArrayList<>();
            this.gateways = new ArrayList<>();
            this.ipcams = new ArrayList<>();
            this.lights = new ArrayList<>();
            this.scenarios = new ArrayList<>();
            this.sounds = new ArrayList<>();
            this.temperatures = new ArrayList<>();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder databaseVersion(int databaseVersion) {
            this.databaseVersion = databaseVersion;
            return this;
        }

        public Builder appVersionCode(int appVersionCode) {
            this.appVersionCode = appVersionCode;
            return this;
        }

        public Builder databaseVersion(String appVersionName) {
            this.appVersionName = appVersionName;
            return this;
        }

        public Builder automations(List<AutomationModel> automations) {
            this.automations.addAll(Stream.of(automations).map(AutomationModel::toMap).toList());
            return this;
        }

        public Builder devices(List<DeviceModel> devices) {
            this.devices.addAll(Stream.of(devices).map(DeviceModel::toMap).toList());
            return this;
        }

        public Builder energies(List<EnergyModel> energies) {
            this.energies.addAll(Stream.of(energies).map(EnergyModel::toMap).toList());
            return this;
        }

        public Builder environments(List<EnvironmentModel> environments) {
            this.environments.addAll(Stream.of(environments).map(EnvironmentModel::toMap).toList());
            return this;
        }

        public Builder gateways(List<GatewayModel> gateways) {
            this.gateways.addAll(Stream.of(gateways).map(GatewayModel::toMap).toList());
            return this;
        }

        public Builder ipcams(List<IpcamModel> ipcams) {
            this.ipcams.addAll(Stream.of(ipcams).map(IpcamModel::toMap).toList());
            return this;
        }

        public Builder lights(List<LightModel> lights) {
            this.lights.addAll(Stream.of(lights).map(LightModel::toMap).toList());
            return this;
        }

        public Builder scenarios(List<ScenarioModel> scenarios) {
            this.scenarios.addAll(Stream.of(scenarios).map(ScenarioModel::toMap).toList());
            return this;
        }

        public Builder sounds(List<SoundModel> sounds) {
            this.sounds.addAll(Stream.of(sounds).map(SoundModel::toMap).toList());
            return this;
        }

        public Builder temperatures(List<TemperatureModel> temperatures) {
            this.temperatures.addAll(Stream.of(temperatures).map(TemperatureModel::toMap).toList());
            return this;
        }

        public ProfileModel build() {
            checkArgument(!TextUtils.isEmpty(name), "name is empty");
            checkArgument(!TextUtils.isEmpty(userId), "userId is empty");

            checkNotNull(createdAt, "createdAt is null");

            checkArgument(databaseVersion > 0, "databaseVersion is invalid");
            checkArgument(appVersionCode > 0, "appVersionCode is invalid");
            checkArgument(!TextUtils.isEmpty(appVersionName), "appVersionName is empty");

            checkNotNull(automations, "automations is null");
            checkNotNull(devices, "devices is null");
            checkNotNull(energies, "energies is null");
            checkNotNull(environments, "environments is null");
            checkNotNull(gateways, "gateways is null");
            checkNotNull(ipcams, "ipcams is null");
            checkNotNull(lights, "lights is null");
            checkNotNull(scenarios, "scenarios is null");
            checkNotNull(sounds, "sounds is null");
            checkNotNull(temperatures, "temperatures is null");

            return new ProfileModel(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDatabaseVersion() {
        return databaseVersion;
    }

    public void setDatabaseVersion(int databaseVersion) {
        this.databaseVersion = databaseVersion;
    }

    public int getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(int appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public List<Map<String, Object>> getAutomations() {
        return automations;
    }

    public void setAutomations(List<Map<String, Object>> automations) {
        this.automations = automations;
    }

    public List<Map<String, Object>> getDevices() {
        return devices;
    }

    public void setDevices(List<Map<String, Object>> devices) {
        this.devices = devices;
    }

    public List<Map<String, Object>> getEnergies() {
        return energies;
    }

    public void setEnergies(List<Map<String, Object>> energies) {
        this.energies = energies;
    }

    public List<Map<String, Object>> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<Map<String, Object>> environments) {
        this.environments = environments;
    }

    public List<Map<String, Object>> getGateways() {
        return gateways;
    }

    public void setGateways(List<Map<String, Object>> gateways) {
        this.gateways = gateways;
    }

    public List<Map<String, Object>> getIpcams() {
        return ipcams;
    }

    public void setIpcams(List<Map<String, Object>> ipcams) {
        this.ipcams = ipcams;
    }

    public List<Map<String, Object>> getLights() {
        return lights;
    }

    public void setLights(List<Map<String, Object>> lights) {
        this.lights = lights;
    }

    public List<Map<String, Object>> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Map<String, Object>> scenarios) {
        this.scenarios = scenarios;
    }

    public List<Map<String, Object>> getSounds() {
        return sounds;
    }

    public void setSounds(List<Map<String, Object>> sounds) {
        this.sounds = sounds;
    }

    public List<Map<String, Object>> getTemperatures() {
        return temperatures;
    }

    public void setTemperatures(List<Map<String, Object>> temperatures) {
        this.temperatures = temperatures;
    }

}
