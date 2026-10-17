package com.social.vitadrop.data.remote


import android.os.SystemClock
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.social.vitadrop.debug.FirestoreDiagnostics
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException

// While you are debugging: the error on screen names the failing call and says where to look.
// When it is fixed, set this to false: users then see the friendly message again.
private const val SHOW_TECHNICAL_MESSAGE = true

private const val FRIENDLY_MESSAGE =
    "Could not reach the server. Check your internet connection and try again."

/**
 * Runs ONE Firestore call and tells you EXACTLY what happened:
 *   - how long it took
 *   - "NO ANSWER" when Firestore did not reply before the timeout (the old message
 *     "Could not reach the server" meant ONLY this: no reply in 15 seconds)
 *   - the real exception (class, Firestore error code, message, cause) when Firestore
 *     replied with an error, for example PERMISSION_DENIED
 *
 * Filter Logcat with the tag you pass in (for example DASHBOARD_REPO).
 */
internal suspend fun <T : Any> firestoreCall(
    tag: String,
    label: String,
    timeoutMs: Long = 15_000L,
    block: suspend () -> T
): Result<T> {

    val start = SystemClock.elapsedRealtime()
    Log.d(tag, "[$label] started")

    return try {

        val value = withTimeoutOrNull(timeoutMs) { block() }
        val tookMs = SystemClock.elapsedRealtime() - start

        if (value == null) {

            Log.e(
                tag,
                "[$label] OPERATION DID NOT COMPLETE WITHIN " +
                        "$timeoutMs ms. The coroutine timed out before producing a result. " +
                        "Check the stage logs and Firestore diagnostics."
            )

            // TEMPORARY: network report. Remove together with FirestoreDiagnostics.kt.
            FirestoreDiagnostics.report("$label timed out")

            val message = if (SHOW_TECHNICAL_MESSAGE) {
                "No answer from Firestore after ${timeoutMs / 1000}s while loading: $label. " +
                        "See Logcat tags DASHBOARD_REPO / FIRESTORE_DIAG."
            } else {
                FRIENDLY_MESSAGE
            }

            Result.failure(IOException(message))

        } else {

            Log.d(tag, "[$label] OK in $tookMs ms")
            Result.success(value)
        }

    } catch (e: CancellationException) {
        // The screen was closed while the call was running: not an error
        throw e

    } catch (e: Exception) {

        val tookMs = SystemClock.elapsedRealtime() - start
        Log.e(tag, "[$label] FAILED after $tookMs ms -> ${describe(e)}", e)

        Result.failure(e)
    }
}

/** "FirebaseFirestoreException [code=PERMISSION_DENIED]: Missing or insufficient permissions." */
private fun describe(e: Throwable): String {

    val code = (e as? FirebaseFirestoreException)?.code

    val cause = e.cause?.let { " | cause: ${it.javaClass.simpleName}: ${it.message}" }.orEmpty()

    return buildString {
        append(e.javaClass.simpleName)
        if (code != null) append(" [code=$code]")
        append(": ")
        append(e.message)
        append(cause)
    }
}