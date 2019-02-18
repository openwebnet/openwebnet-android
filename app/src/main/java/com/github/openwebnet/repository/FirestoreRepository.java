package com.github.openwebnet.repository;

import com.github.openwebnet.model.firestore.ProfileModel;
import com.github.openwebnet.model.firestore.UserModel;
import com.github.openwebnet.model.firestore.UserProfileModel;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import rx.Observable;

public interface FirestoreRepository {

    Observable<Void> updateUser(UserModel user);

    Observable<String> addProfile(UserModel user, String name);

    Observable<List<UserProfileModel>> getProfiles(String userId);

    Observable<ProfileModel> getProfile(DocumentReference profileRef);

    Observable<List<Integer>> applyProfile(ProfileModel profile);

    Observable<Void> renameProfile(String userId, DocumentReference profileRef, String name);

    Observable<Void> shareProfile(String userId, DocumentReference profileRef, String email);

    Observable<Void> deleteProfile(String userId, DocumentReference profileRef);

    Observable<Void> deleteLocalProfile();

    Observable<Void> testQuery(String userId);

}
