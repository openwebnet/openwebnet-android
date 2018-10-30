package com.github.openwebnet.repository.impl;

import com.github.openwebnet.component.Injector;
import com.github.openwebnet.model.AutomationModel;
import com.github.openwebnet.model.LightModel;
import com.github.openwebnet.model.firestore.ProfileModel;
import com.github.openwebnet.model.firestore.UserModel;
import com.github.openwebnet.repository.AutomationRepository;
import com.github.openwebnet.repository.FirestoreRepository;
import com.github.openwebnet.repository.LightRepository;
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
 * replace Date with Timestamp in firestore model
 *
 */
public class FirestoreRepositoryImpl implements FirestoreRepository {

    private static final Logger log = LoggerFactory.getLogger(FirestoreRepositoryImpl.class);

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_PROFILES = "profiles";

    @Inject
    LightRepository lightRepository;

    @Inject
    AutomationRepository automationRepository;

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

        // TODO ipcams, temperatures, energies, lights, automations, scenarios, sounds, devices
        Observable<List<LightModel>> findAllLight = lightRepository.findAll();
        Observable<List<AutomationModel>> findAllAutomation = automationRepository.findAll();

        return Observable.zip(findAllLight, findAllAutomation,
            (lights, automations) ->
                new ProfileModel.Builder()
                    .name(name)
                    .userId(user.getUserId())
                    .lights(lights)
                    //.automations(Lists.newArrayList(automations))
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
