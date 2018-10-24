package com.github.openwebnet.service.impl

import com.github.openwebnet.service.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseServiceImpl : FirebaseService {

    private fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    override fun isAuthenticated(): Boolean {
        return getCurrentUser() != null
    }

    override fun getEmail(): String? {
        return getCurrentUser()?.email
    }

    override fun getDisplayName(): String? {
        return getCurrentUser()?.displayName
    }

    override fun getPhotoUrl(): String? {
        return getCurrentUser()?.photoUrl.toString()
    }

}