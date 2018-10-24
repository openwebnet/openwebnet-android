package com.github.openwebnet.service

interface FirebaseService {

    fun isAuthenticated(): Boolean

    fun getEmail(): String?

    fun getDisplayName(): String?

    fun getPhotoUrl(): String?

}