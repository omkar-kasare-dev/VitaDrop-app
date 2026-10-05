package com.social.vitadrop.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {

    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
}