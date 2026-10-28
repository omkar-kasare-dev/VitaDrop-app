package com.social.vitadrop.presentation.components


import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/** "26 Oct, 6:30 PM": when the blood is needed by. */
fun Timestamp.toNeededByText(): String =
    SimpleDateFormat("d MMM, h:mm a", Locale.getDefault()).format(toDate())