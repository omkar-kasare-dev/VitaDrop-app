package com.social.vitadrop.domain.usecase

import com.social.vitadrop.domain.model.DashboardData
import com.social.vitadrop.domain.model.RequestModel
import com.social.vitadrop.domain.repository.DashboardRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

// BUSINESS RULE: a request still needs donors unless it was finished or cancelled.
// ASSUMPTION: adjust this list to the status values your app really writes
// ("pending" is the only one created by the app code we have seen).
private val CLOSED_STATUSES = setOf("completed", "cancelled", "closed", "expired", "fulfilled")

private const val MAX_EMERGENCY_ON_DASHBOARD = 10

internal fun RequestModel.isOpen(): Boolean =
    status.trim().lowercase() !in CLOSED_STATUSES

/**
 * Loads everything the dashboard shows, in PARALLEL (before: 5 calls one after another).
 * If any part fails, the whole result is a failure so the screen can show an error + Retry
 * (before: failures were hidden and the dashboard silently showed zeros).
 */
class GetDashboardUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardData> = coroutineScope {

        val donors = async { repository.getDonorCount() }
        val hospitals = async { repository.getHospitalCount() }
        val requests = async { repository.getRequestCount() }
        val emergency = async { repository.getEmergencyRequests() }

        val donorsCount = donors.await()
            .getOrElse { return@coroutineScope Result.failure<DashboardData>(it) }
        val hospitalsCount = hospitals.await()
            .getOrElse { return@coroutineScope Result.failure<DashboardData>(it) }
        val requestsCount = requests.await()
            .getOrElse { return@coroutineScope Result.failure<DashboardData>(it) }
        val emergencyRequests = emergency.await()
            .getOrElse { return@coroutineScope Result.failure<DashboardData>(it) }

        Result.success(
            DashboardData(
                donorsCount = donorsCount,
                hospitalsCount = hospitalsCount,
                requestsCount = requestsCount,
                emergencyRequests = emergencyRequests
                    .filter { it.isOpen() }
                    .take(MAX_EMERGENCY_ON_DASHBOARD)
            )
        )
    }
}

/** Open (not completed / cancelled) requests for the "Blood Requests" list screen. */
class GetOpenRequestsUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(): Result<List<RequestModel>> =
        repository.getAllRequests().map { requests -> requests.filter { it.isOpen() } }
}