package com.github.openwebnet.repository;

import com.github.openwebnet.model.firestore.UserModel;

import rx.Observable;

public interface FirestoreRepository {

    Observable<Void> updateUser(UserModel user);

    Observable<Void> addProfile(UserModel user, String name);

    Observable<Void> shareProfile(String email);

    Observable<Void> softDeleteProfile();

}
