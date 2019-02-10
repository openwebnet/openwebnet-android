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
import com.github.openwebnet.model.firestore.ProfileInfoModel;
import com.github.openwebnet.model.firestore.ProfileModel;
import com.github.openwebnet.model.firestore.ProfileVersionModel;
import com.github.openwebnet.model.firestore.ShareProfileRequest;
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
import com.google.android.gms.tasks.Task;
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
import java.util.Date;
import java.util.HashMap;
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
    private static final String COLLECTION_PROFILES_INFO = ENVIRONMENT + "profiles_info";
    private static final String COLLECTION_SHARE_PROFILE = ENVIRONMENT + "share_profile";
    private static final String COLLECTION_SHARE_PROFILE_REQUESTS = "requests";

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
                log.info("updating user: userId={}", user.getUserId());

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
            ProfileModel.addBuilder()
                .version(ProfileVersionModel.newInstance())
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
            .flatMap(profile -> addProfile(user.getUserId(), name, profile));
    }

    // issue: if there are too many DELETED profiles
    // user might be blocked to add more due to maxProfile restriction
    private Observable<String> addProfile(String userId, String name, ProfileModel profile) {
        return Observable.create(subscriber -> {
            try {
                FirebaseFirestore db = getDb();
                WriteBatch batch = db.batch();

                // add profile/{profileRef}/[ProfileModel + ProfileDetailModel]
                DocumentReference profileRef = db.collection(COLLECTION_PROFILES).document();
                batch.set(profileRef, profile, SetOptions.merge());

                UserProfileModel userProfile = UserProfileModel
                    .addBuilder()
                    .profileRef(profileRef)
                    .name(name)
                    .build();

                // add user/{userId}/.../profiles/[UserProfileModel]
                DocumentReference userRef = db.collection(COLLECTION_USERS).document(userId);
                batch.update(userRef, COLLECTION_USER_PROFILES, FieldValue.arrayUnion(userProfile.toMap()));

                ProfileInfoModel profileInfo = ProfileInfoModel
                    .builder(userProfile)
                    .userId(userId)
                    .build();

                String profileKey = profileRef.getPath().replace(COLLECTION_PROFILES + "/", "");

                // add profiles_info/{profileRef}/[ProfileInfoModel]
                DocumentReference profileInfoRef = db.collection(COLLECTION_PROFILES_INFO).document(profileKey);
                batch.set(profileInfoRef, profileInfo, SetOptions.merge());

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
    public Observable<List<UserProfileModel>> getProfiles(String userId) {
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
                                        .map(userProfileMap -> UserProfileModel.getBuilder(userProfileMap).build())
                                        // filter deleted
                                        .filterNot(userProfile -> userProfile.getStatus() == UserProfileModel.Status.DELETED)
                                        .toList();

                                log.info("active user profiles: size={}", userProfileModels.size());

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
                    .document(profileRef.getId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        log.info("profile retrieved with success");
                        ProfileModel profileModel = documentSnapshot.toObject(ProfileModel.class);
                        subscriber.onNext(profileModel);
                        subscriber.onCompleted();
                    })
                    .addOnFailureListener(e -> {
                        log.error("failed to retrieve profile", e);
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
        ProfileVersionModel version = profile.getVersion();

        List<Observable<?>> addAll = Lists.newArrayList(
            automationRepository.addAll(Stream.of(profile.getAutomations())
                .map(automationMap -> AutomationModel.newInstance(automationMap, version)).toList()),
            deviceRepository.addAll(Stream.of(profile.getDevices())
                .map(deviceMap -> DeviceModel.newInstance(deviceMap, version)).toList()),
            energyRepository.addAll(Stream.of(profile.getEnergies())
                .map(energyMap -> EnergyModel.newInstance(energyMap, version)).toList()),
            environmentRepository.addAll(Stream.of(profile.getEnvironments())
                .map(environmentMap -> EnvironmentModel.newInstance(environmentMap, version)).toList()),
            gatewayRepository.addAll(Stream.of(profile.getGateways())
                .map(gatewayMap -> GatewayModel.newInstance(gatewayMap, version)).toList()),
            ipcamRepository.addAll(Stream.of(profile.getIpcams())
                .map(ipcamMap -> IpcamModel.newInstance(ipcamMap, version)).toList()),
            lightRepository.addAll(Stream.of(profile.getLights())
                .map(lightMap -> LightModel.newInstance(lightMap, version)).toList()),
            scenarioRepository.addAll(Stream.of(profile.getScenarios())
                .map(scenarioMap -> ScenarioModel.newInstance(scenarioMap, version)).toList()),
            soundRepository.addAll(Stream.of(profile.getSounds())
                .map(soundMap -> SoundModel.newInstance(soundMap, version)).toList()),
            temperatureRepository.addAll(Stream.of(profile.getTemperatures())
                .map(temperatureMap -> TemperatureModel.newInstance(temperatureMap, version)).toList())
        );

        // count of each model
        return Observable.zip(addAll, results -> Stream.of(results)
            .map(object -> ((List<?>) object).size()).toList());
    }

    @Override
    public Observable<Void> renameProfile(String userId, DocumentReference profileRef, String name) {
        return getProfiles(userId)
            .flatMap(userProfiles -> {
                log.info("rename user profile: userId={} profileRef={} name={}", userId, profileRef.getPath(), name);

                List<UserProfileModel> updatedUserProfiles = Stream
                    .of(userProfiles)
                    .map(userProfile -> {
                        // update name
                        if (userProfile.getProfileRef().getPath().equals(profileRef.getPath())) {
                            return UserProfileModel
                                .getBuilder(userProfile.toMap())
                                .name(name)
                                .modifiedAt(new Date())
                                .build();
                        }
                        return userProfile;
                    })
                    .toList();

                return updateUserProfile(userId, updatedUserProfiles);
            });
    }

    // immutable append-only
    @Override
    public Observable<Void> shareProfile(String userId, DocumentReference profileRef, String email) {
        return Observable.create(subscriber -> {
            try {
                log.info("share [userId={}|profileRef={}] to email [{}]", userId, profileRef.getPath(), email);

                ShareProfileRequest shareProfileRequest = ShareProfileRequest.addBuilder()
                    .profileRef(profileRef)
                    .email(email)
                    .build();

                DocumentReference requestRef = getDb()
                    .collection(COLLECTION_SHARE_PROFILE)
                    .document(userId);

                Task<Void> updateTask = requestRef.update(COLLECTION_SHARE_PROFILE_REQUESTS, FieldValue.arrayUnion(shareProfileRequest.toMap()));

                updateTask.addOnSuccessListener(aVoid -> {
                    log.info("request created with success (1)");
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                });

                updateTask.addOnFailureListener(e1 -> {
                    if (e1.getMessage().contains("NOT_FOUND")) {
                        Map requestMap = new HashMap<String, Object>();
                        requestMap.put(COLLECTION_SHARE_PROFILE_REQUESTS, Lists.newArrayList(shareProfileRequest.toMap()));

                        requestRef
                            .set(requestMap)
                            .addOnSuccessListener(aVoid -> {
                                log.info("request created with success (2)");
                                subscriber.onNext(null);
                                subscriber.onCompleted();
                            })
                            .addOnFailureListener(e2 -> {
                                log.error("failed to create request (2)", e2);
                                subscriber.onError(e2);
                            });
                    } else {
                        log.error("failed to create request (1)", e1);
                        subscriber.onError(e1);
                    }
                });
            } catch (Exception e) {
                log.error("FirestoreRepository#shareProfile", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> deleteProfile(String userId, DocumentReference profileRef) {
        return getProfiles(userId)
            .flatMap(userProfiles -> {
                log.info("delete user profile: userId={} profileRef={}", userId, profileRef.getPath());

                List<UserProfileModel> updatedUserProfiles = Stream
                    .of(userProfiles)
                    .map(userProfile -> {
                        // update status
                        if (userProfile.getProfileRef().getPath().equals(profileRef.getPath())) {
                            return UserProfileModel
                                .getBuilder(userProfile.toMap())
                                .status(UserProfileModel.Status.DELETED)
                                .modifiedAt(new Date())
                                .build();
                        }
                        return userProfile;
                    })
                    .toList();

                return updateUserProfile(userId, updatedUserProfiles);
            });
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
