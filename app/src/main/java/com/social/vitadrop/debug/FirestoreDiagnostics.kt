package com.social.vitadrop.debug


import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet6Address
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Date
import java.util.TimeZone
import java.util.concurrent.atomic.AtomicLong

/**
 * TEMPORARY DEBUG TOOL. Delete this file (and the one line in FirestoreCall.kt that calls it)
 * when the problem is fixed.
 *
 * When a Firestore call gets no answer, this checks every link of the chain, one by one:
 *   clock -> app configuration -> sign-in -> DNS -> TCP connection
 * and prints one clear line for each in Logcat (tag FIRESTORE_DIAG).
 */
object FirestoreDiagnostics {

    private const val TAG = "FIRESTORE_DIAG"
    private const val HOST = "firestore.googleapis.com"

    // Four dashboard calls can time out together: report once, not four times
    private val lastReportAt = AtomicLong(0L)

    suspend fun report(reason: String) {

        val now = System.currentTimeMillis()
        val previous = lastReportAt.get()

        if (now - previous < 20_000L || !lastReportAt.compareAndSet(previous, now)) return

        withContext(Dispatchers.IO) {

            Log.w(TAG, "===== FIRESTORE CONNECTIVITY REPORT ($reason) =====")

            // 1) DEVICE CLOCK: a wrong date or time breaks the secure (TLS) connection
            Log.w(TAG, "1) Device time: ${Date()} | time zone: ${TimeZone.getDefault().id}  <- is this the REAL date and time?")

            // 2) WHICH FIREBASE PROJECT AND WHICH SERVER THE APP TALKS TO
            try {
                val options = FirebaseApp.getInstance().options
                Log.w(TAG, "2) Firebase project: ${options.projectId} | appId: ${options.applicationId}")

                val settings = FirebaseFirestore.getInstance().firestoreSettings
                Log.w(
                    TAG,
                    "2) Firestore host: ${settings.host} | ssl: ${settings.isSslEnabled}  " +
                            "<- host must be $HOST. Anything else (for example 10.0.2.2) means the app " +
                            "points to a Firestore EMULATOR that is not running."
                )
            } catch (e: Exception) {
                Log.e(TAG, "2) Could not read the Firebase configuration: ${e.message}")
            }

            // 3) SIGN-IN
            Log.w(TAG, "3) Signed-in user: ${FirebaseAuth.getInstance().currentUser?.uid ?: "NONE (not logged in)"}")

            // 4) DNS: can this phone turn the server name into an address?
            val addresses: List<InetAddress> = try {
                InetAddress.getAllByName(HOST).toList().also { list ->
                    list.forEach {
                        Log.w(
                            TAG,
                            "4) DNS OK: $HOST -> ${it.hostAddress} (${if (it is Inet6Address) "IPv6" else "IPv4"})"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "4) DNS FAILED for $HOST: ${e.javaClass.simpleName}: ${e.message}  " +
                            "<- the phone/emulator cannot resolve the name: network or DNS problem"
                )
                emptyList()
            }

            // 5) TCP: can this phone open a connection to the server on port 443?
            addresses.take(4).forEach { address ->
                try {
                    Socket().use { it.connect(InetSocketAddress(address, 443), 3_000) }
                    Log.w(TAG, "5) TCP 443 OK -> ${address.hostAddress}")
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "5) TCP 443 FAILED -> ${address.hostAddress}: ${e.javaClass.simpleName}: ${e.message}"
                    )
                }
            }

            Log.w(TAG, "===== END OF REPORT. Now read Logcat tag Firestore for the SDK's own reason. =====")
        }
    }
}