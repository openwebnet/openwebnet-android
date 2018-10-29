package com.github.openwebnet.repository.impl;

import com.github.openwebnet.model.firestore.ProfileModel;
import com.github.openwebnet.model.firestore.UserModel;
import com.github.openwebnet.repository.FirestoreRepository;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;

/*
 * TODO
 *
 * backups
 * security restrictions
 *
 */
public class FirestoreRepositoryImpl implements FirestoreRepository {

    private static final Logger log = LoggerFactory.getLogger(FirestoreRepositoryImpl.class);

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_PROFILES = "profiles";

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
                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("Firestore#updateUser", e);
                subscriber.onError(e);
            }
        });
    }

    @Override
    public Observable<Void> addProfile(UserModel user, String name) {
        return Observable.create(subscriber -> {
            try {
                FirebaseFirestore db = getDb();
                WriteBatch batch = db.batch();

                ProfileModel profileModel = new ProfileModel.Builder()
                    .name(name)
                    .userId(user.getUserId())
                    .build();

                DocumentReference profileRef = db.collection(COLLECTION_PROFILES).document();
                batch.set(profileRef, profileModel, SetOptions.merge());

                DocumentReference userRef = db.collection(COLLECTION_USERS).document(user.getUserId());
                batch.update(userRef, COLLECTION_PROFILES, FieldValue.arrayUnion(profileRef));

                batch.commit()
                    .addOnSuccessListener(aVoid -> log.info("profile added with success"))
                    .addOnFailureListener(e -> log.error("failed to add profile", e));

                subscriber.onCompleted();
            } catch (Exception e) {
                log.error("Firestore#updateUser", e);
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
