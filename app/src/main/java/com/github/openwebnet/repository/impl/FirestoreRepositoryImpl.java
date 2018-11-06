package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
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
import com.github.openwebnet.model.firestore.ProfileModel;
import com.github.openwebnet.model.firestore.UserModel;
import com.github.openwebnet.repository.AutomationRepository;
import com.github.openwebnet.repository.DeviceRepository;
import com.github.openwebnet.repository.EnergyRepository;
import com.github.openwebnet.repository.EnvironmentRepository;
import com.github.openwebnet.repository.FirestoreRepository;
import com.github.openwebnet.repository.GatewayRepository;
import com.github.openwebnet.repository.IpcamRepository;
import com.github.openwebnet.repository.LightRepository;
import com.github.openwebnet.repository.ScenarioRepository;
import com.github.openwebnet.repository.SoundRepository;
import com.github.openwebnet.repository.TemperatureRepository;
import com.google.common.collect.Lists;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

/*
 * TODO
 *
 * backups
 * security restrictions
 * add createdAt/modifiedAt to each model
 * remove dimmer
 *
 */
public class FirestoreRepositoryImpl implements FirestoreRepository {

    private static final Logger log = LoggerFactory.getLogger(FirestoreRepositoryImpl.class);

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_PROFILES = "profiles";

    @Inject
    AutomationRepository automationRepository;

    @Inject
    DeviceRepository deviceRepository;

    @Inject
    EnergyRepository energyRepository;

    @Inject
    EnvironmentRepository environmentRepository;

    @Inject
    GatewayRepository gatewayRepository;

    @Inject
    IpcamRepository ipcamRepository;

    @Inject
    LightRepository lightRepository;

    @Inject
    ScenarioRepository scenarioRepository;

    @Inject
    SoundRepository soundRepository;

    @Inject
    TemperatureRepository temperatureRepository;

    public FirestoreRepositoryImpl() {
        Injector.getApplicationComponent().inject(this);
    }

    private FirebaseFirestore getDb() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build();
        firestore.setFirestoreSettings(settings);
        return firestore;
    }

    @Override
    public Observable<Void> updateUser(UserModel user) {
        return Observable.create(subscriber -> {
            try {
                getDb()
                    .collection(COLLECTION_USERS)
                    .document(user.getUserId())
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> log.info("user updated with success"))
                    .addOnFailureListener(e -> log.error("failed to update user", e));

                subscriber.onNext(null);
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("Firestore#updateUser", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<String> addProfile(UserModel user, String name) {

        List<Observable<?>> findAll = Lists.newArrayList(
            automationRepository.findAll(),
            deviceRepository.findAll(),
            energyRepository.findAll(),
            environmentRepository.findAll(),
            gatewayRepository.findAll(),
            ipcamRepository.findAll(),
            lightRepository.findAll(),
            scenarioRepository.findAll(),
            soundRepository.findAll(),
            temperatureRepository.findAll()
        );

        // Observable.zip support only up to 9 parameters, use Iterable
        return Observable.zip(findAll, results ->
            new ProfileModel.Builder()
                .name(name)
                .userId(user.getUserId())
                .automations((List<AutomationModel>) results[0])
                .devices((List<DeviceModel>) results[1])
                .energies((List<EnergyModel>) results[2])
                .environments((List<EnvironmentModel>) results[3])
                .gateways((List<GatewayModel>) results[4])
                .ipcams((List<IpcamModel>) results[5])
                .lights((List<LightModel>) results[6])
                .scenarios((List<ScenarioModel>) results[7])
                .sounds((List<SoundModel>) results[8])
                .temperatures((List<TemperatureModel>) results[9])
                .build())
            .flatMap(this::addProfile);
    }

    private Observable<String> addProfile(ProfileModel profile) {
        return Observable.create(subscriber -> {
            try {
                FirebaseFirestore db = getDb();
                WriteBatch batch = db.batch();

                DocumentReference profileRef = db.collection(COLLECTION_PROFILES).document();
                batch.set(profileRef, profile, SetOptions.merge());

                DocumentReference userRef = db.collection(COLLECTION_USERS).document(profile.getUserId());
                batch.update(userRef, COLLECTION_PROFILES, FieldValue.arrayUnion(profileRef));

                batch.commit()
                    .addOnSuccessListener(aVoid -> {
                        log.info("profile added with success");
                        subscriber.onNext(profileRef.getPath());
                        subscriber.onCompleted();
                    })
                    .addOnFailureListener(e -> {
                        log.error("failed to add profile", e);
                        subscriber.onError(e);
                    });
            } catch (Exception e) {
                log.error("Firestore#addProfile", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> shareProfile(String email) {
        return null;
    }

    @Override
    public Observable<Void> softDeleteProfile() {
        return null;
    }

}
