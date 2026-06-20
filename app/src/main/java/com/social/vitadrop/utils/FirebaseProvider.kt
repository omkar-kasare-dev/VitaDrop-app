package com.social.vitadrop.utils



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {


    val firestore: FirebaseFirestore by lazy {


        FirebaseFirestore.getInstance().apply {

            useEmulator(
                "172.20.175.225",
                8080
            )
        }
    }

    val auth: FirebaseAuth by lazy {

        FirebaseAuth.getInstance().apply {

            useEmulator(
                "172.20.175.225",
                9099
            )
        }
    }
}