package com.github.openwebnet.repository.impl;

import com.annimon.stream.Stream;
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
import com.github.openwebnet.model.firestore.ProfileDetailModel;
import com.github.openwebnet.model.firestore.ProfileModel;
import com.github.openwebnet.model.firestore.UserModel;
import com.github.openwebnet.model.firestore.UserProfileModel;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.WriteBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

public class FirestoreRepositoryImpl implements FirestoreRepository {

    private static final Logger log = LoggerFactory.getLogger(FirestoreRepositoryImpl.class);

    private static boolean DEVELOPMENT = false;

    private static final String ENVIRONMENT = DEVELOPMENT ? "dev_" : "";
    private static final String COLLECTION_USERS = ENVIRONMENT + "users";
    private static final String COLLECTION_USER_PROFILES = "profiles";
    private static final String COLLECTION_PROFILES = ENVIRONMENT + "profiles";

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
            // warning
            .setTimestampsInSnapshotsEnabled(true)
            // cache
            .setPersistenceEnabled(false)
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
                    .addOnSuccessListener(aVoid -> {
                        log.info("user updated with success");
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    })
                    .addOnFailureListener(e -> {
                        log.error("failed to update user", e);
                        subscriber.onError(e);
                    });
            } catch (Exception e) {
                log.error("FirestoreRepository#updateUser", e);
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
                .details(new ProfileDetailModel.Builder()
                    .userId(user.getUserId())
                    .name(name)
                    .build())
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

                // FIRESTORE_SECURITY_RULE: max size
                DocumentReference profileRef = db.collection(COLLECTION_PROFILES).document();
                batch.set(profileRef, profile, SetOptions.merge());

                UserProfileModel userProfile = new UserProfileModel.Builder(profile)
                    .profileRef(profileRef).build();

                DocumentReference userRef = db.collection(COLLECTION_USERS).document(profile.getDetails().getUserId());
                batch.update(userRef, COLLECTION_USER_PROFILES, FieldValue.arrayUnion(userProfile.toMap()));

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
                log.error("FirestoreRepository#addProfile", e);
                subscriber.onError(e);
            }
        });
    }

    private boolean userHasProfiles(DocumentSnapshot document) {
        return document != null &&
            document.exists() &&
            document.getData() != null &&
            document.getData().containsKey(COLLECTION_USER_PROFILES);
    }

    @Override
    public Observable<List<UserProfileModel>> getUserProfiles(String userId) {
        return Observable.create(subscriber -> {
            try {
                getDb()
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .get(Source.DEFAULT)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (userHasProfiles(document)) {

                                List<UserProfileModel> userProfileModels =
                                    Stream.of((List<Map<String, Object>>) document.getData().get(COLLECTION_USER_PROFILES))
                                        .map(userProfileMap -> new UserProfileModel.Builder(userProfileMap).build())
                                        .toList();

                                log.info("user profiles: size={}", userProfileModels.size());

                                subscriber.onNext(userProfileModels);
                                subscriber.onCompleted();
                            } else {
                                log.error("user profiles not found");
                                subscriber.onNext(new ArrayList<>());
                                subscriber.onCompleted();
                            }
                        } else {
                            log.error("failed to get user profiles", task.getException());
                            subscriber.onError(task.getException());
                        }
                    });
            } catch (Exception e) {
                log.error("FirestoreRepository#getUserProfiles", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<ProfileModel> getProfile(DocumentReference profileRef) {
        return Observable.create(subscriber -> {
            try {
                getDb()
                    .collection(COLLECTION_PROFILES)
                    // FIRESTORE_SECURITY_RULE: only owner
                    .document(profileRef.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        log.info("profile model retrieved with success");
                        ProfileModel profileModel = documentSnapshot.toObject(ProfileModel.class);
                        subscriber.onNext(profileModel);
                        subscriber.onCompleted();
                    })
                    .addOnFailureListener(e -> {
                        log.error("failed to retrieve profile model", e);
                        subscriber.onError(e);
                    });

            } catch (Exception e) {
                log.error("FirestoreRepository#getProfile", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<List<Integer>> applyProfile(ProfileModel profile) {

        List<Observable<?>> addAll = Lists.newArrayList(
            automationRepository.addAll(Stream.of(profile.getAutomations())
                .map(automationMap -> new AutomationModel.Builder(automationMap).build()).toList()),
            deviceRepository.addAll(Stream.of(profile.getDevices())
                .map(deviceMap -> new DeviceModel.Builder(deviceMap).build()).toList()),
            energyRepository.addAll(Stream.of(profile.getEnergies())
                .map(energyMap -> new EnergyModel.Builder(energyMap).build()).toList()),
            environmentRepository.addAll(Stream.of(profile.getEnvironments())
                .map(environmentMap -> new EnvironmentModel().fromMap(environmentMap)).toList()),
            gatewayRepository.addAll(Stream.of(profile.getGateways())
                .map(gatewayMap -> new GatewayModel().fromMap(gatewayMap)).toList()),
            ipcamRepository.addAll(Stream.of(profile.getIpcams())
                .map(ipcamMap -> new IpcamModel.Builder(ipcamMap).build()).toList()),
            lightRepository.addAll(Stream.of(profile.getLights())
                .map(lightMap -> new LightModel.Builder(lightMap).build()).toList()),
            scenarioRepository.addAll(Stream.of(profile.getScenarios())
                .map(scenarioMap -> new ScenarioModel.Builder(scenarioMap).build()).toList()),
            soundRepository.addAll(Stream.of(profile.getSounds())
                .map(soundMap -> new SoundModel.Builder(soundMap).build()).toList()),
            temperatureRepository.addAll(Stream.of(profile.getTemperatures())
                .map(temperatureMap -> new TemperatureModel.Builder(temperatureMap).build()).toList())
        );

        // count of each model
        return Observable.zip(addAll, results -> Stream.of(results)
            .map(object -> ((List<?>) object).size()).toList());
    }

    @Override
    public Observable<Void> softDeleteProfile(String userId, DocumentReference profileRef) {
        return getUserProfiles(userId)
            .flatMap(userProfiles -> {
                log.info("soft delete profile: userId={} profileRef={}", userId, profileRef.getPath());

                // remove element from array
                List<UserProfileModel> updatedUserProfiles = Stream
                    .of(userProfiles)
                    .filterNot(userProfile ->
                        userProfile.getProfileRef().getPath().equals(profileRef.getPath()))
                    .toList();

                return updateUserProfile(userId, updatedUserProfiles);
            });
    }

    @Override
    public Observable<Void> shareProfile(String email) {
        // TODO don't expose mapping userId/email
        return null;
    }

    private Observable<Void> updateUserProfile(String userId, List<UserProfileModel> userProfiles) {
        return Observable.create(subscriber -> {
            try {
                log.info("updating user profile: new size={}", userProfiles.size());

                List<Map<String, Object>> userProfilesMap =
                    Stream.of(userProfiles).map(UserProfileModel::toMap).toList();

                getDb()
                    .collection(COLLECTION_USERS)
                    .document(userId)
                    .update(COLLECTION_USER_PROFILES, userProfilesMap)
                    .addOnSuccessListener(aVoid -> {
                        log.info("user profile updated with success");
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    })
                    .addOnFailureListener(e -> {
                        log.error("failed to update user profile", e);
                        subscriber.onError(e);
                    });
            } catch (Exception e) {
                log.error("FirestoreRepository#updateUserProfile", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> deleteLocalProfile() {
        return environmentRepository.deleteAll()
            .flatMap(aVoid -> gatewayRepository.deleteAll());
    }

}
