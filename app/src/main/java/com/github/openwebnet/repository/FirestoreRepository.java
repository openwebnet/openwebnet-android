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

    Observable<List<UserProfileModel>> getUserProfiles(String userId);

    Observable<ProfileModel> getProfile(DocumentReference profileRef);

    Observable<List<Integer>> applyProfile(ProfileModel profile);

    Observable<Void> renameUserProfile(String userId, DocumentReference profileRef);

    Observable<Void> deleteUserProfile(String userId, DocumentReference profileRef);

    Observable<Void> shareProfile(String userId, DocumentReference profileRef, String email);

    Observable<Void> deleteLocalProfile();

}
